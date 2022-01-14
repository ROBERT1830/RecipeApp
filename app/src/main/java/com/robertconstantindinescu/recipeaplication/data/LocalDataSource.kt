package com.robertconstantindinescu.recipeaplication.data

import com.robertconstantindinescu.recipeaplication.data.database.RecipesDao
import com.robertconstantindinescu.recipeaplication.data.database.entities.FavoritesEntity
import com.robertconstantindinescu.recipeaplication.data.database.entities.PersonalizedRecipeEntity
import com.robertconstantindinescu.recipeaplication.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Esta clase es la que se comunicará con la base de datos local. Para ello necesita tener inyectado
 * la interfaz dao.
 * Esta clase contiene todos los métodos necesarios praa hacer inserciones y peticiones a la
 * base de datos.
 */
class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao
) {

    /**
     * Métos que hacen una petición a la base de datos local usando el dao.
     * La query devuelve un flow, que más tarde convertiremos en livedata en mainViewModel
     */
    fun readRecipes(): Flow<List<RecipesEntity>>{
        return recipesDao.readRecipes()
    }

    fun readPersonalizedRecipes():Flow<List<PersonalizedRecipeEntity>>{
        return recipesDao.readPersonalizedRecipes()
    }
    //leemos las recetas gurdados en la base de datos de favoritos.
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>{
        return recipesDao.readFavoriteRecipes()
    }

    /**
     * Los dos métodos que vienen son para insertar recetas y recetas personalizas. Cada una en su
     * base de datos corresponiente.
     */
    suspend fun insertRecipes(recipesEntity: RecipesEntity){
        recipesDao.insertRecipes(recipesEntity)
    }
    suspend fun insertPersonalizedRecipes(personalizedRecipeEntity: PersonalizedRecipeEntity){
        recipesDao.insertPersonalizedRecipe(personalizedRecipeEntity)
    }

    suspend fun insertFavoriteRecipes(favoritesEntity: FavoritesEntity){
        recipesDao.insertFavoriteRecipe(favoritesEntity)
    }


    /**
     * Métodos para eliminar recetas de la tabla de favoritos. Definen dos acciones posibles
     * eliminar recetas especificas o todas las recetas de la tabla.
     */
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity){
        recipesDao.deleteFavoriteRecipe(favoritesEntity)
    }

    suspend fun deleteAllFavoriteRecipes(){
        recipesDao.deleteAllFavoriteRecipes()
    }

    fun sortByName():Flow<List<FavoritesEntity>> =
          recipesDao.sortByName()

    fun sortByTimeDuration(): Flow<List<FavoritesEntity>> = recipesDao.sortByTimeDuration()
    fun orderByLikes(): Flow<List<FavoritesEntity>> = recipesDao.orderByLikes()



}