package io.github.nfdz.cryptool.shared.password.entity

data class Password(
    val id: String,
    val name: String,
    val password: String,
    val tags: Set<String>,
) {
    companion object {
        fun splitTags(value: String): Set<String> {
            return value.split(",")
                .map { it.trim().lowercase() }
                .filterNot { it.isBlank() }
                .toSet()
        }

        fun joinTags(tags: Set<String>): String = tags.joinToString(separator = ", ")
    }
}