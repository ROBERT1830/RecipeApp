package com.robertconstantindinescu.recipeaplication.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robertconstantindinescu.recipeaplication.models.FoodRecipe
import com.robertconstantindinescu.recipeaplication.models.Result

/**
 * we wil have 2 funcitons. 1 wich will convert the recipe to string and the second
 * wichc will convert the string from the database in foodRecipe Object for use it
 */
class RecipesTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun foodRecipeToString(foodRecipe: FoodRecipe):String{
        //we use a GSON library to serialize the object that we are passing.
        return gson.toJson(foodRecipe) //here we are returning a JSON string. because we use the gson library to convert the object to string


    }

    @TypeConverter
    fun stringToFoodRecipe(data: String): FoodRecipe{
        //thi wil convert the string from the database to a FoodRecipe object
        val listType = object : TypeToken<FoodRecipe>() {}.type
        return gson.fromJson(data, listType)
    }


    /**
     * 19---ROOM FAVORITES
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