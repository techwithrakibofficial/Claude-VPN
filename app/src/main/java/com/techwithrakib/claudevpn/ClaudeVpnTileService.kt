package com.techwithrakib.claudevpn

import android.content.ComponentName
import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class ClaudeVpnTileService : TileService() {
 override fun onStartListening() {
  super.onStartListening()
  VpnController.initialize(this)
  VpnController.refreshState(this)
  updateTileState()
 }

 override fun onClick() {
  super.onClick()
  VpnController.initialize(this)
  if (VpnController.isUp()) {
   qsTile?.apply {
    state = Tile.STATE_UNAVAILABLE
    subtitle = "Disconnecting"
    updateTile()
   }
   Thread {
    try { VpnController.disconnect(this) } catch (_: Throwable) { }
    mainExecutor.execute {
     VpnController.refreshState(this)
     updateTileState()
     TileService.requestListeningState(this, ComponentName(this, ClaudeVpnTileService::class.java))
    }
   }.start()
   return
  }

  // The activity handles the system VPN-consent screen and profile checks.
  val launch = packageManager.getLaunchIntentForPackage(packageName)
  launch?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
  launch?.putExtra(MainActivity.EXTRA_TILE_TOGGLE, true)
  if (launch != null) startActivityAndCollapse(launch)
 }

 private fun updateTileState() {
  qsTile?.apply {
   val connected = VpnController.isUp()
   state = if (connected) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
   label = "Claude VPN"
   subtitle = when {
    connected -> "Connected"
    VpnController.hasConfig() -> "Tap to connect"
    else -> "Import profile"
   }
   updateTile()
  }
 }
}
