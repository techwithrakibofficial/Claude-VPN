package com.techwithrakib.claudevpn

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** WireGuard backend. Only user-imported, authorized profiles are accepted. */
object VpnController {
 private const val KEY_ALIAS = "claude_vpn_profile_key"
 private const val IV_LENGTH = 12
 private lateinit var backend: GoBackend
 private lateinit var configFile: File
 @Volatile private var state: Tunnel.State = Tunnel.State.DOWN
 @Volatile private var lastError: String? = null

 val tunnel: Tunnel = object : Tunnel {
  override fun getName(): String = "claude-vpn"
  override fun onStateChange(newState: Tunnel.State) { state = newState }
 }

 @Synchronized fun initialize(context: Context) {
  if (!::backend.isInitialized) {
   backend = GoBackend(context.applicationContext)
   configFile = File(context.applicationContext.filesDir, "imported.conf.enc")
  }
 }

 fun hasConfig() = ::configFile.isInitialized && configFile.exists()
 fun isUp() = state == Tunnel.State.UP
 fun status() = if (isUp()) "Connected" else "Disconnected"
 fun error() = lastError

 @Synchronized fun importConfig(context: Context, bytes: ByteArray) {
  initialize(context)
  Config.parse(bytes.inputStream()) // Validate before persisting.
  val encrypted = encrypt(bytes)
  configFile.writeBytes(encrypted)
  lastError = null
 }

 @Synchronized fun connect(context: Context) {
  initialize(context)
  check(hasConfig()) { "Import a valid WireGuard configuration first." }
  try {
   val plain = decrypt(configFile.readBytes())
   val config = Config.parse(plain.inputStream())
   backend.setState(tunnel, Tunnel.State.UP, config)
   state = backend.getState(tunnel)
   lastError = null
  } catch (t: Throwable) {
   state = Tunnel.State.DOWN
   lastError = t.message ?: "Connection failed"
   throw t
  }
 }

 @Synchronized fun disconnect(context: Context) {
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

 @Synchronized fun refreshState(context: Context): Tunnel.State {
  initialize(context)
  state = try { backend.getState(tunnel) } catch (_: Exception) { Tunnel.State.DOWN }
  return state
 }

 private fun encrypt(plain: ByteArray): ByteArray {
  val cipher = Cipher.getInstance("AES/GCM/NoPadding")
  cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
  val encrypted = cipher.doFinal(plain)
  return cipher.iv + encrypted
 }

 private fun decrypt(payload: ByteArray): ByteArray {
  require(payload.size > IV_LENGTH) { "Stored VPN profile is invalid. Import it again." }
  val iv = payload.copyOfRange(0, IV_LENGTH)
  val encrypted = payload.copyOfRange(IV_LENGTH, payload.size)
  val cipher = Cipher.getInstance("AES/GCM/NoPadding")
  cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
  return cipher.doFinal(encrypted)
 }

 private fun getOrCreateKey(): SecretKey {
  val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
  (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
  val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
  generator.init(
   KeyGenParameterSpec.Builder(
    KEY_ALIAS,
    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
   ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
    .setKeySize(256)
    .setRandomizedEncryptionRequired(true)
    .build()
  )
  return generator.generateKey()
 }
}
