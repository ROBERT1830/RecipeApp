package com.robertconstantindinescu.recipeaplication.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robertconstantindinescu.recipeaplication.models.FoodRecipe
import com.robertconstantindinescu.recipeaplication.models.Result

/**
 * Esta clase se utiliza para hacer conversiones de objetos complejos. Resulta que en sql es complicado
 * guardar objetos complejos como por ejemplo un Objeto que a su vez contiene objetos y listas
 * como sucede con la respuesta en formato JSON que nos llega del servidor.
 * En tales casos, usaremos Typeconverter. De modo que cada vez que room detecte que tiene que
 * guardar un objeto complejo como es el caso de FoodRecipe, llamará a la función determinada
 * para serializar ese JSON y poder guardarlo en la base de datos como un string JSON.
 *
 * Estas conversiones las haremos con GSON, una libreria de google.
 */
class RecipesTypeConverter {

    var gson = Gson()

    /**
     * Función que convierte el objeto complejo a string.
     */
    @TypeConverter
    fun foodRecipeToString(foodRecipe: FoodRecipe):String{
        //we use a GSON library to serialize the object that we are passing.
        return gson.toJson(foodRecipe) //here we are returning a JSON string. because we use the gson library to convert the object to string


    }

    /**
     * Función que realiza el paso contrario cuadno el objeto se saca de la basd de datos.
     * convertimos el JSON serializado a un objeto complejo.
     */
    @TypeConverter
    fun stringToFoodRecipe(data: String): FoodRecipe{
        //thi wil convert the string from the database to a FoodRecipe object
        val listType = object : TypeToken<FoodRecipe>() {}.type
        return gson.fromJson(data, listType)
    }


    /**
     * Idem para ambas funciones que siguen.
     */
    @TypeConverter
    fun resultToString(result: Result): String{
        return gson.toJson(result)
    }
    @TypeConverter
    fun stringToResult(data: String): Result{
        val listType = object : TypeToken<Result>() {}.type
        return gson.fromJson(data, listType)
    }
}