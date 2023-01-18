package io.github.nfdz.cryptool.shared.password.entity

object FakePassword {
    fun mock(id: String = "A"): Password {
        return Password(
            id = "$id",
            name = "Test $id",
            password = "Password $id",
            tags = setOf("tag-${id.lowercase()}")
        )
    }
}