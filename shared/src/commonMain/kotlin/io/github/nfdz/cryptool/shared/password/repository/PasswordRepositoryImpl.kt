package io.github.nfdz.cryptool.shared.password.repository

import io.github.nfdz.cryptool.shared.core.realm.RealmGateway
import io.github.nfdz.cryptool.shared.password.entity.Password
import io.github.nfdz.cryptool.shared.password.repository.realm.PasswordRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class PasswordRepositoryImpl(
    private val realmGateway: RealmGateway,
) : PasswordRepository {

    private val realm: Realm
        get() = realmGateway.instance

    override fun getAll(): List<Password> {
        return realm.query<PasswordRealm>().find().map {
            it.toEntity()
        }
    }

    override suspend fun addAll(passwords: List<Password>) {
        realm.write {
            passwords.forEach {
                copyToRealm(
                    PasswordRealm().apply {
                        id = it.id
                        name = it.name
                        password = it.password
                        tags = Password.joinTags(it.tags)
                    }, UpdatePolicy.ALL
                )
            }
        }
    }

    override suspend fun observe(): Flow<List<Password>> {
        return realm.query<PasswordRealm>().asFlow().transform { value ->
            emit(value.list.map { it.toEntity() })
        }
    }

    override suspend fun create(name: String, password: String, tags: String): Password {
        val entry = PasswordRealm.create(
            name = name,
            password = password,
            tags = tags,
        )
        return realm.write {
            copyToRealm(entry)
        }.toEntity()
    }

    override suspend fun edit(passwordToEdit: Password, name: String, password: String, tags: String): Password {
        return realm.write {
            query<PasswordRealm>("id == '${passwordToEdit.id}'").find().first().apply {
                this.name = name
                this.password = password
                this.tags = tags
            }
        }.toEntity()
    }

    override suspend fun remove(id: String) {
        realm.write {
            delete(query<PasswordRealm>("id == '$id'").find().first())
        }
    }
}