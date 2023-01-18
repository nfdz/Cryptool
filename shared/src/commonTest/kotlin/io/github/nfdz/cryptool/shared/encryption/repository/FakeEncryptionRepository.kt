package io.github.nfdz.cryptool.shared.encryption.repository

import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeEncryptionRepository(
    val getAllAnswer: List<Encryption> = emptyList(),
    val observeAnswer: Flow<List<Encryption>> = flow { },
    val createAnswer: Encryption? = null,
    val editAnswer: Encryption? = null,
    val observeWithIdAnswer: Flow<Encryption> = flow { },
) : EncryptionRepository {

    var getAllCount = 0
    override fun getAll(): List<Encryption> {
        getAllCount++
        return getAllAnswer
    }

    var addAllRegistry = mutableListOf<List<Encryption>>()
    override suspend fun addAll(encryptions: List<Encryption>) {
        addAllRegistry.add(encryptions)
    }

    var observeCount = 0
    override suspend fun observe(): Flow<List<Encryption>> {
        observeCount++
        return observeAnswer
    }

    var observeWithIdCount = 0
    var observeWithIdArg: String? = null
    override suspend fun observe(id: String): Flow<Encryption> {
        delay(50)
        observeWithIdCount++
        observeWithIdArg = id
        return observeWithIdAnswer
    }

    var createCount = 0
    var createArgName: String? = null
    var createArgPassword: String? = null
    var createArgAlgorithm: AlgorithmVersion? = null
    override suspend fun create(name: String, password: String, algorithm: AlgorithmVersion): Encryption {
        createCount++
        createArgName = name
        createArgPassword = password
        createArgAlgorithm = algorithm
        return createAnswer!!
    }

    var editCount = 0
    var editArgName: String? = null
    var editArgPassword: String? = null
    var editArgAlgorithm: AlgorithmVersion? = null
    var editArgEncryptionToEdit: Encryption? = null
    override suspend fun edit(
        encryptionToEdit: Encryption,
        name: String,
        password: String,
        algorithm: AlgorithmVersion,
    ): Encryption {
        editCount++
        editArgEncryptionToEdit = encryptionToEdit
        editArgName = name
        editArgPassword = password
        editArgAlgorithm = algorithm
        return editAnswer!!
    }

    var deleteCount = 0
    var deleteArgIds: Set<String>? = null
    override suspend fun delete(ids: Set<String>) {
        deleteCount++
        deleteArgIds = ids
    }

    var setFavoriteCount = 0
    var setFavoriteArgIds: Set<String>? = null
    override suspend fun setFavorite(ids: Set<String>) {
        setFavoriteCount++
        setFavoriteArgIds = ids
    }

    var unsetFavoriteCount = 0
    var unsetFavoriteArgIds: Set<String>? = null
    override suspend fun unsetFavorite(ids: Set<String>) {
        unsetFavoriteCount++
        unsetFavoriteArgIds = ids
    }

    var setSourceCount = 0
    var setSourceArgId: String? = null
    var setSourceArgSource: MessageSource? = null
    override suspend fun setSource(id: String, source: MessageSource?) {
        delay(50)
        setSourceCount++
        setSourceArgId = id
        setSourceArgSource = source
    }
}