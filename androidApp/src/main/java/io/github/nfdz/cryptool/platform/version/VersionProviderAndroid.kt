package io.github.nfdz.cryptool.platform.version

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import androidx.annotation.RequiresApi
import io.github.aakira.napier.Napier
import io.github.nfdz.cryptool.BuildConfig
import io.github.nfdz.cryptool.shared.platform.storage.KeyValueStorage
import io.github.nfdz.cryptool.shared.platform.version.CertificateState
import io.github.nfdz.cryptool.shared.platform.version.VersionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

class VersionProviderAndroid(
    private val context: Context,
    private val storage: KeyValueStorage,
) : VersionProvider {

    companion object {
        private const val versionKey = "version"
        private const val notifiedRemoteVersionKey = "notified_remote_version"
        private const val certificateTagRegex = "<sign(?:\\s.*?!/)?>(.*?)</sign\\s*>"
    }

    override val appVersion: Int
        get() = BuildConfig.VERSION_CODE

    private val appVersionName: String
        get() = BuildConfig.VERSION_NAME

    override var storedVersion: Int
        get() = storage.getInt(versionKey, VersionProvider.noVersion)
        set(value) = storage.putInt(versionKey, value)

    private val notifiedRemoteVersion: String
        get() = storage.getString(notifiedRemoteVersionKey) ?: ""

    override fun setNotifiedRemoteVersion(version: String) {
        storage.putString(notifiedRemoteVersionKey, version)
    }

    override suspend fun verifyCertificateRemote(): CertificateState = withContext(Dispatchers.IO) {
        getAppCertificate() // Precache the certificate in any case
        if (BuildConfig.VALIDATE_CERTIFICATE_GITHUB) {
            val body = remoteDataDto?.body
            if (body.isNullOrBlank()) return@withContext CertificateState.UNKNOWN
            val remoteCertificate = Regex(certificateTagRegex).find(body)?.value
            if (remoteCertificate.isNullOrBlank()) return@withContext CertificateState.UNKNOWN
            if (apkCertificate == remoteCertificate) CertificateState.VALID
            else CertificateState.INVALID
        } else {
            CertificateState.IGNORE
        }
    }

    override suspend fun getAppCertificate(): String? = withContext(Dispatchers.IO) {
        apkCertificate
    }

    override suspend fun getRemoteNewVersion(): String? = withContext(Dispatchers.IO) {
        if (BuildConfig.CHECK_NEW_VERSION_GITHUB) {
            val remoteVersion = remoteDataDto?.name
            val differentVersion = appVersionName != remoteVersion
            val notNotified = notifiedRemoteVersion != remoteVersion
            if (differentVersion && notNotified) remoteVersion else null
        } else {
            null
        }
    }

    // Disclaimer: this is the only internet request of the application. Feel free to delete it.
    private val remoteDataDto: RemoteInfoDto? by lazy {
        runCatching {
            val connection = URL(VersionProvider.latestGithubVersonApi).openConnection() as HttpURLConnection
            val remoteInfoDto: RemoteInfoDto
            try {
                val data = connection.inputStream.bufferedReader().use { it.readText() }
                val dataJson = Json.parseToJsonElement(data)
                remoteInfoDto = RemoteInfoDto(
                    name = dataJson.jsonObject["name"]!!.jsonPrimitive.content,
                    body = dataJson.jsonObject["body"]!!.jsonPrimitive.content,
                )
            } finally {
                connection.disconnect()
            }
            remoteInfoDto
        }.onFailure {
            Napier.e(
                tag = "VersionProvider",
                message = "Error fetching remoteDataDto: ${it.message}",
                throwable = it
            )
        }.getOrNull()
    }

    private val apkCertificate: String? by lazy {
        runCatching {
            val certs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                    .getCertificatesNew()
            } else {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
                    .getCertificateOld()
            }
            if (certs.size != 1) {
                throw IllegalStateException("Unexpected certificates number: ${certs.size}")
            }
            certs.first()
        }.onFailure {
            Napier.e(
                tag = "VersionProvider",
                message = "Error reading APK Certificates: ${it.message}",
                throwable = it
            )
        }.getOrNull()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun PackageInfo.getCertificatesNew(): List<String> {
        if (signingInfo == null) {
            throw IllegalStateException("No PackageInfo.signingInfo")
        }
        return if (signingInfo.hasMultipleSigners()) {
            signatureDigest(signingInfo.apkContentsSigners);
        } else {
            signatureDigest(signingInfo.signingCertificateHistory);
        }
    }

    private fun PackageInfo.getCertificateOld(): List<String> {
        if (signatures == null || signatures.size == 0 || signatures[0] == null) {
            throw IllegalStateException("No PackageInfo.signatures")
        }
        return signatureDigest(signatures)
    }

    private fun signatureDigest(sig: Signature): String {
        val signature: ByteArray = sig.toByteArray()
        return signature.toSha1()
    }

    private fun signatureDigest(sigList: Array<Signature>): List<String> {
        return sigList.map {
            signatureDigest(it)
        }
    }

    private fun ByteArray.toSha1(): String {
        val digest = MessageDigest.getInstance("SHA-1")
        val result = digest.digest(this)
        return result.joinToString("") { "%02x".format(it) }
    }
}

private data class RemoteInfoDto(
    val name: String,
    val body: String,
)