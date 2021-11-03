package com.robertconstantindinescu.recipeaplication.di

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.robertconstantindinescu.recipeaplication.data.database.RecipesDao
import com.robertconstantindinescu.recipeaplication.data.database.RecipesDataBase
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


/**
 * this object will have the database injection
 * here we will tell hilt library hot to provide our own database nuilder and our recipes dao.
 */

@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {
    /**
     * this function return rom database builder
     */
    @Singleton
    @Provides //provides because is a thirdpartty class from library
    fun provideDatabase( //devuelve una instancia de la base de datos simpere la misma pq tenemos singleton
        @ApplicationContext context: Context  //this function has injected the context as a aparameter becasue we need it for the room builder
    ) = Room.databaseBuilder(
        context,
        RecipesDataBase::class.java,
        DATABASE_NAME

    ).build()

    /**
     * in order to provide our dao we need to provide the database dependency. lokk an aexample normal you will se
     */
    @Singleton
    @Provides
    //nos devuelve la interfaz dao
    fun provideDao(database: RecipesDataBase) = database.recipesDao() //we calll this function from RecipesDatabase here
}