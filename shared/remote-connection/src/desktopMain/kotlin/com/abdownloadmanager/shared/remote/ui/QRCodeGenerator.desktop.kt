
package com.abdownloadmanager.shared.remote.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.awt.image.BufferedImage
import com.google.zxing.client.j2se.MatrixToImageWriter

@Composable
actual fun rememberQRBitmap(content: String): ImageBitmap {
    return remember(content) {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix)
        bufferedImage.asImageBitmap()
    }
}
