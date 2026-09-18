package com.pedro.ChamaKids

expect object DeviceIdentifier {
    fun getUniqueId(): String
    fun closeApp()
}
