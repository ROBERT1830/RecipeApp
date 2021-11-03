package com.robertconstantindinescu.recipeaplication.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.robertconstantindinescu.recipeaplication.data.database.entities.FavoritesEntity
import com.robertconstantindinescu.recipeaplication.data.database.entities.PersonalizedRecipeEntity
import com.robertconstantindinescu.recipeaplication.data.database.entities.RecipesEntity

/**
 * 19--room fav
 */
@Database(
    entities = [RecipesEntity::class, FavoritesEntity::class, PersonalizedRecipeEntity::class],
    version = 1,
    exportSchema = false  //no queremos historial. si lo pones a true room the crea n file con el historial de la base de datos. en este caso no queremos.
)
@TypeConverters(RecipesTypeConverter::class,PersonalizedRecipesTypeConverter::class )
abstract class RecipesDataBase: RoomDatabase() {
    abstract fun recipesDao(): RecipesDao

    /**
     * we are going to create the database builder in the module part in di package because we use di.
     * we will create a module in di for the builder.
     *
     */



}