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

/**
 * Esta clase la utilizamos para escuchar de forma continua la conexión de red y notificar cuando se
 * pierde la conexión. Para ello debemos de extender de ConnectivityManager.NetworkCallback().
 */
class NetworkListener: ConnectivityManager.NetworkCallback() {
    //MutableStateFlow from kotlin corpoourtines. we need to specify a defaiult value
    //se utiliza MutableStateFlow que permanece activo en memoria y  va emitiendo valores.
    //en este caso valores bolelanos.
    private val isNetworkAvailable = MutableStateFlow(false)

    /**
     * Esta función compruba si nuestro dispositivo tiene conexión a internet. Si tiene conexión,
     * asignamos true a isNetworkAvailable y devolvemos su valor.
     *
     *
     * @return --> devolvemos el valor de isNetworkAvailable.
     */
    fun checkNetworkAvailability(context: Context): MutableStateFlow<Boolean>{
        //Obtemeos un ConnectivityManager para manejar los estados de conexión.
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        //hacemos que el manager reciba la notificación sobre los cambios de red en el sistema.
        connectivityManager.registerDefaultNetworkCallback(this)
        var isConnected = false
        //tell the amanger to Return an array of all Network currently tracked by the framework.
        //manager dime todas lsa formas de conexxion a als qeu ya esta conectado mi dispositivo
        //con esto decimos: manager dime todas las formas de conexion a las que ya esta conectado mi dispositivo
        connectivityManager.allNetworks.forEach {  network ->
            //comprobar la capacidad de conexión a internet
            val networkCapability = connectivityManager.getNetworkCapabilities(network)
            networkCapability?.let {
                //si tenemos capacidad para conectarse a internet
                if (it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)){ //de esa forma de conexxion, indicame si el dispositivo obtiene coenxxion. Hazme la conexcion
                    //seteamos el valor de la variable a true.
                    isConnected = true
                    return@forEach //devolvemos el resultado del foraech
                }
            }
        }
        isNetworkAvailable.value = isConnected
        return isNetworkAvailable
    }
    /**
     * estos dos métodos, se van a disparar cuando el sistema detecta que hay red o no.
     * En cada caso, devolveremos el valor de isNetworkAvailable según si hay red o no.
     */
    override fun onAvailable(network: Network) {
        isNetworkAvailable.value = true
    }

    override fun onLost(network: Network) {
        isNetworkAvailable.value = false
    }
}