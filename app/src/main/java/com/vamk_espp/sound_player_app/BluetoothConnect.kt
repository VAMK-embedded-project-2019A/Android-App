package com.vamk_espp.sound_player_app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.vamk_espp.sound_player_app.helpers.ExtraId

class BluetoothConnect : AppCompatActivity() {
    private var deviceName: String? = null
    private var deviceMac: String? = null
    private lateinit var dialogTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bt_connect)

        deviceName = intent.getStringExtra(ExtraId.EXTRA_DEVICE_NAME)
        deviceMac = intent.getStringExtra(ExtraId.EXTRA_DEVICE_MAC)

        dialogTextView = findViewById(R.id.bt_connect_status)
    }

    override fun onResume() {
        super.onResume()

        dialogTextView.text = "Connecting to $deviceName \nat $deviceMac"
    }
}
