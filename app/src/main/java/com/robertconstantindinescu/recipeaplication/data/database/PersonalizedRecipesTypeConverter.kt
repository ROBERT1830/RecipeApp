package com.robertconstantindinescu.recipeaplication.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robertconstantindinescu.recipeaplication.models.FoodRecipe
import com.robertconstantindinescu.recipeaplication.models.Result
import com.robertconstantindinescu.recipeaplication.models.foodrecipepersonalized.PersonalizedFoodRecipe

/**
 * we wil have 2 funcitons. 1 wich will convert the recipe to string and the second
 * wichc will convert the string from the database in foodRecipe Object for use it
 */
class PersonalizedRecipesTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun <T>foodRecipeToString(foodRecipe: PersonalizedFoodRecipe):String{
        //we use a GSON library to serialize the object that we are passing.
        return gson.toJson(foodRecipe) //here we are returning a JSON string. because we use the gson library to convert the object to string


    }

    @TypeConverter
    fun stringToFoodRecipe(data: String): PersonalizedFoodRecipe {
        //thi wil convert the string from the database to a FoodRecipe object
        val listType = object : TypeToken<PersonalizedFoodRecipe>() {}.type
        return gson.fromJson(data, listType)
    }


//    /**
//     * 19---ROOM FAVORITES
//     */
//    @TypeConverter
//    fun resultToString(result: Result): String{
//        return gson.toJson(result)
//    }
//    @TypeConverter
//    fun stringToResult(data: String): Result {
//        val listType = object : TypeToken<Result>() {}.type
//        return gson.fromJson(data, listType)
//    }
}