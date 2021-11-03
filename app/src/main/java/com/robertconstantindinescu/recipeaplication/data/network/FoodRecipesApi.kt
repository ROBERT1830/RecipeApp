package com.robertconstantindinescu.recipeaplication.data.network

import com.robertconstantindinescu.recipeaplication.models.FoodRecipe
import com.robertconstantindinescu.recipeaplication.models.foodrecipepersonalized.PersonalizedFoodRecipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 * with this interface we are going to get out GET requests. for specific endpoints
 */
interface FoodRecipesApi {

    //https://api.spoonacular.com/recipes/complexSearch?number=1&apiKey=5dbcf751c6eb41ad9cd3023b93a41499&type=drink&diet=vegan&addRecipeInformation=true&fillIngredients=true
    /**
     * this is the url from where we get the information. We hace already defined in the GET
     * the endpoint but we need to put the queries we need inside the funciont
     * There are two ways of doing that
     * 1) add one by one the parameters of the query or 2) use @QueryMap ---> this allow us to create
     * like a hashmap that allow us to create the queries at once
     *
     * This function returns a FoodRecipe model class where inside is placed a list of result for each recipe
     * We need to wrap this object FoodRecipe inside a Response class (is a class used for http response
     * ). This is part from retrofit
     * so we are going to add the foodrecipe clas inside that response clas and
     *
     * When we try to use this funciton we will specify the queries in a hasmap.
     * The function wil use kotlin koroutines and will run in a background thread.
     */
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


}