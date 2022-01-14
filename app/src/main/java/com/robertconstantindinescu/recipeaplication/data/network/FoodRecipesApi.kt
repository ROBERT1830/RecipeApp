package com.robertconstantindinescu.recipeaplication.data.network

import com.robertconstantindinescu.recipeaplication.models.FoodRecipe
import com.robertconstantindinescu.recipeaplication.models.PersonalizedRecipeResult
import com.robertconstantindinescu.recipeaplication.models.Result
import com.robertconstantindinescu.recipeaplication.models.foodrecipepersonalized.PersonalizedFoodRecipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * with this interface we are going to get out GET requests. for specific endpoints
 */
/**
 * Esta interfaz la usareomos para hacer las peticiones al servidor mediante el método GET.
 * cada una de las funciones contienen los endpoints necesarios para hacer la petición.
 * Además tienen los parámetros necesarios para hacer la petición a modo de mapa con su clave y valor
 * Todas las funciones van a devolver un objeto Response con el modelo de datos .
 *
 * los objetos response son los que usamos para almacenar y devovler la respuestas http.
 */
interface FoodRecipesApi {

    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries: Map<String, String>
    ): Response<FoodRecipe>

    @GET("/recipes/complexSearch")
    suspend fun searchRecipes(
        @QueryMap searchQuery: Map<String, String>
    ): Response<FoodRecipe>

    @GET("/recipes/findByIngredients")
    suspend fun getPersonalizedRecipe(
        @QueryMap personalizedSearch: Map<String,  String>
    ):Response<PersonalizedFoodRecipe>

    @GET("/recipes/{id}/information")
    suspend fun getRecipeById(
        @Path("id") recipeId:Int,
        @QueryMap parameters: Map<String, String>
    ):Response<PersonalizedRecipeResult>


}