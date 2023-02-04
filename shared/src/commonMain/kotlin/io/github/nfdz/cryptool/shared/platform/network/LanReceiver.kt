package io.github.nfdz.cryptool.shared.platform.network

interface LanReceiver {
    fun getPort(): Int
    fun getFreeSlot(): Int
}