@file:Suppress("DEPRECATION")

package com.challenge.datamanager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class Utils {

    companion object {
        @Volatile private var instance: Utils? = null
        fun getInstance() =
                instance ?: synchronized(this) {
                    instance ?: Utils().also { instance = it }
                }
    }
    fun isConnectedToInternet(context: Context) : Boolean{
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}