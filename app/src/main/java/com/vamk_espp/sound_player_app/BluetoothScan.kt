package com.vamk_espp.sound_player_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.TextView
import com.vamk_espp.sound_player_app.bluetooth_scan.ScanClient as Client
import com.vamk_espp.sound_player_app.bluetooth_scan.ListAdapter
import com.vamk_espp.sound_player_app.helpers.ActivityId
import com.vamk_espp.sound_player_app.helpers.ExtraId
import com.vamk_espp.sound_player_app.helpers.compareDevice
import kotlin.collections.ArrayList

class BluetoothScan : AppCompatActivity() {
    private val deviceList = object : ArrayList<BluetoothDevice>() {
        // add with priority and deduplicate
        override fun add(element: BluetoothDevice): Boolean {
            // compare result
            var cmp = 1

            // get first device in the list that has lower priority than the added element
            var i = indexOfFirst {
                cmp = compareDevice(it, element)
                cmp < 1
            }
            // if none is found, add to the end of the list
            i = if (i > -1) i else size

            // only add if item is not duplicate
            if (cmp != 0) {
                super.add(i, element)
                return true
            }
            return false
        }
    }

    private lateinit var deviceListAdapter: ListAdapter
    private lateinit var deviceListView: ListView
    private lateinit var dialogTextView: TextView
    private var scanning = true

    private val client = object : Client(this) {
        override fun onDiscover(device: BluetoothDevice) {
            // add device to list and update
            deviceList.add(device)
            deviceListAdapter.notifyDataSetChanged()
        }

        override fun onScanStart() {
            scanning = true
            dialogTextView.setText(R.string.scan_start_dialog)
        }

        override fun onScanStop() {
            scanning = false
            dialogTextView.setText(R.string.scan_stop_dialog)
        }

        override fun onError() = handleBtError()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bt_scan)

        // load views
        deviceListView = findViewById(R.id.bt_scan_device_list)
        dialogTextView = findViewById(R.id.bt_scan_dialog)

        // setup list view
        deviceListAdapter = ListAdapter(this, R.layout.bt_scan_item, deviceList)
        deviceListView.adapter = deviceListAdapter
        deviceListView.setOnItemClickListener { _, _, position, _ ->
            val device = deviceList[position]
            startConnection(device)
        }

        Log.d("BtScan", "On create")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("BtScan", "On destroy")
    }

    override fun onResume() {
        super.onResume()

        // register client broadcast listener
        client.register()

        // start bluetooth scan
        startScan()

        Log.d("BtScan", "On resume")
    }

    override fun onPause() {
        super.onPause()

        // unregister client broadcast listener
        client.unregister()

        Log.d("BtScan", "On pause")
    }

    fun dialogOnClick(@Suppress("UNUSED_PARAMETER") v: View) {
        if (!scanning)
            startScan()
    }

    private fun startScan() {
        // clear list and update view
        deviceList.clear()
        deviceListAdapter.notifyDataSetChanged()

        // start new scan
        client.startScan()
    }

    private fun handleBtError() {
        this.finish()

        val intent = Intent(this, BluetoothStatus::class.java).apply {
            putExtra(ExtraId.EXTRA_ACTIVITY_ID, ActivityId.BT_SCAN)
        }
        startActivity(intent)
    }

    private fun startConnection(device: BluetoothDevice) {
        this.finish()

        val intent = Intent(this, BluetoothConnect::class.java).apply {
            putExtra(ExtraId.EXTRA_DEVICE_NAME, device.name ?: "Anonymous device")
            putExtra(ExtraId.EXTRA_DEVICE_MAC, device.address)
        }
        startActivity(intent)
    }
}
