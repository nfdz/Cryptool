package io.github.nfdz.cryptool.shared.platform.network

import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.shared.encryption.repository.EncryptionRepository
import io.github.nfdz.cryptool.shared.gatekeeper.repository.GatekeeperRepository
import io.github.nfdz.cryptool.shared.message.repository.MessageRepository
import io.github.nfdz.cryptool.shared.message.viewModel.MessageAction
import io.github.nfdz.cryptool.shared.message.viewModel.MessageViewModel
import io.github.nfdz.cryptool.shared.platform.localization.LocalizedError
import io.github.nfdz.cryptool.shared.platform.storage.KeyValueStorage
import io.github.nfdz.cryptool.shared.platform.time.Clock
import kotlinx.coroutines.*
import java.net.BindException
import java.net.ServerSocket
import java.net.SocketTimeoutException
import kotlin.random.Random

class LanReceiverAndroid(
    private val localizedError: LocalizedError,
    private val messageViewModel: MessageViewModel,
    private val encryptionRepository: EncryptionRepository,
    private val gatekeeperRepository: GatekeeperRepository,
    private val messageRepository: MessageRepository,
    private val storage: KeyValueStorage,
) : LanReceiver, CoroutineScope by CoroutineScope(Dispatchers.IO) {

    companion object {
        private const val tag = "LanReceiver"
        private const val lastPortKey = "cryptool_lan_receiver_last_port"
        private const val serverRetryPeriodInMillis = 5_000L
        private const val serverTimeoutInMillis = 40_000
        private const val noPort = -1
    }

    private var serverJob: Job? = null

    init {
        encryptionRepository.addOnSetSourceAction { source ->
            if (source is MessageSource.Lan) {
                ensureServerIsUp()
            }
        }
    }

    private fun ensureServerIsUp() {
        serverJob?.cancel()
        serverJob = launch {
            val jobId = serverJob?.hashCode().toString()
            Napier.d(tag = tag, message = "[$jobId] Launch server")
            do {
                setupServer()
            } while (isServerNeeded())
            Napier.d(tag = tag, message = "LAN server is not needed anymore")
        }
    }

    private fun isServerNeeded(): Boolean = runCatching {
        encryptionRepository.getAllWith(MessageSource.lanPrefix).isNotEmpty()
    }.getOrElse { false }

    private suspend fun setupServer() = runCatching {
        ServerSocket(getPort()).use { serverSocket ->
            Napier.d(tag = tag, message = "Server listening: ${serverSocket.inetAddress} | ${serverSocket.localPort}")
            serverSocket.soTimeout = serverTimeoutInMillis
            serverSocket.accept().use { clientSocket ->
                val receivedText = clientSocket.getInputStream().bufferedReader().use { it.readText() }
                if (gatekeeperRepository.isOpen()) {
                    processIncomingText(receivedText)
                }
            }
        }
    }.onFailure {
        when (it) {
            is SocketTimeoutException -> Unit
            is BindException -> {
                Napier.e(tag = tag, message = "Bind error, reset port", throwable = it)
                storage.putInt(lastPortKey, noPort)
                delay(serverRetryPeriodInMillis)
            }
            else -> {
                Napier.e(tag = tag, message = "Error: ${it.message}", throwable = it)
                delay(serverRetryPeriodInMillis)
            }
        }
    }

    private suspend fun processIncomingText(receivedText: String) = runCatching {
        Napier.d(tag = tag, message = "Received message: '${receivedText}'")
        val parts = receivedText.split(",")
        val slot = parts[0]
        val encryptedMessage = parts[1]
        val encryption = findEncryption(slot)

        messageRepository.receiveMessageAsync(
            encryption = encryption,
            encryptedMessage = encryptedMessage,
            timestampInMillis = Clock.nowInMillis(),
        )
    }.onFailure {
        Napier.e(tag = tag, message = "Cannot process incoming text", throwable = it)
        messageViewModel.dispatch(MessageAction.Event(localizedError.messageReceiveLanError))
    }

    private fun findEncryption(slot: String): Encryption {
        return encryptionRepository.getAllWith(MessageSource.lanPrefix).first {
            val lanSource = it.source as MessageSource.Lan
            lanSource.slot == slot
        }
    }

    override fun getFreeSlot(): Int {
        val maxSlot: Int = encryptionRepository.getAllWith(MessageSource.lanPrefix)
            .mapNotNull { it.source as? MessageSource.Lan }
            .maxByOrNull { it.slot }
            ?.slot?.toInt() ?: 0
        return maxSlot + 1
    }

    override fun getPort(): Int {
        var lastPort = storage.getInt(lastPortKey, noPort)
        if (lastPort < 0) {
            lastPort = randomPort()
            storage.putInt(lastPortKey, lastPort)
        }
        Napier.d(tag = tag, message = "Port=$lastPort")
        return lastPort
    }

    // IANA recommends 49152-65535 for Ephemeral Ports
    private fun randomPort() = Random.nextInt(49152, 65535)

}