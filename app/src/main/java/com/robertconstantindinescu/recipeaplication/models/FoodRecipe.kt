package com.robertconstantindinescu.recipeaplication.models


import com.google.gson.annotations.SerializedName

/**
 * This class has nested the result class and the extended ingredients
 */
/**
 * Este es el modelo principal que tiene el JSON obtenido.
 * En el JSON vamos a tener una lista con toodas los resultados, es decir las recetas.
 */
data class FoodRecipe(
    @SerializedName("results")
    val results: List<Result>,
)