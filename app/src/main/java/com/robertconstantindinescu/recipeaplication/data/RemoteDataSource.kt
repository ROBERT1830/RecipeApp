package com.robertconstantindinescu.recipeaplication.data

import com.robertconstantindinescu.recipeaplication.data.network.FoodRecipesApi
import com.robertconstantindinescu.recipeaplication.models.FoodRecipe
import com.robertconstantindinescu.recipeaplication.models.PersonalizedRecipeResult
import com.robertconstantindinescu.recipeaplication.models.Result
import com.robertconstantindinescu.recipeaplication.models.foodrecipepersonalized.PersonalizedFoodRecipe
import retrofit2.Response
import javax.inject.Inject



/**
 * Esta clase se encarga de hacer las peticiones al servidor. Para necesitamos inyectar FoodRecipesApi
 * que realiza tal función.
 * Annotando el constructor con @Inject, hará que hilt busque en el modulo esa dependencia. Es decir
 * buscara una función cuyo retorno es lo que le estamos pidiento (una interfáz FoodRecipesApi).
 */
class RemoteDataSource @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
) {

    /**
     * Todas estas funcoines se encargan de hacer una peticion al servidor y devolvernos una
     * respuesta con el tipo determinado en cada caso. Todas son similares. Difiere
     * el tipo de parametros que se pasan en función del tipo de búsqueda.
     */

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