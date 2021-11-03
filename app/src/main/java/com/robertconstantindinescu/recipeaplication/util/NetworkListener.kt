package com.robertconstantindinescu.recipeaplication.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * We want to lsiten for our internet connection and get notifies whentever we get or lose intenet conection
 * This class needs to extend ConnectivityManager.NetworkCallback().
 *
 * ConnectivityManager: Class that answers queries about the state of network connectivity. It also notifies applications when network connectivity changes.
 */
class NetworkListener: ConnectivityManager.NetworkCallback() {
    //MutableStateFlow from kotlin corpoourtines. we need to specify a defaiult value
    private val isNetworkAvailable = MutableStateFlow(false) //este es como un livedata qeu va emitiendo valores a cuadno se da un cambio.

    /**
     * Create funciton  that has one parameter which is contet and return a mutableStateFlow
     * so this function will return true or false.
     *
     * This funciton will check if our device has internet coneection
     * and if it has we are going to asign true our isNetworkAvailable varieble  and then
     * return its value
     */
    fun checkNetworkAvailability(context: Context): MutableStateFlow<Boolean>{
        //retrieve a manager to habldle conectivity service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //we tell to the amanger to receive notifications about changes in the system default network. The callbacks will continue to be called until either the application exits or unregisterNetworkCallback(ConnectivityManager.NetworkCallback) is called
        connectivityManager.registerDefaultNetworkCallback(this)
        var isConnected = false
        //tell the amanger to Return an array of all Network currently tracked by the framework.
        //manager dime todas lsa formas de conexxion a als qeu ya esta conectado mi dispositivo
        connectivityManager.allNetworks.forEach {  network ->
            //check the connection cabability to internet of each  network that we have and check if we are online or not.
            val networkCapability = connectivityManager.getNetworkCapabilities(network) //si hay capacidad de conectarse a esa forma de red
            networkCapability?.let {
                //if there is a capability to internet connection
                if (it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)){ //de esa forma de conexxion, indicame si el dispositivo obtiene coenxxion. Hazme la conexcion
                    //Baciscally here we are checking if our device has intenet connectionand if it hs we want
                    //to set the value of our isConected variable to true. And retun it.
                    isConnected = true
                    return@forEach //devolvemos el resultado del foraech
                }
            }
        }
        isNetworkAvailable.value = isConnected
        return isNetworkAvailable
    }


    /**
     * Override two funciton to manage the internet coneection
     * this two functions will trigger when the network is available
     * and when is lost.
     */

    override fun onAvailable(network: Network) {
        isNetworkAvailable.value = true
    }

    override fun onLost(network: Network) {
        isNetworkAvailable.value = false
    }
}