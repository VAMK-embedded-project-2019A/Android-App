package com.vamk_espp.sound_player_app.bluetooth_scan

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

open class ScanClient(val context: Context) {
    private val adapter = BluetoothAdapter.getDefaultAdapter()
    private val filter = IntentFilter()

    private var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = receiveIntent(intent)
    }

    init {
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
    }

    // start a scan (last 12 seconds)
    fun startScan() {
        adapter.startDiscovery()
    }

    // register broadcast listener
    fun register() {
        context.registerReceiver(receiver, filter)
    }

    // unregister broadcast listener
    fun unregister() {
        context.unregisterReceiver(receiver)
    }

    // on discovering new device
    open fun onDiscover(device: BluetoothDevice) {
    }

    // on starting scanning
    open fun onScanStart() {
    }

    // on ending scanning
    open fun onScanStop() {
    }

    open fun onError() {

    }

    // call corresponding call back for each action
    private fun receiveIntent(intent: Intent) {
        when (intent.action) {
            BluetoothDevice.ACTION_FOUND -> receiveActionFound(intent)
            BluetoothAdapter.ACTION_DISCOVERY_STARTED -> onScanStart()
            BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> onScanStop()
            BluetoothAdapter.ACTION_STATE_CHANGED -> receiveStateChange(intent)
        }
    }

    private fun receiveActionFound(intent: Intent) {
        val device =
            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        if (device != null) {
            onDiscover(device)
        }
    }

    private fun receiveStateChange(intent: Intent) {
        val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
        if (state != BluetoothAdapter.STATE_ON)
            onError()
    }
}