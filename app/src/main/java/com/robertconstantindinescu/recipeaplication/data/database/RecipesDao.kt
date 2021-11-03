package com.robertconstantindinescu.recipeaplication.data.database

import androidx.room.*
import com.robertconstantindinescu.recipeaplication.data.database.entities.FavoritesEntity
import com.robertconstantindinescu.recipeaplication.data.database.entities.PersonalizedRecipeEntity
import com.robertconstantindinescu.recipeaplication.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface RecipesDao { //that need a database in which can operate
    /**
     * we will have 2 queries
     * 1- for inserting the data to recipesentity
     * 2-Fro reading from the entitiy class
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE) //that means that whenever we fetch new data from api, we want to replaec the old one. so that we have the newest data in our Recupeentity
    suspend fun insertRecipes(recipesEntity: RecipesEntity) //we use suspend function becasue later we use coroutines to run this query.

    //add the kotlinx flow not other
    /**
     * in that case instead of using livedata we use flow. This flow is gona be used inside the repositorio
     * but when we reach viewmodel then we will convert that flow into a live data. Flow is similar to livedata
     * the data inside the flow is circulating whenever we receive some new value
     *
     */
    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>


    /**
     * 19--Room FAVORITES
     *
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Query("SELECT * FROM favorite_recipes_table ORDER BY id ASC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()


    /**
     * ROOM PERSONALIZED RECIPES
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalizedRecipe(personalizedRecipeEntity: PersonalizedRecipeEntity)
    @Query("SELECT * FROM personalized_recipes_table ORDER BY id ASC")
    fun readPersonalizedRecipes(): Flow<List<PersonalizedRecipeEntity>>

}