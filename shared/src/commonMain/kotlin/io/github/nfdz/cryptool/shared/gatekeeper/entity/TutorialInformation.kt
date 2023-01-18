package io.github.nfdz.cryptool.shared.gatekeeper.entity

data class TutorialInformation(
    val title: String,
    val messages: List<String>,
) {
    companion object {
        const val defaultPassword: String = "123456789"
    }
}