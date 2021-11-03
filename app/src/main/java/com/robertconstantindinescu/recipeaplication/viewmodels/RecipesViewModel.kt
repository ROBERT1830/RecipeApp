package com.robertconstantindinescu.recipeaplication.viewmodels

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.robertconstantindinescu.recipeaplication.data.DataStoreRepository
import com.robertconstantindinescu.recipeaplication.util.Constants
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.API_KEY
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.QUERY_API_KEY
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.QUERY_DIET
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.QUERY_NUMBER
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.QUERY_SEARCH
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.QUERY_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * only used to share the api end points and make clenaear the Recipefragment. This class wiil be shread betwen the recipesGragment and RecipesBottomSheet
 */
class RecipesViewModel @ViewModelInject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) : AndroidViewModel(application) {

    private var mealType = DEFAULT_MEAL_TYPE
    private var dietType = DEFAULT_DIET_TYPE

    /**Variables para los ingredientes*/
    private var meatType = Constants.DEFAULT_MEAT_TYPE
    private var vegetablType = Constants.DEFAULT_VEGETABLE_TYPE
    private var fishType = Constants.DEFAULT_FISH_TYPE
    private var otherIngredientsType = Constants.DEFAULT_OTHER_INGREDIENT_TYPE

    /**
     * NETWORK--->
     * create a variable network sataus
     */
    var networkStatus = false
    var backOnline = false




    //the type of that variable will be  Flow<MealAndDietType> and we use that to read the datasTORE REPO AND USE the values to display then inside the queries and perfomr later the query. when press the fab select the chip and apply
    //lo que pasa aqui es que si nos fijamos readMealAndDietType es de tipo Flow<MealAndDietType>, que actua como una especie de live data y puede ser observable desde las activity o fragments
    val readMealAndDietType = dataStoreRepository.readMealAndDietType
    //function to save mealanddiet type. will have 4 parameters. Which will represents  the 4 values we defined inside the datastore repository

    // TODO: 2/11/21 CONTINUAR CON  readMeatVegyFishOtherType DEL DATASTORE
    val readMeatVegyFishOtherType = dataStoreRepository.readMeatVegyFishOtherType

    /**
     * con esto estamos recogiendo un flow del datarepository y entonces aui lo convertimos en un livedata
     * que podra ser observado.
     */
    val readBackOnline = dataStoreRepository.readBackOnline.asLiveData() //convertimos el flow en un livedata observable. Fijste que cuadno hemos trabajado ocn SttteFlow no lo hemos hecho



    /**Fijate que aqui no vamos del viremodel a un repo y a la datastore. Pq esta data sotre hace como de repo tamien por eso se llama datastoreRepo*/
    fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) =
        //Dispatchers.IO pq vamos a hacer operaciones en base de datos auque sea mas pequeña como la datastore preference
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMealAndDietType(mealType, mealTypeId, dietType, dietTypeId)
        }

    fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }




    /**
     * basically we call this function inside  getRecipes and that one will retunr a hasmap of all queries
     */
    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        /**launcha coroutine to collect the values from that particular flow. The flow is the comunication or read action with
         * the datasource
         * el colelct te recoge este dato del flow que es un MealAndDietType*/
        viewModelScope.launch {
            readMealAndDietType.collect { value -> /**Hasta que no se coleccionan los datos no se vuelve a emitir uno. Es decir el flow se queda suspendido epseranod a que su dato emitido sea coleecioando*/
                //now store the value in both up variables
                //esta d variabels van a toamr el valor de la datastore preference repo directametne
                //y ahora vamos a coger y usar esas variables en el hasmap del query
                mealType = value.selectedMealType
                dietType = value.selectedDietType

            }

        }



        //asociate the [key] with its value // all of them are in the query string http...
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER  //this is the number of recipes we will get from the request
        queries[QUERY_API_KEY] = API_KEY
        //her ewe will get the new values for the endpoints and if there is no data inside that variable we will perform a default value because we specicy it in the .map inside the datastore rEPOSITORY
        queries[QUERY_TYPE] = mealType
        queries[QUERY_DIET] = dietType
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    fun applySearchQuery(searchQuery: String): HashMap<String, String>{
        val queries: HashMap<String, String> = HashMap()
        //specifie each and every query
        queries[QUERY_SEARCH] = searchQuery //here we had to ad a new constant
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"
        return queries
        /**
         * Aqui no usamos mealtype ni nada, pq quermeos que sea un resultado mas general
         * es decir todas als recetas que tengan un elemento que nosotors escribimos.
         */


    }
    /**Query para hacer la búsqueda personalizada*/
    fun applyPersonalizedRecipeQuery(): HashMap<String, String>{
        val query: HashMap<String, String> = HashMap()

        viewModelScope.launch {
            readMeatVegyFishOtherType.collect { value ->
                meatType = value.selectedMeatType
                vegetablType = value.selectedVegyType
                fishType = value.selectedFishType
                otherIngredientsType = value.selectedOtherIngrType
            }
        }

        var parameter: String = "${meatType},+${vegetablType},+${fishType},+${otherIngredientsType}"

        query[Constants.QUERY_INGREDIENTS] = parameter
        query[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        query[QUERY_API_KEY] = API_KEY

        Log.d("query", query.toString())

        return query
    }

    /**
     * Netwirk --->
     * here we want to check if the value of our networksatatus if it is
     * then display a a toasmessge seing no intenet coneection
     */

    fun showNetworkStatus(){
        if (!networkStatus){
            Toast.makeText(getApplication(), "No internet Connection.", Toast.LENGTH_SHORT).show()
            //when we lose our internet conenction and we want to set the value of that function to tru.
            saveBackOnline(true)
        }else if(networkStatus){
            if (backOnline){
                Toast.makeText(getApplication(), "We are back online", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }
}