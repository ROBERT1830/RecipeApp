package com.robertconstantindinescu.recipeaplication.models


import com.google.gson.annotations.SerializedName

/**
 * This class has nested the result class and the extended ingredients
 */
data class FoodRecipe(
    @SerializedName("results")
    val results: List<Result>,
)