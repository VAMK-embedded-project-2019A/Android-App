package com.vamk_espp.sound_player_app.helpers

import android.bluetooth.BluetoothDevice
import java.util.Locale

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

// compare two device for sorting
fun compareDevice(a: BluetoothDevice, b: BluetoothDevice): Int {
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
