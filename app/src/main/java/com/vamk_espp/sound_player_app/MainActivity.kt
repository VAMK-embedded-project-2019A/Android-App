package com.vamk_espp.sound_player_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var listViewAdapter: ArrayAdapter<String>

    private val btAdapter = BluetoothAdapter.getDefaultAdapter()
    private var deviceList = ArrayList<String>()

    private var discoverRecv = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context, intent: Intent) {
            if (intent.action == BluetoothDevice.ACTION_FOUND) {
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                if (device != null) {
                    val name = if (device.name == null) "<Anonymous>" else device.name
                    val address = if (device.address == null) "No address" else device.address
                    deviceList.add("$name\n$address")
                    listViewAdapter.notifyDataSetChanged()

                    Log.d("Bluetooth", "Found device:")
                    Log.d("Bluetooth", "Name: $name")
                    Log.d("Bluetooth", "Name: $address")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // setup list view
        listView = findViewById(R.id.bt_device_list)
        listViewAdapter = ArrayAdapter(this, R.layout.bluetooth_device_card, deviceList)
        listView.adapter = listViewAdapter

        // setup callback intent for bluetooth discover
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        registerReceiver(discoverRecv, filter)

        // start bluetooth discover
        btAdapter.startDiscovery()
    }
}
