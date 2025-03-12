
package com.abdownloadmanager.desktop.pages.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.abdownloadmanager.shared.app.utils.*
import com.abdownloadmanager.shared.remote.RemoteConnectionService
import com.abdownloadmanager.shared.remote.ui.QRCodeGenerateView
import org.koin.compose.koinInject

@Composable
fun RemoteConnectionPage() {
    val connectionService = koinInject<RemoteConnectionService>()
    val connectionState by connectionService.connectionState.collectAsState()
    
    var showQRCode by remember { mutableStateOf(false) }
    var connectionId by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            "Remote Connection",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Connection Status
        when (val state = connectionState) {
            is RemoteConnectionService.ConnectionState.Connected -> {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colors.primary
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Connected to remote device", style = MaterialTheme.typography.subtitle1)
                            Text("Connection ID: ${state.remoteId}", style = MaterialTheme.typography.caption)
                        }
                        Spacer(Modifier.weight(1f))
                        Button(onClick = { connectionService.disconnect() }) {
                            Text("Disconnect")
                        }
                    }
                }
            }
            is RemoteConnectionService.ConnectionState.Connecting -> {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Connecting...", style = MaterialTheme.typography.subtitle1)
                    }
                }
            }
            is RemoteConnectionService.ConnectionState.Error -> {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    backgroundColor = MaterialTheme.colors.error.copy(alpha = 0.1f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colors.error
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(state.message, style = MaterialTheme.typography.subtitle1)
                    }
                }
            }
            else -> { /* Not connected, show nothing */ }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (showQRCode) {
            QRCodeGenerateView(
                connectionService = connectionService,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { showQRCode = false },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Hide QR Code")
            }
        } else {
            // Connect Form
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Connect to Remote Device",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    OutlinedTextField(
                        value = connectionId,
                        onValueChange = { connectionId = it },
                        label = { Text("Connection ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                    
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showQRCode = true },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Generate QR Code")
                        }
                        
                        Button(
                            onClick = { 
                                connectionService.connectToRemoteDevice(
                                    connectionId.text, 
                                    password.text
                                )
                            },
                            enabled = connectionId.text.isNotEmpty() && password.text.isNotEmpty() &&
                                    connectionState !is RemoteConnectionService.ConnectionState.Connecting
                        ) {
                            Text("Connect")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Paste connection code section
            OutlinedButton(
                onClick = {
                    // Function to parse connection code (ID|Password format)
                    getClipboardText()?.let { clipText ->
                        if (clipText.contains("|")) {
                            val parts = clipText.split("|")
                            if (parts.size == 2) {
                                connectionId = TextFieldValue(parts[0], TextRange(parts[0].length))
                                password = TextFieldValue(parts[1], TextRange(parts[1].length))
                            }
                        }
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Filled.ContentPaste, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Paste Connection Code")
            }
        }
    }
}

@Composable
private fun getClipboardText(): String? {
    // Implementation dependent on desktop platform
    return try {
        java.awt.Toolkit.getDefaultToolkit().systemClipboard.getData(java.awt.datatransfer.DataFlavor.stringFlavor) as? String
    } catch (e: Exception) {
        null
    }
}
