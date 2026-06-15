package com.abster.home.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.abster.home.model.RouterDevice
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.net.InetAddress

object DiscoveryService {

    suspend fun discoverRouters(context: Context): List<RouterDevice> = withContext(Dispatchers.IO) {
        val devices = mutableListOf<RouterDevice>()
        
        // 1. Get Gateway IP
        val gateway = getGateway(context)
        if (gateway != null) {
            devices.add(RouterDevice("Main Router", gateway, "Detected via Gateway", 100))
        }

        // 2. Try mDNS discovery (NSD)
        val mDnsDevices = discoverViaNsd(context)
        devices.addAll(mDnsDevices)

        // 3. Add some common router IPs if they are not the gateway or discovered via mDNS
        val commonIps = listOf("192.168.1.1", "192.168.0.1", "192.168.100.1")
        for (ip in commonIps) {
            if (devices.none { it.host == ip } && isReachable(ip)) {
                devices.add(RouterDevice("Discovered Router", ip, "Generic OpenWrt", 80))
            }
        }

        // If nothing found, add a fallback for development/demo
        if (devices.isEmpty()) {
            devices.add(RouterDevice("Mock Router (Fallback)", "192.168.1.1", "OpenWrt 23.05", 90))
        }

        devices.distinctBy { it.host }
    }

    private suspend fun discoverViaNsd(context: Context): List<RouterDevice> {
        val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val multicastLock = wifiManager.createMulticastLock("DiscoveryServiceLock")
        multicastLock.setReferenceCounted(true)

        val discoveredDevices = mutableListOf<RouterDevice>()
        val discoveryDeferred = CompletableDeferred<List<RouterDevice>>()

        val discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) {}
            override fun onServiceFound(service: NsdServiceInfo) {
                if (service.serviceType.contains("_http") || service.serviceType.contains("_ssh")) {
                    val resolveListener = object : NsdManager.ResolveListener {
                        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
                        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                            val host = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                serviceInfo.hostAddresses.firstOrNull()?.hostAddress
                            } else {
                                @Suppress("DEPRECATION")
                                serviceInfo.host?.hostAddress
                            }
                            val name = serviceInfo.serviceName
                            if (host != null) {
                                discoveredDevices.add(RouterDevice(name, host, "mDNS: ${serviceInfo.serviceType}", 95))
                            }
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        // On API 34+, resolveService is deprecated in favor of registerServiceInfoCallback.
                        // However, for a simple one-shot resolve, we can still use it or suppress.
                        @Suppress("DEPRECATION")
                        nsdManager.resolveService(service, Dispatchers.IO.asExecutor(), resolveListener)
                    } else {
                        @Suppress("DEPRECATION")
                        nsdManager.resolveService(service, resolveListener)
                    }
                }
            }

            override fun onServiceLost(service: NsdServiceInfo) {}
            override fun onDiscoveryStopped(regType: String) {
                discoveryDeferred.complete(discoveredDevices)
            }
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                discoveryDeferred.complete(emptyList())
            }
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                discoveryDeferred.complete(discoveredDevices)
            }
        }

        try {
            multicastLock.acquire()
            nsdManager.discoverServices("_http._tcp.", NsdManager.PROTOCOL_DNS_SD, discoveryListener)
            
            withTimeoutOrNull(3000) {
                kotlinx.coroutines.delay(2500)
                discoveredDevices
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                nsdManager.stopServiceDiscovery(discoveryListener)
            } catch (e: Exception) {}
            if (multicastLock.isHeld) {
                multicastLock.release()
            }
        }

        return discoveredDevices
    }

    private fun getGateway(context: Context): String? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = cm.activeNetwork
                val lp = cm.getLinkProperties(activeNetwork)
                val gatewayAddr = lp?.routes?.firstOrNull { it.isDefaultRoute }?.gateway
                gatewayAddr?.hostAddress
            } else {
                val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                @Suppress("DEPRECATION")
                val dhcp = wm.dhcpInfo
                val gateway = dhcp.gateway
                if (gateway == 0) return null
                ((gateway and 0xFF).toString() + "." +
                        (gateway shr 8 and 0xFF) + "." +
                        (gateway shr 16 and 0xFF) + "." +
                        (gateway shr 24 and 0xFF))
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun isReachable(ip: String): Boolean {
        return try {
            InetAddress.getByName(ip).isReachable(500)
        } catch (e: Exception) {
            false
        }
    }
}
