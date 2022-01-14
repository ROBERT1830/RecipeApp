package com.robertconstantindinescu.recipeaplication.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robertconstantindinescu.recipeaplication.models.FoodRecipe
import com.robertconstantindinescu.recipeaplication.models.Result
import com.robertconstantindinescu.recipeaplication.models.foodrecipepersonalized.PersonalizedFoodRecipe


/**
 * Idéntico al explicado para RecupesTypeConverter.
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


}