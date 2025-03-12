
package com.abdownloadmanager.shared.remote

import com.abdownloadmanager.shared.config.AppConfig
import com.abdownloadmanager.shared.utils.PlatformInfo
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import kotlin.time.Duration.Companion.seconds

class RemoteConnectionService : KoinComponent {
    private val appConfig: AppConfig by inject()
    private val client = HttpClient(CIO) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
            pingInterval = 15.seconds.inWholeMilliseconds
        }
    }
    
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
    private val _remoteCommands = MutableSharedFlow<RemoteCommand>()
    val remoteCommands: SharedFlow<RemoteCommand> = _remoteCommands.asSharedFlow()
    
    private var connectionJob: Job? = null
    private var deviceId: String = ""
    
    init {
        // Generate a unique device ID if not already present
        deviceId = appConfig.remoteConnectionDeviceId.value
        if (deviceId.isEmpty()) {
            deviceId = UUID.randomUUID().toString()
            appConfig.remoteConnectionDeviceId.value = deviceId
        }
    }
    
    fun connectToRemoteDevice(connectionId: String, password: String) {
        if (connectionJob?.isActive == true) {
            connectionJob?.cancel()
        }
        
        _connectionState.value = ConnectionState.Connecting
        
        connectionJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                client.webSocket(
                    method = HttpMethod.Get,
                    host = "abdownloadmanager-remote.example.com", // This would be your actual remote server
                    port = 443,
                    path = "/connect"
                ) {
                    // Authentication
                    send(Frame.Text("""{"type":"auth","id":"$connectionId","password":"$password","deviceInfo":{"name":"${PlatformInfo.deviceName}","platform":"${PlatformInfo.os}"}}"""))
                    
                    val authResponse = incoming.receive()
                    if (authResponse is Frame.Text) {
                        val text = authResponse.readText()
                        if (text.contains("\"status\":\"success\"")) {
                            _connectionState.value = ConnectionState.Connected(connectionId)
                            
                            // Handle messages
                            try {
                                for (frame in incoming) {
                                    when (frame) {
                                        is Frame.Text -> {
                                            val command = Json.decodeFromString<RemoteCommand>(frame.readText())
                                            _remoteCommands.emit(command)
                                        }
                                        else -> { /* Ignore other frame types */ }
                                    }
                                }
                            } catch (e: Exception) {
                                _connectionState.value = ConnectionState.Error("Connection error: ${e.message}")
                            }
                        } else {
                            _connectionState.value = ConnectionState.Error("Authentication failed")
                        }
                    }
                }
            } catch (e: Exception) {
                _connectionState.value = ConnectionState.Error("Failed to connect: ${e.message}")
            } finally {
                if (_connectionState.value !is ConnectionState.Error) {
                    _connectionState.value = ConnectionState.Disconnected
                }
            }
        }
    }
    
    fun disconnect() {
        connectionJob?.cancel()
        _connectionState.value = ConnectionState.Disconnected
    }
    
    fun generateConnectionQR(): String {
        val connectionId = UUID.randomUUID().toString().take(8)
        val password = UUID.randomUUID().toString().take(6)
        
        return "${connectionId}|${password}"
    }
    
    sealed class ConnectionState {
        object Disconnected : ConnectionState()
        object Connecting : ConnectionState()
        data class Connected(val remoteId: String) : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }
}
