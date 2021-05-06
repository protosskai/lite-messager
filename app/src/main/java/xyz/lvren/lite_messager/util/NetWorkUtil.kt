package xyz.lvren.lite_messager.util

import android.content.Context
import android.net.*
import java.net.Inet4Address
import java.net.InetAddress
import java.util.*

class NetWorkUtil(val context: Context) {

    private var connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var caps: NetworkCapabilities?
    private var linkProperties: LinkProperties?

    init {
        val currentNetwork = connectivityManager.activeNetwork
        caps = connectivityManager.getNetworkCapabilities(currentNetwork)

        linkProperties = connectivityManager.getLinkProperties(currentNetwork)

    }

    // 获取本地IPV4地址
    fun getLocalV4Address(): InetAddress? {
        val addresses = linkProperties?.linkAddresses ?: LinkedList()
        if (addresses.size == 0)
            return null
        for (address in addresses) {
            if (address.address is Inet4Address)
                return address.address
        }
        return null
    }

    // 检查WiFi是否已连接
    fun getWifiConnected(): Boolean {
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI) {
            return networkInfo.isAvailable
        }
        return false
    }
}