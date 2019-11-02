package com.vamk_espp.sound_player_app.bluetooth_scan

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.vamk_espp.sound_player_app.R

class ListAdapter(context: Context, private val layout: Int, list: List<BluetoothDevice>) :
    ArrayAdapter<BluetoothDevice>(context, layout, list) {

    // override rendering for each item
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflateView()
        val device = getItem(position)

        if (device != null) {
            setTextView(view, R.id.name, device.name ?: "<Anonymous>")
            setTextView(view, R.id.address, device.address)
        }

        return view
    }

    // return an inflate view for the current layout resource
    private fun inflateView(): View {
        val li = LayoutInflater.from(context)
        return li.inflate(layout, null)
    }

    // set the value of TextView inside a view
    private fun setTextView(view: View, id: Int, text: String) {
        view.findViewById<TextView>(id)?.text = text
    }
}