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

/**
 * Utilizamos un objeto para crear el módulo de dependencias necesarias para la interfaz Dao.
 * Para que la interfáz dao pueda realizar las funciones de cada uno de los métodos, necesita que
 * estos métodos operen sobre una base de datos.
 * De este modo usando inyección de dependencias vamos a definir la base de datos y esta se la vamos
 * a proveer al dao.
 *
 * la instancia de la base de datos sigue el patrón singleton. Para que solo tengmaos una sola instanfcia
 * de esta base de datos durante la vida de la aplicación.
 *
 * Como no podemos hacer una inyección directa por constructor de la base de datos room porque es
 * una libreria de terceros y tampoco de una interfáz debemos de crearnos un módulo para proveer ambos.
 *
 * Gracias a @Provide podemos proveer interfaces y librerias de terceros.
 */
@Module
@InstallIn(ApplicationComponent::class)
object DatabaseModule {

    /**
     * Esta función va a retornar una instancia de la base de datos, siemrpe la misma.
     * Se usa la inyección de  @ApplicationContext, porque el builder de room necesita de un
     * contexto.
     */
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        RecipesDataBase::class.java,
        DATABASE_NAME

    ).build()

    /**
     * in order to provide our dao we need to provide the database dependency. lokk an aexample normal you will se
     */
    /**
     * Función que provee el dao y a su vez provee al dao con la base de datos creada. Todo ello
     * para devolver la interfaz dao.
     */
    @Singleton
    @Provides
    fun provideDao(database: RecipesDataBase) = database.recipesDao()
}