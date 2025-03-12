
package com.abdownloadmanager.shared.remote.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdownloadmanager.shared.remote.RemoteConnectionService

@Composable
expect fun rememberQRBitmap(content: String): ImageBitmap

@Composable
fun QRCodeGenerateView(
    connectionService: RemoteConnectionService,
    modifier: Modifier = Modifier
) {
    val connectionData = remember { connectionService.generateConnectionQR() }
    val (id, pass) = remember(connectionData) { connectionData.split('|') }
    
    val qrBitmap = rememberQRBitmap(connectionData)

    Card(
        modifier = modifier.padding(16.dp),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Scan with another device",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.White)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = qrBitmap,
                    contentDescription = "QR Code for remote connection",
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Connection ID: $id", fontSize = 16.sp)
            Text("Password: $pass", fontSize = 16.sp)
            
            Text(
                "Share this information with the device you want to connect to",
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
