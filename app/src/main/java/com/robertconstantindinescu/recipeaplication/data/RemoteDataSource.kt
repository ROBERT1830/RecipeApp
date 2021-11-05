package com.robertconstantindinescu.recipeaplication.data

import com.robertconstantindinescu.recipeaplication.data.network.FoodRecipesApi
import com.robertconstantindinescu.recipeaplication.models.FoodRecipe
import com.robertconstantindinescu.recipeaplication.models.PersonalizedRecipeResult
import com.robertconstantindinescu.recipeaplication.models.Result
import com.robertconstantindinescu.recipeaplication.models.foodrecipepersonalized.PersonalizedFoodRecipe
import retrofit2.Response
import javax.inject.Inject


/**
 * This class request the data from  the api. And for that we need to inject the
 * interface FoodRecipesApi inside this class so that we can request new data from api
 * For that we are going to use dependecy injection.
 * This remotedasoruce fetch the data from the food api. And the second datasource will be a local data soruce which wil handle local databse.
 * Ofcoruse we are going to have one repository where we are going to inject those two data sources.
 *
 * Here by anotating the construcotr with @Inject  and specicfiing thi type which we wat to inject
 * hilt will search for this specific type in the module and it will search for a funtion wich return this same type
 */
class RemoteDataSource @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi //inyectamos la interfaz api con todas sus dependencias ya creadas
) {

    //Map is used to make different requests at hte same time.
    /*Becasue getRecipes is s suspned function because perfom a api request
    * this function that will call getRecipes needs to be suspend as well. And both
    * will run in a separate thread. */

    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe>{
        return foodRecipesApi.getRecipes(queries)
    }

    suspend fun  searchRecipes(searchQuery: Map<String, String>): Response<FoodRecipe>{
        return foodRecipesApi.searchRecipes(searchQuery)
    }
    suspend fun getPersonalizedRecipe(personalizedQuery: Map<String, String>):
            Response<PersonalizedFoodRecipe>{
        return foodRecipesApi.getPersonalizedRecipe(personalizedQuery)

    }
    suspend fun getRecipeById(recipeId: Int, queryEndPoint: Map<String, String>):
            Response<PersonalizedRecipeResult>{
        return foodRecipesApi.getRecipeById(recipeId, queryEndPoint)

    }





}