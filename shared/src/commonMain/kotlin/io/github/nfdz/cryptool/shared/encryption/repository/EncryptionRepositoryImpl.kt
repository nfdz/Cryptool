package io.github.nfdz.cryptool.shared.encryption.repository

import io.github.nfdz.cryptool.shared.core.realm.RealmGateway
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.entity.serialize
import io.github.nfdz.cryptool.shared.encryption.repository.realm.EncryptionRealm
import io.github.nfdz.cryptool.shared.message.entity.MessageOwnership
import io.github.nfdz.cryptool.shared.message.repository.realm.MessageRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class EncryptionRepositoryImpl(
    private val realmGateway: RealmGateway,
) : EncryptionRepository {

    private val realm: Realm
        get() = realmGateway.instance

    override fun getAll(): List<Encryption> {
        return realm.query<EncryptionRealm>().find().map { it.toEntity() }
    }

    override fun getAllWith(source: MessageSource): List<Encryption> {
        return realm.query<EncryptionRealm>("source == '${source.serialize()}'").find().map { it.toEntity() }
    }

    override suspend fun addAll(encryptions: List<Encryption>) {
        realm.write {
            encryptions.forEach {
                copyToRealm(
                    EncryptionRealm().apply {
                        id = it.id
                        name = it.name
                        password = it.password
                        algorithm = it.algorithm.name
                        source = it.source?.serialize() ?: ""
                        isFavorite = it.isFavorite
                        unreadMessagesCount = it.unreadMessagesCount
                        lastMessage = it.lastMessage
                        lastMessageTimestamp = it.lastMessageTimestamp
                    }, UpdatePolicy.ALL
                )
            }
        }
    }

    override suspend fun observe(): Flow<List<Encryption>> {
        return realm.query<EncryptionRealm>().asFlow().transform { value ->
            emit(value.list.map { it.toEntity() })
        }
    }

    override suspend fun observe(id: String): Flow<Encryption> {
        return realm.query<EncryptionRealm>("id == '${id}'").first().asFlow().transform { value ->
            value.obj?.let { emit(it.toEntity()) }
        }
    }

    override suspend fun create(name: String, password: String, algorithm: AlgorithmVersion): Encryption {
        val entry = EncryptionRealm.create(
            name = name,
            password = password,
            algorithm = algorithm,
        )
        return realm.write {
            copyToRealm(entry)
        }.toEntity()
    }

    override suspend fun edit(
        encryptionToEdit: Encryption,
        name: String,
        password: String,
        algorithm: AlgorithmVersion,
    ): Encryption {
        val diff = StringBuilder().apply {
            if (encryptionToEdit.name != name) {
                this.append("Name: '${encryptionToEdit.name}' ➡️ '$name'\n")
            }
            if (encryptionToEdit.password != password) {
                this.append("Password: '${encryptionToEdit.password}' ➡️ '${password.hideSensitive()}'\n")
            }
            if (encryptionToEdit.algorithm != algorithm) {
                this.append("Algorithm: '${encryptionToEdit.algorithm.name}' ➡️ '${algorithm.name}'\n")
            }
        }.toString().trim()
        return realm.write {
            val editedEntry = query<EncryptionRealm>("id == '${encryptionToEdit.id}'").find().first().apply {
                this.name = name
                this.password = password
                this.algorithm = algorithm.name
            }
            copyToRealm(
                MessageRealm.create(
                    encryptionId = encryptionToEdit.id,
                    message = diff,
                    encryptedMessage = "",
                    ownership = MessageOwnership.SYSTEM,
                )
            )
            editedEntry
        }.toEntity()
    }

    override suspend fun delete(ids: Set<String>) {
        realm.write {
            ids.forEach {
                delete(query<MessageRealm>("encryptionId == '${it}'").find())
                delete(query<EncryptionRealm>("id == '${it}'").find().first())
            }
        }
    }

    override suspend fun setFavorite(ids: Set<String>) = setFavorite(ids, true)

    override suspend fun unsetFavorite(ids: Set<String>) = setFavorite(ids, false)

    private suspend fun setFavorite(ids: Set<String>, state: Boolean) {
        return realm.write {
            ids.forEach {
                query<EncryptionRealm>("id == '${it}'").find().first().apply {
                    this.isFavorite = state
                }
            }
        }
    }

    override suspend fun setSource(id: String, source: MessageSource?) {
        if (id.isBlank()) return
        val serializedSource = source?.serialize() ?: ""
        realm.write {
            query<EncryptionRealm>("id == '${id}'").find().first().apply {
                if (source != null && source.exclusive) {
                    // avoid collisions when the source is exclusive
                    val collision =
                        query<EncryptionRealm>("password == '${password}' AND source == '$serializedSource'").find()
                            .isNotEmpty()
                    if (collision) throw ExclusiveSourceCollisionException()
                }
                this.source = serializedSource
            }
            if (source != null) {
                copyToRealm(
                    MessageRealm.create(
                        encryptionId = id,
                        message = "Message source: $serializedSource",
                        encryptedMessage = "",
                        ownership = MessageOwnership.SYSTEM,
                    )
                )
            }
        }
    }

    override suspend fun acknowledgeUnreadMessages(id: String) {
        if (id.isBlank()) return
        realm.write {
            query<EncryptionRealm>("id == '${id}'").find().first().apply {
                this.unreadMessagesCount = 0
            }
        }
    }

    private fun String.hideSensitive(): String = '\u2022'.toString().repeat(this.length)
}

class ExclusiveSourceCollisionException : Exception()
