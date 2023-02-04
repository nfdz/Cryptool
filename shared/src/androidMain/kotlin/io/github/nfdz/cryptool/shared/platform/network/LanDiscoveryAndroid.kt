package io.github.nfdz.cryptool.shared.platform.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
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

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
        val supported: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }

    private val connectivityManager
        get() = context.getSystemService(ConnectivityManager::class.java)


    private val networksMap: MutableMap<Network, List<String>> by lazy {
        mutableMapOf()
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        val addresses = network.getAddresses()
        networksMap[network] = addresses
        Napier.d(tag = tag, message = "Network available ($network): $addresses")
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        Napier.d(tag = tag, message = "Network lost ($network)")
        networksMap.remove(network)
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
        while (true) {
            emit(networksMap.flatMap { it.value })
            delay(refreshIntervalInMillis)
            Napier.d(tag = tag, message = "Refreshing address list")
        }
    }

    override fun setupNetworkCallback() {
        unregisterNetworkCallback()
        registerNetworkCallback()
        launch {
            delay(autoUnregisterInMillis)
            unregisterNetworkCallback()
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