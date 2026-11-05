package com.techwithrakib.claudevpn

import android.app.Activity
import android.app.StatusBarManager
import android.content.ComponentName
import android.graphics.drawable.Icon
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.service.quicksettings.TileService
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
 companion object { const val EXTRA_TILE_TOGGLE = "tile_toggle" }
 private var pendingConnect = false
 private var message by mutableStateOf<String?>(null)
 private var refreshTick by mutableIntStateOf(0)

 private val consent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
  if (it.resultCode == Activity.RESULT_OK && pendingConnect) connectNow()
  else if (pendingConnect) message = "VPN permission was not granted."
  pendingConnect = false
 }
 private val configPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
  if (uri != null) {
   lifecycleScope.launch {
    try {
     val bytes = withContext(Dispatchers.IO) {
      contentResolver.openInputStream(uri)?.use { it.readBytes() }
       ?: error("Unable to read the selected file.")
     }
     withContext(Dispatchers.IO) { VpnController.importConfig(this@MainActivity, bytes) }
     message = "WireGuard profile imported securely."
     refresh()
    } catch (e: Exception) {
     message = e.message ?: "Invalid configuration"
    }
   }
  }
 }

 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  VpnController.initialize(this)
  setContent { Screen(refreshTick, message, ::chooseConfig, ::toggleVpn, ::requestQuickSettingsTile) }
  if (intent.getBooleanExtra(EXTRA_TILE_TOGGLE, false)) toggleVpn()
 }

 override fun onNewIntent(intent: android.content.Intent) {
  super.onNewIntent(intent)
  setIntent(intent)
  if (intent.getBooleanExtra(EXTRA_TILE_TOGGLE, false)) toggleVpn()
 }

 private fun refresh() {
  refreshTick++
  TileService.requestListeningState(this, ComponentName(this, ClaudeVpnTileService::class.java))
 }

 private fun chooseConfig() {
  configPicker.launch(arrayOf("text/plain", "application/octet-stream"))
 }

 private fun toggleVpn() {
  if (VpnController.isUp()) {
   lifecycleScope.launch {
    message = "Disconnecting…"
    try {
     withContext(Dispatchers.IO) { VpnController.disconnect(this@MainActivity) }
     message = null
    } catch (e: Exception) { message = e.message ?: "Disconnect failed" }
    refresh()
   }
   return
  }
  if (!VpnController.hasConfig()) {
   message = "Import a WireGuard .conf profile first."
   chooseConfig()
   return
  }
  val prepare = VpnService.prepare(this)
  if (prepare != null) { pendingConnect = true; consent.launch(prepare) } else connectNow()
 }

 private fun connectNow() {
  lifecycleScope.launch {
   message = "Connecting securely…"
   try {
    withContext(Dispatchers.IO) { VpnController.connect(this@MainActivity) }
    message = null
   } catch (e: Exception) {
    message = e.message ?: "Connection failed"
   }
   refresh()
  }
 }

 private fun requestQuickSettingsTile() {
  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
   message = "Open Quick Settings, tap Edit, then add the Claude VPN tile."
   return
  }
  val statusBar = getSystemService(StatusBarManager::class.java)
  if (statusBar == null) { message = "Quick Settings tile API is unavailable on this device."; return }
  statusBar.requestAddTileService(
   ComponentName(this, ClaudeVpnTileService::class.java),
   "Claude VPN",
   Icon.createWithResource(this, android.R.drawable.ic_lock_lock),
   { command -> runOnUiThread(command) }
  ) { result ->
   message = when (result) {
    StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED -> "Quick Settings tile added."
    StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED -> "Quick Settings tile is already added."
    StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_NOT_ADDED -> "Tile was not added."
    else -> "Quick Settings tile request could not be completed."
   }
  }
 }
}

@Composable
private fun Screen(
 tick: Int,
 message: String?,
 onImport: () -> Unit,
 onToggle: () -> Unit,
 onAddTile: () -> Unit
) {
 val navy = Color(0xFF07101D)
 val panel = Color(0xFF111F32)
 val cyan = Color(0xFF58D9E8)
 val connected = VpnController.isUp()
 val configured = VpnController.hasConfig()
 MaterialTheme(colorScheme = darkColorScheme(primary = cyan, background = navy, surface = panel)) {
  Column(
   Modifier.fillMaxSize().background(navy).padding(horizontal = 22.dp, vertical = 28.dp),
   horizontalAlignment = Alignment.CenterHorizontally,
   verticalArrangement = Arrangement.Center
  ) {
   Text("CLAUDE VPN", color = Color.White, fontSize = 29.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp)
   Spacer(Modifier.height(7.dp))
   Text("PRIVATE CONNECTION", color = cyan, fontSize = 11.sp, letterSpacing = 2.4.sp)
   Spacer(Modifier.height(34.dp))
   Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = panel),
    shape = RoundedCornerShape(30.dp)
   ) {
    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
     Text(if (connected) "PROTECTED" else "NOT CONNECTED", color = if (connected) cyan else Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
     Spacer(Modifier.height(9.dp))
     Text(
      if (configured) "Your WireGuard profile is ready" else "Add a WireGuard profile to get started",
      color = Color(0xFFB8C6D8), fontSize = 13.sp
     )
     Spacer(Modifier.height(24.dp))
     Button(
      onClick = onToggle,
      modifier = Modifier.fillMaxWidth().height(52.dp),
      enabled = configured,
      shape = RoundedCornerShape(18.dp)
     ) { Text(if (connected) "DISCONNECT" else "CONNECT", fontWeight = FontWeight.Bold, letterSpacing = 1.sp) }
     Spacer(Modifier.height(10.dp))
     OutlinedButton(
      onClick = onImport,
      modifier = Modifier.fillMaxWidth().height(50.dp),
      shape = RoundedCornerShape(18.dp)
     ) { Text(if (configured) "CHANGE WIREGUARD PROFILE" else "IMPORT WIREGUARD PROFILE") }
    }
   }
   Spacer(Modifier.height(14.dp))
   OutlinedButton(onClick = onAddTile, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
    Text("ADD QUICK SETTINGS TILE")
   }
   Spacer(Modifier.height(18.dp))
   if (message != null) Text(message, color = Color(0xFFFFB4AB), fontSize = 12.sp)
   Spacer(Modifier.height(12.dp))
   Text("Profiles are encrypted on this device. Use only servers you are authorized to access.", color = Color.Gray, fontSize = 11.sp)
  }
 }
}
