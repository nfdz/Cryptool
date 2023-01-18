package io.github.nfdz.cryptool.shared.core.import

class FakeImportDataManager(
    val consumeDataDtoError: Throwable? = null,
) : ImportDataManager {
    override suspend fun consumeDataV1(data: String, configuration: ImportConfiguration): ImportResult {
        TODO("Not yet implemented")
    }

    override suspend fun consumeDataV2(data: String, configuration: ImportConfiguration): ImportResult {
        TODO("Not yet implemented")
    }

    var consumeDataDtoCount = 0
    var consumeDataDtoArgData: Any? = null
    override suspend fun consumeDataDto(data: Any) {
        consumeDataDtoCount++
        consumeDataDtoArgData = data
        consumeDataDtoError?.let { throw it }
    }
}