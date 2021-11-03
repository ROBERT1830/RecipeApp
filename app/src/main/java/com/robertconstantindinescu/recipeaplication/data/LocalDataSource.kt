package com.robertconstantindinescu.recipeaplication.data

import com.robertconstantindinescu.recipeaplication.data.database.RecipesDao
import com.robertconstantindinescu.recipeaplication.data.database.entities.FavoritesEntity
import com.robertconstantindinescu.recipeaplication.data.database.entities.PersonalizedRecipeEntity
import com.robertconstantindinescu.recipeaplication.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * in this class we are going to inject the dao
 * This local datasource will contaian all the queries from our recipes because we have injected it
 * For reading the database we use a flow. but int he mainviewmodel we will change that to livedata.
 */
class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao //qui le inyectamos la interfaz pero que relamente a esta le hemos proporcionado la base de datos sore la que operar.
) {
    //here we dond need suspend
     fun readRecipes(): Flow<List<RecipesEntity>>{ //te devulve los datos envueltos en un flow. Entonces tu cuadno te hagas el flow builder es cuadno los vas a ir sacando y emitiendo.
        return recipesDao.readRecipes()
    }


    /**
     * create a function for inserting the recipes into the local database
     * we will insert the entire list
     * so inside we call inserRecipes from the dao.
     */
    suspend fun insertRecipes(recipesEntity: RecipesEntity){
        recipesDao.insertRecipes(recipesEntity)
    }
    suspend fun insertPersonalizedRecipes(personalizedRecipeEntity: PersonalizedRecipeEntity){
        recipesDao.insertPersonalizedRecipe(personalizedRecipeEntity)
    }
    fun readPersonalizedRecipes():Flow<List<PersonalizedRecipeEntity>>{
        return recipesDao.readPersonalizedRecipes()
    }

    /**
     * 19-ROOM FAVORITES
     */

    //read favorites recipes
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>{ //te devulve los datos envueltos en un flow. Entonces tu cuadno te hagas el flow builder es cuadno los vas a ir sacando y emitiendo.
        return recipesDao.readFavoriteRecipes()
    }

    suspend fun insertFavoriteRecipes(favoritesEntity: FavoritesEntity){
        recipesDao.insertFavoriteRecipe(favoritesEntity)
    }

    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity){
        recipesDao.deleteFavoriteRecipe(favoritesEntity)
    }

    suspend fun deleteAllFavoriteRecipes(){
        recipesDao.deleteAllFavoriteRecipes()
    }
}