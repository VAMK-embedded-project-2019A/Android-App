package com.vamk_espp.sound_player_app.bluetooth_status

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

open class StatusClient(private val context: Context) {
    private val adapter = BluetoothAdapter.getDefaultAdapter()
    private val filter = IntentFilter()

    private var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = receiveIntent(intent)
    }

    init {
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
    }

    // on Bluetooth state change
    open fun onStateChange(new_state: Int) {
    }

    // get current device status, use only at start
    fun state(): Int {
        return adapter.state
    }

    // register broadcast listener
    fun register() {
        context.registerReceiver(receiver, filter)
    }

    // unregister broadcast listener
    fun unregister() {
        context.unregisterReceiver(receiver)
    }

    // call corresponding call back for each action
    private fun receiveIntent(intent: Intent) {
        when (intent.action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> receiveStateChange(intent)
        }
    }

    private fun receiveStateChange(intent: Intent) {
        val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

        onStateChange(state)
    }
}
