package com.vamk_espp.sound_player_app

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.vamk_espp.sound_player_app.helpers.ActivityId
import com.vamk_espp.sound_player_app.helpers.ExtraId
import com.vamk_espp.sound_player_app.bluetooth_status.StatusClient as Client

class BluetoothStatus : AppCompatActivity() {
    private lateinit var dialogTextView: TextView
    private lateinit var nextActivityId: String

    private val BtMessage = hashMapOf(
        BluetoothAdapter.STATE_ON to "Bluetooth is on.",
        BluetoothAdapter.STATE_OFF to "Bluetooth is off. Turn on bluetooth to use this application.",
        BluetoothAdapter.STATE_TURNING_ON to "Bluetooth is turning on.",
        BluetoothAdapter.STATE_TURNING_OFF to "Bluetooth is turning off.",
        BluetoothAdapter.ERROR to "Bluetooth has unknown error."
    )

    private val client = object : Client(this) {
        override fun onStateChange(new_state: Int) = updateState(new_state)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bt_status)

        // get the next activity id (if none, start with scan)
        nextActivityId = intent.getStringExtra(ExtraId.EXTRA_ACTIVITY_ID) ?: ActivityId.BT_SCAN

        // get the TextView for the dialog
        dialogTextView = findViewById(R.id.bt_status)

        Log.d("BtStatus", "On create")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d("BtStatus", "On destroy")
    }

    override fun onResume() {
        super.onResume()

        // register client broadcast listener
        client.register()

        // update current bluetooth state
        dialogTextView.text = "Getting bluetooth device status"
        updateState(client.state())

        Log.d("BtStatus", "On resume")
    }

    override fun onPause() {
        super.onPause()

        // unregister client broadcast listener
        client.unregister()

        Log.d("BtStatus", "On pause")
    }

    private fun updateState(new_state: Int) {
        if (new_state == BluetoothAdapter.STATE_ON)
            startNextActivity()

        dialogTextView.text = BtMessage[new_state]
    }

    private fun startNextActivity() {
        this.finish()

        when (nextActivityId) {
            ActivityId.BT_SCAN -> {
                val intent = Intent(this, BluetoothScan::class.java)
                startActivity(intent)
            }
        }
    }
}
