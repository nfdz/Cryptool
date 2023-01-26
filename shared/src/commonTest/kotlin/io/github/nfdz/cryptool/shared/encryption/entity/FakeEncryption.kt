package io.github.nfdz.cryptool.shared.encryption.entity

object FakeEncryption {
    fun mock(id: String = "1"): Encryption {
        return Encryption(
            id,
            "Conversation $id",
            "test $id",
            AlgorithmVersion.V2,
            MessageSource.Manual,
            false,
            0,
            "",
            0L,
        )
    }
}