package com.robertconstantindinescu.recipeaplication.data

import android.content.Context
import android.util.Log
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import com.robertconstantindinescu.recipeaplication.util.Constants
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.PREFERENCES_BACK_ONLINE
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.PREFERENCES_DIET_TYPE
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.PREFERENCES_DIET_TYPE_ID
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.PREFERENCES_MEAL_TYPE
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.PREFERENCES_MEAL_TYPE_ID
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.PREFERENCES_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject


/**
 * Esta clase se utiliza para almacenar las selecciones que se realizan en los chips.
 * la forma de almacenar datos es por medio de clave valor.
 */
@ActivityRetainedScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {


    /**
     * Nos definimos las claves, su nombre y el tipo de dato que van referenciar.
     */
    private object PreferenceKeys {
        val selectedMealType = preferencesKey<String>(PREFERENCES_MEAL_TYPE)
        val selectedMealTypeId = preferencesKey<Int>(PREFERENCES_MEAL_TYPE_ID)

        val selectedDietType = preferencesKey<String>(PREFERENCES_DIET_TYPE)
        val selectedDietTypeId = preferencesKey<Int>(PREFERENCES_DIET_TYPE_ID)
        /**Variables para almacenar los tipos de ingredientes*/

        val selectedMeatType = preferencesKey<String>(Constants.PREFERENCES_MEAT_TYPE)
        val selectedfMeatTypeId = preferencesKey<Int>(Constants.MEAT_TYPE_ID)
        val selectedVegetableType = preferencesKey<String>(Constants.PREFERENCES_VEGETABLE_TYPE)
        val selectedVegetableTypeId = preferencesKey<Int>(Constants.VEGETABLE_TYPE_ID)
        val selectedFishType = preferencesKey<String>(Constants.PREFERENCES_FISH_TYPE)
        val selectedFishTypeId = preferencesKey<Int>(Constants.FISH_TYPE_ID)
        /**Variable para almacenar el estado de red*/
        val backOnline = preferencesKey<Boolean>(PREFERENCES_BACK_ONLINE)

    }
    /**
     * Creamos la datastore inicandole un nombre.
     */
    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCES_NAME
    )
    /**
     * Esta función obtiene como parámetros una serie de datos que constituirán los valores
     * para las claves y los almacena en la dataStore preference.
     * la función edit es de suspensión por tanto DataStorePreferences está corriendo en un hilo secundario.
     * Por eso se necesita suspent.
     */
    suspend fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int){
        dataStore.edit { preferences -> //this is a mutablePreference Object. Usas el objeto MutablePreferences que es como un HashMap generico para almacenar en la datasotre las claves y su valor
            preferences[PreferenceKeys.selectedMealType] = mealType //here we specify the preference key one by one. and its value which is passed as arguments. The key will be asociated with its value
            preferences[PreferenceKeys.selectedMealTypeId] = mealTypeId
            preferences[PreferenceKeys.selectedDietType] = dietType
            preferences[PreferenceKeys.selectedDietTypeId] = dietTypeId

        }
    }

    /**
     * Idem pero para el apartado de recetas personalizadas.
     */
    suspend fun saveMeatVegyFishOtherType(
        meatType: String, meatTypeId: Int, vegetableType: String, vegetableTypeId:Int,
        fishType:String, fishTypeId: Int
    ){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.selectedMeatType] = meatType
            preferences[PreferenceKeys.selectedfMeatTypeId] = meatTypeId
            preferences[PreferenceKeys.selectedVegetableType] = vegetableType
            preferences[PreferenceKeys.selectedVegetableTypeId] = vegetableTypeId
            preferences[PreferenceKeys.selectedFishType] = fishType
            preferences[PreferenceKeys.selectedFishTypeId] = fishTypeId



        }
    }

    /**
     * ídem pero para el estado de red.
     */
    suspend fun saveBackOnline(backOnline: Boolean){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.backOnline] = backOnline
        }
    }
    /**
     * Con esta función vamos a leer de la base de datos. Por tanto esta variable se usara para leer
     * de la data store y su tipo de retorno debe de ser un Flow del tipo MealAndDietType para leer
     * y obtener los datos de forma secuencial.
     */
    val readMealAndDietType: Flow<MealAndDietType> = dataStore.data //Provides efficient, cached (when possible) access to the latest durably persisted state. The flow will always either emit a value or throw an exception encountered when attempting to read from disk.
        .catch { exeption ->
            if(exeption is IOException){
                emit(emptyPreferences())
            }else{
                throw exeption
            }

        }
        //here we retrieve the values from the data store using specific keys. map retunrs a flow
        //usando map vamos a obtener los valores del data store usando las calves especficadas anterirmente.
        //esto va a retornar un Flow con el tipo de dato MealAndDietType.
        //Además se especifica un valor por defecto de dicha clave en caso de no haber datos guardados para esa clave.
        .map { preferences -> //usando ese objeto preferences qeu es como un hashmap
            val selectedMealType = preferences[PreferenceKeys.selectedMealType] ?: DEFAULT_MEAL_TYPE
            val selectedMealTypeId = preferences[PreferenceKeys.selectedMealTypeId] ?: 0
            val selectedDietType = preferences[PreferenceKeys.selectedDietType] ?: DEFAULT_DIET_TYPE
            val selectedDietTypeId = preferences[PreferenceKeys.selectedDietTypeId] ?: 0
            //este es el objeto que creamos y devolvemos.
            MealAndDietType(
                selectedMealType,
                selectedMealTypeId,
                selectedDietType,
                selectedDietTypeId
            )

        }


    /**
     * ídem
     */
    val readMeatVegyFishOtherType: Flow<MeatVegyFishOtherType> = dataStore.data
        .catch { exeption ->
            if(exeption is IOException){
                emit(emptyPreferences())
            }else{
                throw exeption
            }

        }
        .map { preferences ->
            val selectedMeatType = preferences[PreferenceKeys.selectedMeatType] ?: Constants.DEFAULT_MEAT_TYPE
            val selectedMealTypeId = preferences[PreferenceKeys.selectedfMeatTypeId] ?: 0
            val selectedVegyType = preferences[PreferenceKeys.selectedVegetableType] ?: Constants.DEFAULT_VEGETABLE_TYPE
            val selectedVegyTypeId = preferences[PreferenceKeys.selectedVegetableTypeId] ?: 0
            val selectedFishType = preferences[PreferenceKeys.selectedFishType]?: Constants.DEFAULT_FISH_TYPE
            val selectedFishTypeId = preferences[PreferenceKeys.selectedFishTypeId]?: 0


            MeatVegyFishOtherType(
                selectedMeatType,
                selectedMealTypeId,
                selectedVegyType,
                selectedVegyTypeId,
                selectedFishType,
                selectedFishTypeId
            )


        }


    /**
     * ídem
     */
    val readBackOnline: Flow<Boolean> = dataStore.data

        .catch { exeption ->
            if(exeption is IOException){
                emit(emptyPreferences())
            }else{
                throw exeption
            }

        }
        .map { preferences ->
            Log.d("readBackOnline", "called")
            val backOnline = preferences[PreferenceKeys.backOnline] ?: false
            backOnline

        }
}

/**
 * Estas dos data class son relmente los tipos de dato que vamos a devolver en los flow.
 */
data class MealAndDietType(
    val selectedMealType: String, //chip text
    val selectedMealTypeId: Int, // chip id
    val selectedDietType: String,
    val selectedDietTypeId: Int
)

data class MeatVegyFishOtherType(
    val selectedMeatType: String,
    val selectedMeatTypeId: Int,
    val selectedVegyType: String,
    val selectedVegyTypeId: Int,
    val selectedFishType: String,
    val selectedFishTypeId: Int

    )