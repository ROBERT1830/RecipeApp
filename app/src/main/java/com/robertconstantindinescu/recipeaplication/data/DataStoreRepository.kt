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
 * in this class we a re going to store the litle data from the chip
 * we need to inject th aplicatioin context because we need that afor the data store
 * Data store is runnign on background thread (shared preferences in ui thread)
 */
@ActivityRetainedScoped //we use that because this datastorerepo will be used inside  or recipesViewModel
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {
    /**Define our keys. that we a re going to use in stpre preferences
     * using preferencesky and we have to define the String, i mena, the type of data.
     * and its name. And under that key we store inside this data store key im going to store
     * a value of a type string.
     * Rememeber that we want to save the name of that chip and its id so we need a key int
     * So when we open the dialog we need to read that id in order to read the data store preferences
     * and apply the selection in the dialog
     *
     * Store the values inside the constant class*/
    private object PreferenceKeys {
        val selectedMealType = preferencesKey<String>(PREFERENCES_MEAL_TYPE) //indicas el nomrbe de la key y que tipo de valor va a almacenar.
        val selectedMealTypeId = preferencesKey<Int>(PREFERENCES_MEAL_TYPE_ID) //indica el tipo de dato que tendra el valor de esta llave, no que la llave es un int.

        val selectedDietType = preferencesKey<String>(PREFERENCES_DIET_TYPE)
        val selectedDietTypeId = preferencesKey<Int>(PREFERENCES_DIET_TYPE_ID)
        /**Variables para almacenar los tipos de ingredientes*/

        val selectedMeatType = preferencesKey<String>(Constants.PREFERENCES_MEAT_TYPE)
        val selectedfMeatTypeId = preferencesKey<Int>(Constants.MEAT_TYPE_ID)
        val selectedVegetableType = preferencesKey<String>(Constants.PREFERENCES_VEGETABLE_TYPE)
        val selectedVegetableTypeId = preferencesKey<Int>(Constants.VEGETABLE_TYPE_ID)
        val selectedFishType = preferencesKey<String>(Constants.PREFERENCES_FISH_TYPE)
        val selectedFishTypeId = preferencesKey<Int>(Constants.FISH_TYPE_ID)



        /**
         * ADD ONE MORE KEY FOR THE INTERNET CONEXTION. this will be boolean key
         */
        val backOnline = preferencesKey<Boolean>(PREFERENCES_BACK_ONLINE)

    }

    /**Now proceed and create the datastore. Preferences es de android x
     * we hacve to use the context.createDagtasaroter inside which we place the name of the datastore*/

    private val dataStore: DataStore<Preferences> = context.createDataStore(
        name = PREFERENCES_NAME
    )

    /**Function to save values
     * as a aparameters are going to be the values.
     * inside that funcion we use the dataStore to save those values.
     * Take into account that edit function is a suspend function so we can think that datastore preferences
     * is runingn on background thread. So we need to declare the function as suspend
     *
     * Again this funciton will take the values (of the chips )from the parameters and we are going to store
     * this values in store preferences using the keys. */
    suspend fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int){
        dataStore.edit { preferences -> //this is a mutablePreference Object. Usas el objeto MutablePreferences que es como un HashMap generico para almacenar en la datasotre las claves y su valor
            preferences[PreferenceKeys.selectedMealType] = mealType //here we specify the preference key one by one. and its value which is passed as arguments. The key will be asociated with its value
            preferences[PreferenceKeys.selectedMealTypeId] = mealTypeId
            preferences[PreferenceKeys.selectedDietType] = dietType
            preferences[PreferenceKeys.selectedDietTypeId] = dietTypeId

        }
    }

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

    suspend fun saveBackOnline(backOnline: Boolean){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.backOnline] = backOnline
        }
    }

    /**Funciton to read from the data store
     * this variable is used to read from the data store preferences. and its type must be
     * Flow(android x coroutines) in order to read and get the data seccuencyally. Its type is the data class
     * So her ebassically when we are reading the values from the botttom sheet we are going to use
     * Flow to pass this class MealAndDietType WHICH CONTIANTNS the differente field we need to read
     *
     * RECUERDA QUE FLOW ES ALGO ASI COMO UN LIVE DATA EL VA EMITIENDO VALORES DE LA BASE DE DATOS A TIEMPO REAL, ES DINAMICO*/
    val readMealAndDietType: Flow<MealAndDietType> = dataStore.data //Provides efficient, cached (when possible) access to the latest durably persisted state. The flow will always either emit a value or throw an exception encountered when attempting to read from disk.
        .catch { exeption ->
            if(exeption is IOException){
                emit(emptyPreferences())
            }else{
                throw exeption
            }

        }
        //here we retrieve the values from the data store using specific keys. map retunrs a flow
        .map { preferences -> //usando ese objeto preferences qeu es como un hashmap
            //here we create 4 variable lfor the different values. for those values saved in the data store and then create a MealAndDietType pbject and emit that object using flow. Because the type of the flow is that data class we need to emit the same object
            //this line basically store a value in that variable. So using preferences[] we select the value of that specific
            //key and if there is no value saved for that specific key already then we emit or return  main course
            val selectedMealType = preferences[PreferenceKeys.selectedMealType] ?: DEFAULT_MEAL_TYPE
            val selectedMealTypeId = preferences[PreferenceKeys.selectedMealTypeId] ?: 0
            val selectedDietType = preferences[PreferenceKeys.selectedDietType] ?: DEFAULT_DIET_TYPE
            val selectedDietTypeId = preferences[PreferenceKeys.selectedDietTypeId] ?: 0
            //finally after we grabbed that data from our datasotre, we create MealAndDietType object out of this values obtained above

            //este es el objeto que creamos y deviovemos. no hace falta return, lo hace solo
            MealAndDietType(
                selectedMealType,
                selectedMealTypeId,
                selectedDietType,
                selectedDietTypeId
            )

        }


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

/**Create the data class that contain the real data inside. Is like room
 * we will use this class to pass al that values*/

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