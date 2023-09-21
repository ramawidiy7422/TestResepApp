package com.ramawidi.ghoresepmakanan.data.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NetworkMonitor(context: Context): ConnectivityManager.NetworkCallback() {
    private val _isNetworkAvailable = MutableStateFlow(false)
    val isNetworkAvailable: StateFlow<Boolean> get() = _isNetworkAvailable

    init {
        checkNetworkAvailability(context)
    }

    private fun checkNetworkAvailability(context: Context) {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).let {
                connectivityManager ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(this)
            }
            else {
                // For API Level 24 below
                NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build().let {
                            networkRequest ->
                        connectivityManager.registerNetworkCallback(networkRequest, this)
                    }
            }
        }
    }

    override fun onAvailable(network: Network) {
        _isNetworkAvailable.value = true
    }

    override fun onLost(network: Network) {
        _isNetworkAvailable.value = false
    }

}