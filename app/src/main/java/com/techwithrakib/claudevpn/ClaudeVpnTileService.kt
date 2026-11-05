package com.techwithrakib.claudevpn

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class ClaudeVpnTileService : TileService() {
 override fun onStartListening() {
  super.onStartListening()
  VpnController.initialize(this)
  updateTileState()
 }
 override fun onClick() {
  super.onClick()
  VpnController.initialize(this)
  if (VpnController.isUp()) {
   try { VpnController.disconnect(this) } catch (_: Throwable) { }
   updateTileState()
   return
  }
  val launch = packageManager.getLaunchIntentForPackage(packageName)
  launch?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
  launch?.putExtra(MainActivity.EXTRA_TILE_TOGGLE, true)
  if (launch != null) startActivityAndCollapse(launch)
 }
 private fun updateTileState() {
  qsTile?.apply {
   state = if (VpnController.isUp()) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
   label = "Claude VPN"
   subtitle = if (VpnController.hasConfig()) VpnController.status() else "Import config"
   updateTile()
  }
 }
}
