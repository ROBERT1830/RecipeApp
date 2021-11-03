package com.robertconstantindinescu.recipeaplication.util

//this will be a sealed class.
/**
 * rememeber that a sealed class is used to store data in diferent states using diferent clases.
 * Here we wil hace 2 parameters. oNE WHICH WILL REPRESENT THE ACUTAL DATA FROM OUR API,
 * and the second one wich will represent a message. We will use generic for the type of data of the paramenters
 *
 * This class is used to pass a repsonse from our API
 */
sealed class NetworkResult<T>( //network admitira cualquier tipo de clase, cualquiera de las tres qeu hemos definido abajo.
    val data: T? = null,  //aui cuando es succes resulta que esa clase te devuelve un network class con la data de la api. entonces esta variable almacenara ese dato.
    val message: String? = null
) {
    //en el succes the data that wil be passes is never null so we dont need T?
    class Succes<T>(data: T): NetworkResult<T>(data) //will return network result after passing some data, the data is the performed request. data from NetworkResult is the same as succes
    //aqui se devuelve el netwok pero con los datos a null pq hay error pero con un mensaje string y es lo que almacena el networkresult.
    class Error<T>(message: String?, data: T?= null): NetworkResult<T>(data, message) //as a parameters we recive a message and allways put the data to null. this will retutn a data and a mesage
    class Loading<T>: NetworkResult<T>()

}