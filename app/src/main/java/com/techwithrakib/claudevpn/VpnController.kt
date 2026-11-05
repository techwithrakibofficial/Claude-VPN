package com.techwithrakib.claudevpn

import android.content.Context
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import java.io.File

/** WireGuard backend. Only user-imported, authorized profiles are accepted. */
object VpnController {
 private lateinit var backend: GoBackend
 private lateinit var configFile: File
 @Volatile private var state: Tunnel.State = Tunnel.State.DOWN
 @Volatile private var lastError: String? = null

 val tunnel: Tunnel = object : Tunnel {
  override fun getName(): String = "claude-vpn"
  override fun onStateChange(newState: Tunnel.State) { state = newState }
 }

 fun initialize(context: Context) {
  if (!::backend.isInitialized) {
   backend = GoBackend(context.applicationContext)
   configFile = File(context.applicationContext.filesDir, "imported.conf")
  }
 }
 fun hasConfig() = ::configFile.isInitialized && configFile.exists()
 fun isUp() = state == Tunnel.State.UP
 fun status() = if (isUp()) "Connected" else "Disconnected"
 fun error() = lastError

 fun importConfig(context: Context, bytes: ByteArray) {
  initialize(context)
  Config.parse(bytes.inputStream()) // Validate before persisting.
  configFile.writeBytes(bytes)
  lastError = null
 }
 fun connect(context: Context) {
  initialize(context)
  check(hasConfig()) { "Import a valid WireGuard configuration first." }
  try {
   val config = Config.parse(configFile.inputStream())
   backend.setState(tunnel, Tunnel.State.UP, config)
   lastError = null
  } catch (t: Throwable) {
   state = Tunnel.State.DOWN
   lastError = t.message ?: "Connection failed"
   throw t
  }
 }
 fun disconnect(context: Context) {
  initialize(context)
  try {
   backend.setState(tunnel, Tunnel.State.DOWN, null)
   state = Tunnel.State.DOWN
   lastError = null
  } catch (t: Throwable) {
   lastError = t.message ?: "Disconnect failed"
   throw t
  }
 }
}
