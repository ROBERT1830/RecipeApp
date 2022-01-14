package com.robertconstantindinescu.recipeaplication.data.database

import androidx.room.*
import com.robertconstantindinescu.recipeaplication.data.database.entities.FavoritesEntity
import com.robertconstantindinescu.recipeaplication.data.database.entities.PersonalizedRecipeEntity
import com.robertconstantindinescu.recipeaplication.data.database.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow

/**
 * Esta interfáz la vamos a tener que definir como Dao, puesto que contendrá todos aquellos
 * métodos que realizaran acciones con la base de datos.
 */
@Dao
interface RecipesDao { //that need a database in which can operate

    /**
     * Función que realiza el insert de recetas en la base de datos.
     * OnConflictStrategy.REPLACE reemplazará los datos en la base de datos cada vez que hagamos una
     * nueva petición en la base de datos. De este modo tendremos una única columna con los datos
     * siempre actualizados.
     *
     * se utiliza funciones de suspension puesto que este método será llamado desde una corrutina.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    /**
     * Función que genera la petición de recetas a la base de datos. Marcamos la funcion con
     * la etiqueta @Query. Devolveremos un objeto Flow con la lista de objetos necesaria.
     * Un flow es como un livedate, pero va emitiendo valores de forma secuencial cada vez que haya un
     * cambio en la base de datos.
     */
    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Query("SELECT * FROM favorite_recipes_table ORDER BY id DESC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    /**
     * Método que realiza la eliminación de una receta en particular.
     */
    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    /**
     * Método que realiza la eliminacion de todas las recetas de la tabla favoritos.
     */
    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPersonalizedRecipe(personalizedRecipeEntity: PersonalizedRecipeEntity)
    @Query("SELECT * FROM personalized_recipes_table ORDER BY id ASC")
    fun readPersonalizedRecipes(): Flow<List<PersonalizedRecipeEntity>>

    @Query("SELECT * FROM favorite_recipes_table ORDER BY name ASC")
    fun sortByName(): Flow<List<FavoritesEntity>>

    @Query("SELECT * FROM favorite_recipes_table ORDER BY readyInMinutes ASC")
    fun sortByTimeDuration(): Flow<List<FavoritesEntity>>

    @Query("SELECT * FROM favorite_recipes_table ORDER BY aggregateLikes DESC")
    fun orderByLikes(): Flow<List<FavoritesEntity>>

}