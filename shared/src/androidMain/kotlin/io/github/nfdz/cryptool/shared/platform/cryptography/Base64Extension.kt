package io.github.nfdz.cryptool.shared.platform.cryptography

import android.util.Base64

actual fun ByteArray.encodeBase64(): String = Base64.encodeToString(this, base64Flags)
actual fun String.decodeBase64(): ByteArray = Base64.decode(this, base64Flags)

private const val base64Flags = Base64.NO_PADDING or Base64.NO_WRAP or Base64.NO_CLOSE or Base64.URL_SAFE