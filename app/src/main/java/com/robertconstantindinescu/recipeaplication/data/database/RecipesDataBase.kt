package com.robertconstantindinescu.recipeaplication.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.robertconstantindinescu.recipeaplication.data.database.entities.FavoritesEntity
import com.robertconstantindinescu.recipeaplication.data.database.entities.PersonalizedRecipeEntity
import com.robertconstantindinescu.recipeaplication.data.database.entities.RecipesEntity

/**
 * Esta clase define la propia base de datos. Para ello vamos a usar los anotadores room
 * para indicar que esta clase abstracta formara la propia base de datos.
 * Deberemos de indicar nuestras entidades, la version de la base de datos y si queremos o no
 * un historial del esquema.
 *
 * Esta es una clase abstracta debido a que cuando inyectamos la interfaz en el RemoteDataSource
 * esta debe de hacer uso de la base de datos sobre la que va a operar. De este modo siempre hay una
 * dependencia por asi decirlo entre el dao y la base de datos. De normal para hacer una acción
 * en la base de datos tenmoes qeu hacer lo siguiente: nombre_base_datos.recipesDao.readFavoriteRecipes
 * pero al haber hecho la inyección de dependencias en el modulo, ya proveemos la base de datos
 * al dao. De modo que cada vez que lo inyectamos hilt se va al modulo, busca la función que
 * devuelve el tipo inyectado y lo proporciona. Y si nos fijamos, en la función que devuelve el dao
 * ya proveemos la base de datos tambien.
 */
@Database(
    entities = [RecipesEntity::class, FavoritesEntity::class, PersonalizedRecipeEntity::class],
    version = 1,
    exportSchema = false  //no queremos historial. si lo pones a true room the crea un file con el historial de la base de datos. en este caso no queremos.
)
@TypeConverters(RecipesTypeConverter::class,PersonalizedRecipesTypeConverter::class )
abstract class RecipesDataBase: RoomDatabase() {
    abstract fun recipesDao(): RecipesDao

}