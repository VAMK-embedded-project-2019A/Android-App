package com.vamk_espp.sound_player_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.bluetooth.BluetoothDevice
import android.view.View
import android.widget.TextView
import com.vamk_espp.sound_player_app.bluetooth_scan.Client
import com.vamk_espp.sound_player_app.bluetooth_scan.ListAdapter
import java.util.*
import kotlin.collections.ArrayList

class BluetoothScan : AppCompatActivity() {
    private val deviceList = object : ArrayList<BluetoothDevice>() {
        // prioritize device display (from highest to lowest)
        // - Device name ESPP Sound Player
        // - Device with other name
        // - Device without name
        @Suppress("SpellCheckingInspection")
        private fun prioritizeDevice(a: BluetoothDevice): Int {
            val deviceName = "ESPP Sound Player"

            when (a.name) {
                null -> return 0
                deviceName -> return 2
            }
            return 1
        }

        private fun compareDevice(a: BluetoothDevice, b: BluetoothDevice): Int {
            val pA = prioritizeDevice(a)
            val pB = prioritizeDevice(b)

            // compare priority
            if (pA > pB) return 1
            if (pA < pB) return -1

            // compare name (alphabetical)
            if (a.name != null && b.name != null) {
                // compare without case
                val nameA = a.name.toLowerCase(Locale.getDefault())
                val nameB = b.name.toLowerCase(Locale.getDefault())
                if (nameA > nameB) return -1
                if (nameA < nameB) return 1

                // compare with case
                if (a.name > b.name) return -1
                if (a.name < b.name) return 1
            }

            // compare address (increasing)
            if (a.address > b.address) return -1
            if (a.address < b.address) return 1

            return 0
        }

        // add with priority and deduplicate
        override fun add(element: BluetoothDevice): Boolean {
            var cmp = 1
            var i = size // if the for loop is not break, add to the end of the array

            // find the first item that is not lower than the element in priority
            for (j in 0 until size) {
                cmp = compareDevice(element, this[j])

                // add to the position of the first element that has lower priority
                if (cmp > -1) {
                    i = j
                    break
                }
            }

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

        // register client broadcast listener
        client.register()

        // start bluetooth scan
        client.startScan()
    }

    override fun onDestroy() {
        super.onDestroy()

        // unregister client broadcast listener
        client.unregister()
    }

    fun dialogOnClick(@Suppress("UNUSED_PARAMETER") v: View) {
        if (!scanning) {
            // clear list and update view
            deviceList.clear()
            deviceListAdapter.notifyDataSetChanged()

            // start new scan
            client.startScan()
        }
    }
}
