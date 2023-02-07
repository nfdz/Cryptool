package io.github.nfdz.cryptool.shared.platform.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.net.Inet4Address

class LanDiscoveryAndroid(private val context: Context) : LanDiscovery, ConnectivityManager.NetworkCallback(),
    CoroutineScope by CoroutineScope(Dispatchers.IO) {

    companion object {
        private const val tag = "LanDiscovery"
        private const val refreshIntervalInMillis = 3_000L
        private const val autoUnregisterInMillis = 30_000L
    }

    private val connectivityManager
        get() = context.getSystemService(ConnectivityManager::class.java)

    private val addressesCache = mutableSetOf<String>()

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        val addresses = network.getAddresses()
        addressesCache.addAll(addresses)
        Napier.d(tag = tag, message = "Network available ($network): $addresses")
    }

    private fun Network.getAddresses(): List<String> = runCatching {
        val linkProperties = connectivityManager.getLinkProperties(this)
        linkProperties!!.linkAddresses
            .filter { it.address is Inet4Address } // Only IPV4 for convenience (at the moment)
            .map {
                it.address.hostAddress
            }
    }.getOrElse { emptyList() }

    override fun observeAddresses(): Flow<List<String>> = flow {
        var previousValue: List<String> = emptyList()
        while (true) {
            val newValue = addressesCache.toList().sorted()
            if (previousValue != newValue) {
                previousValue = newValue
                emit(newValue)
                Napier.d(tag = tag, message = "Refreshing address list - new value")
            } else {
                Napier.d(tag = tag, message = "Refreshing address list - same value")
            }
            delay(refreshIntervalInMillis)
        }
    }

    override fun setupNetworkCallback() {
        addressesCache.clear()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            unregisterNetworkCallback()
            registerNetworkCallback()
            launch {
                delay(autoUnregisterInMillis)
                unregisterNetworkCallback()
            }
        } else {
            @Suppress("DEPRECATION") val networks = connectivityManager.allNetworks
            networks.forEach { network ->
                val addresses = network.getAddresses()
                addressesCache.addAll(addresses)
            }
        }
    }

    private fun registerNetworkCallback() = runCatching {
        Napier.d(tag = tag, message = "Register Network Callback")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(this)
        }
    }

    private fun unregisterNetworkCallback() = runCatching {
        Napier.d(tag = tag, message = "Unregister Network Callback")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.unregisterNetworkCallback(this)
        }
    }
}