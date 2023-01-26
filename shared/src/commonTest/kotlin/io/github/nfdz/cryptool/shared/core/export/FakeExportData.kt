package io.github.nfdz.cryptool.shared.core.export

class FakeExportData(
    val prepareDataDtoAnswer: Any? = null,
) : ExportData {
    override suspend fun prepareData(configuration: ExportConfiguration): String {
        TODO("Not yet implemented")
    }

    var prepareDataDtoCount = 0
    override suspend fun prepareDataDto(): Any {
        prepareDataDtoCount++
        return prepareDataDtoAnswer!!
    }
}