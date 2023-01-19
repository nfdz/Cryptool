package io.github.nfdz.cryptool.shared.message.entity

object FakeMessage {
    fun mock(id: String = "1"): Message {
        return Message(
            id = id,
            encryptionId = "$id",
            message = "Hello $id",
            encryptedMessage = "fwofwffklr",
            timestampInMillis = 987688696768,
            ownership = MessageOwnership.OTHER,
            isFavorite = false,
        )
    }
}