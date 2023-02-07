package io.github.nfdz.cryptool.ui.platform

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.*
import com.google.zxing.common.BitMatrix
import com.google.zxing.common.HybridBinarizer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface QrProvider {
    suspend fun encode(data: String, sizeInPx: Int): Bitmap?
    suspend fun decode(image: Bitmap): String?
}


internal class QrProviderAndroid : QrProvider {

    companion object {
        private const val tag = "QrProvider"
        private const val dataPixelColor = Color.BLACK
        private const val emptyPixelColor = Color.WHITE
    }

    override suspend fun encode(data: String, sizeInPx: Int): Bitmap? = withContext(Dispatchers.Default) {
        runCatching {
            val hints = mapOf<EncodeHintType, Any>(EncodeHintType.MARGIN to "0")
            val matrix = MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, sizeInPx, sizeInPx, hints)
            val pixelArray = matrix.toPixelArray()
            Bitmap.createBitmap(sizeInPx, sizeInPx, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixelArray, 0, sizeInPx, 0, 0, sizeInPx, sizeInPx)
            }
        }.onFailure {
            Napier.e(tag = tag, message = "Error encoding QR", throwable = it)
        }.getOrNull()
    }

    override suspend fun decode(image: Bitmap): String? = withContext(Dispatchers.Default) {
        runCatching {
            val pixels = IntArray(image.width * image.height)
            image.getPixels(pixels, 0, image.width, 0, 0, image.width, image.height)
            val source = RGBLuminanceSource(image.width, image.height, pixels)
            val bitmap = BinaryBitmap(HybridBinarizer(source))
            MultiFormatReader().decode(bitmap).text
        }.onFailure {
            Napier.e(tag = tag, message = "Error decoding QR", throwable = it)
        }.getOrNull()
    }

    private fun BitMatrix.toPixelArray(): IntArray {
        val w = width
        val h = height
        val array = IntArray(w * h)
        for (y in 0 until h) {
            val offset = y * w
            for (x in 0 until w) {
                array[offset + x] = if (this[x, y]) dataPixelColor else emptyPixelColor
            }
        }
        return array
    }
}