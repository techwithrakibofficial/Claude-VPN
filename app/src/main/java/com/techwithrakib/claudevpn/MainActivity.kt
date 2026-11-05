package com.techwithrakib.claudevpn

import android.app.Activity
import android.net.VpnService
import android.os.Bundle
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

class MainActivity : ComponentActivity() {
 companion object { const val EXTRA_TILE_TOGGLE = "tile_toggle" }
 private var pendingConnect = false
 private var message by mutableStateOf<String?>(null)
 private var refreshTick by mutableIntStateOf(0)

 private val consent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
  if (it.resultCode == Activity.RESULT_OK && pendingConnect) connectNow()
  pendingConnect = false
 }
 private val configPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
  if (uri != null) try {
   contentResolver.openInputStream(uri)?.use { VpnController.importConfig(this, it.readBytes()) }
   refresh()
  } catch (e: Exception) { message = e.message ?: "Invalid configuration" }
 }

 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  VpnController.initialize(this)
  setContent { Screen(refreshTick, message, ::chooseConfig, ::toggleVpn) }
  if (intent.getBooleanExtra(EXTRA_TILE_TOGGLE, false)) toggleVpn()
 }
 private fun refresh() { refreshTick++ }
 private fun chooseConfig() { configPicker.launch(arrayOf("text/plain", "application/octet-stream")) }
 private fun toggleVpn() {
  if (VpnController.isUp()) {
   try { VpnController.disconnect(this); message = null } catch (e: Exception) { message = e.message }
   refresh(); return
  }
  if (!VpnController.hasConfig()) { message = "Import a WireGuard .conf file first."; chooseConfig(); return }
  val prepare = VpnService.prepare(this)
  if (prepare != null) { pendingConnect = true; consent.launch(prepare) } else connectNow()
 }
 private fun connectNow() {
  try { VpnController.connect(this); message = null } catch (e: Exception) { message = e.message ?: "Connection failed" }
  refresh()
 }
}

@Composable
private fun Screen(tick: Int, message: String?, onImport: () -> Unit, onToggle: () -> Unit) {
 val navy = Color(0xFF08111F)
 val panel = Color(0xFF122238)
 val cyan = Color(0xFF55D6E8)
 val connected = VpnController.isUp()
 val configured = VpnController.hasConfig()
 MaterialTheme(colorScheme = darkColorScheme(primary = cyan, background = navy, surface = panel)) {
  Column(Modifier.fillMaxSize().background(navy).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
   Text("CLAUDE VPN", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
   Spacer(Modifier.height(8.dp))
   Text("PRIVATE CONNECTION", color = cyan, fontSize = 11.sp, letterSpacing = 2.sp)
   Spacer(Modifier.height(36.dp))
   Card(colors = CardDefaults.cardColors(containerColor = panel), shape = RoundedCornerShape(28.dp)) {
    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
     Text(if (connected) "CONNECTED" else "DISCONNECTED", color = if (connected) cyan else Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
     Spacer(Modifier.height(8.dp))
     Text(if (configured) "WireGuard profile imported" else "No VPN profile configured", color = Color.LightGray, fontSize = 13.sp)
     Spacer(Modifier.height(22.dp))
     Button(onClick = onToggle, modifier = Modifier.fillMaxWidth(), enabled = configured) { Text(if (connected) "DISCONNECT" else "CONNECT") }
     OutlinedButton(onClick = onImport, modifier = Modifier.fillMaxWidth()) { Text(if (configured) "CHANGE CONFIGURATION" else "IMPORT WIREGUARD CONFIG") }
    }
   }
   Spacer(Modifier.height(18.dp))
   if (message != null) Text(message, color = Color(0xFFFFB4AB), fontSize = 12.sp)
   Text("Use only a VPN server you are authorized to access.", color = Color.Gray, fontSize = 11.sp)
  }
 }
}
