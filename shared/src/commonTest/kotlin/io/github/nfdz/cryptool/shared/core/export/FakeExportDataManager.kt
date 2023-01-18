package io.github.nfdz.cryptool.shared.core.export

class FakeExportDataManager(
    val prepareDataDtoAnswer: Any? = null,
) : ExportDataManager {
    override suspend fun prepareData(configuration: ExportConfiguration): String {
        TODO("Not yet implemented")
    }

    var prepareDataDtoCount = 0
    override suspend fun prepareDataDto(): Any {
        prepareDataDtoCount++
        return prepareDataDtoAnswer!!
    }
}