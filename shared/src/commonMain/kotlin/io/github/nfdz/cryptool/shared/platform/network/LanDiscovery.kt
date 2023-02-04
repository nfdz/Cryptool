package io.github.nfdz.cryptool.shared.platform.network

import kotlinx.coroutines.flow.Flow

interface LanDiscovery {
    fun setupNetworkCallback()
    fun observeAddresses(): Flow<List<String>>
}