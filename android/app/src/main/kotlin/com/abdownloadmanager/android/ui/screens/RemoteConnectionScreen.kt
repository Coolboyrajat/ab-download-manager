
package com.abdownloadmanager.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.abdownloadmanager.shared.remote.RemoteConnectionService
import com.abdownloadmanager.shared.remote.ui.QRCodeGenerateView
import com.abdownloadmanager.shared.remote.ui.QRCodeScannerView
import org.koin.androidx.compose.koinViewModel

@Composable
fun RemoteConnectionScreen() {
    val connectionService = koinViewModel<RemoteConnectionService>()
    val connectionState by connectionService.connectionState.collectAsState()
    
    var showQRScanner by remember { mutableStateOf(false) }
    var showQRGenerator by remember { mutableStateOf(false) }
    var connectionId by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    
    if (showQRScanner) {
        QRCodeScannerView(
            connectionService = connectionService,
            onScanned = { id, pass ->
                connectionId = id
                password = pass
                showQRScanner = false
                connectionService.connectToRemoteDevice(id, pass)
            }
        )
        return
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Remote Connection") },
                navigationIcon = if (showQRGenerator) {
                    {
                        IconButton(onClick = { showQRGenerator = false }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                } else null
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
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
            
            if (showQRGenerator) {
                QRCodeGenerateView(
                    connectionService = connectionService,
                    modifier = Modifier.fillMaxWidth()
                )
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
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { showQRScanner = true }
                            ) {
                                Icon(Icons.Filled.QrCodeScanner, contentDescription = null)
                                Spacer(Modifier.width(4.dp))
                                Text("Scan")
                            }
                            
                            Button(
                                onClick = { 
                                    connectionService.connectToRemoteDevice(connectionId, password)
                                },
                                enabled = connectionId.isNotEmpty() && password.isNotEmpty() &&
                                        connectionState !is RemoteConnectionService.ConnectionState.Connecting
                            ) {
                                Text("Connect")
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Button options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = { showQRGenerator = true }) {
                        Icon(Icons.Filled.QrCode, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Generate QR Code")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedButton(
                        onClick = {
                            clipboardManager.getText()?.text?.let { clipText ->
                                if (clipText.contains("|")) {
                                    val parts = clipText.split("|")
                                    if (parts.size == 2) {
                                        connectionId = parts[0]
                                        password = parts[1]
                                    }
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Filled.ContentPaste, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Paste from Clipboard")
                    }
                }
            }
        }
    }
}
