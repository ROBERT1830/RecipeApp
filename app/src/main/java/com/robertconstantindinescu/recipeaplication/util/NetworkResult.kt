package com.robertconstantindinescu.recipeaplication.util

//this will be a sealed class.
/**
 * rememeber that a sealed class is used to store data in diferent states using diferent clases.
 * Here we wil hace 2 parameters. oNE WHICH WILL REPRESENT THE ACUTAL DATA FROM OUR API,
 * and the second one wich will represent a message. We will use generic for the type of data of the paramenters
 *
 * This class is used to pass a repsonse from our API
 */


/**
 * Se utiliza una sealed class para almacenar los datos, en este caso una respuesta del servidor en
 * tres estados iferentes. Se dispone de dos parametros, uno que son los datos de la respuesta del
 * servidor y un mensaje.
 *
 * NetworkResult, admitira cualquier tipo de clase, cualquiera de las tres qeu hemos definido abajo.
 */
sealed class NetworkResult<T>( //network
    val data: T? = null,
    val message: String? = null
) {

    class Succes<T>(data: T): NetworkResult<T>(data)
    class Error<T>(message: String?, data: T?= null): NetworkResult<T>(data, message)
    class Loading<T>: NetworkResult<T>()

}