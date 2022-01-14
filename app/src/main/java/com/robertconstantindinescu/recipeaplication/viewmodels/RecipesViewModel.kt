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
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.QUERY_INFORMATION
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.QUERY_NUMBER
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.QUERY_SEARCH
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.QUERY_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Esta clase, se utiliza para gestionar los endpoint de las diferetnes peticiones al servidor.
 *
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
     * Variables para el estado de la red.
     */
    var networkStatus = false
    var backOnline = false


    //the type of that variable will be  Flow<MealAndDietType> and we use that to read the datasTORE REPO AND USE the values to display then inside the queries and perfomr later the query. when press the fab select the chip and apply
    //lo que pasa aqui es que si nos fijamos readMealAndDietType es de tipo Flow<MealAndDietType>, que actua como una especie de live data y puede ser observable desde las activity o fragments

    /**
     * readMealAndDietType es de tipo Flow<MealAndDietType> actua como una especie de live data
     * y puede ser recolectado desde las activity o fragments
     */
    val readMealAndDietType = dataStoreRepository.readMealAndDietType
    val readMeatVegyFishOtherType = dataStoreRepository.readMeatVegyFishOtherType

    /**
     * con esto estamos recogiendo un flow del datarepository y entonces aqui lo convertimos en un livedata
     * que podra ser observado.
     */
    val readBackOnline =
        dataStoreRepository.readBackOnline.asLiveData()



    /**Esta función se encarga de realizar el guardado del tipo de comida y dieta cuando
     * hacemos una selección en el bottomsheet.
     * Para ello se ha utiliado el DataStore para almacenar ese conjunto de selecciones
     * como conjutnos de clave valor. */
    fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) =
        //Dispatchers.IO pq vamos a hacer operaciones en base de datos auque sea mas pequeña como la datastore preference
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMealAndDietType(mealType, mealTypeId, dietType, dietTypeId)
        }

    /**Función que utiliza el DataStore para almacenar el estado de la red. Al ser un proceso en el que
     * interviene almacenamiento en algún tipo de base de datos como DataStore, pues al usar
     * las corrutinas se especifica Dispatchers.IO*/
    fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }

    /**ídem pero para guardar la selección de los ingredientes en el apartado de recetas personalizadas
     * También utiliza dataStoreRepository*/
    fun saveMeatFishVegyType(
        meatType: String, meatTypeId: Int, vegetableType: String, vegetableTypeId: Int,
        fishType: String, fishTypeId: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMeatVegyFishOtherType(meatType, meatTypeId, vegetableType,
                vegetableTypeId, fishType, fishTypeId)
        }


    }


    /**
     * basically we call this function inside  getRecipes and that one will retunr a hasmap of all queries
     */
    /**
     * Función que se encarga de montar la query necesaria para hacer la petición al servidor.
     * Se utiliza un HasMap dónde tendremos las claves (parámetros) con su respectivo valor.
     *
     * @return ---> devolvemos la query formada que se usará para hacer la petición.
     */
    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        /**Mediante una corrutina, recoelctamos los datos flow que provienen del DataStore.
         * value es de tipo  MealAndDietType
         * Hasta que no se coleccionan los datos no se vuelve a emitir uno. Es decir el flow se
         * queda suspendido esperando a que su dato emitido en la clase DataStore sea coleecioando*/
        viewModelScope.launch {
            readMealAndDietType.collect { value ->

                //almacenamos los valores del tipo de comida y dieta seleccionados en el bottomsheet
                //en las variables globales.
                //Estas se utilizarán en las queries.
                mealType = value.selectedMealType
                dietType = value.selectedDietType

            }

        }
        //asociamos la clave con su valor y montamos la query.
        queries[QUERY_NUMBER] =
            DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_TYPE] = mealType
        queries[QUERY_DIET] = dietType
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"

        return queries
    }

    /**
     * Función que forma la query a partir del id de una receta. Se utiliza corrutinas para
     * recolectar los datos referentes a lla selección de los ingredientes.
     * Cuando se selecciona un ingrediente, se almacena la selección y aqui recolectamos ese dato.
     * @return ---> devolvemos la query basada en el id de una receta.
     */
    fun applyQueryById(): HashMap<String, String>{
        val queries: HashMap<String, String> = HashMap()
        viewModelScope.launch {
            readMeatVegyFishOtherType.collect { value ->
                meatType = value.selectedMeatType
                vegetablType = value.selectedVegyType
                fishType = value.selectedFishType

            }
        }
        queries["includeNutrition"] = "false"
        queries[QUERY_API_KEY] = API_KEY
        return queries

    }

    /**
     * Función que monta la query utilizando un string proveniente del cuadro de búsqueda.
     * Aqui no usamos mealtype ni nada, porque queremos que sea un resultado mas general
     * es decir todas als recetas que tengan un elemento que nosotors escribimos.
     * @return ---> devolvemos la query montada.
     */
    fun applySearchQuery(searchQuery: String): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()
        //specifie each and every query
        queries[QUERY_SEARCH] = searchQuery //here we had to ad a new constant
        queries[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        queries[QUERY_API_KEY] = API_KEY
        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"
        return queries
    }
    /**Función para montar la query con los ingredientes que selecciona el usuario
     * Por defecto se ponene los tipos de comida nada. De modo que si todos los campos
     * contienen nada selecionado, se efectúa una petición estándar.
     * @return ---> devolvemos la query*/
    fun applyPersonalizedRecipeQuery(): HashMap<String, String> {
        val query: HashMap<String, String> = HashMap()

        viewModelScope.launch {
            readMeatVegyFishOtherType.collect { value ->
                meatType = value.selectedMeatType
                if (meatType == "nothing") meatType = ""
                vegetablType = value.selectedVegyType
                if (vegetablType == "nothing") vegetablType = ""
                fishType = value.selectedFishType
                if (fishType == "nothing") fishType = ""

            }
        }
        var parameter: String = "${meatType},+${vegetablType},+${fishType}"

        query[Constants.QUERY_INGREDIENTS] = parameter
        query[QUERY_NUMBER] = DEFAULT_RECIPES_NUMBER
        query[QUERY_API_KEY] = API_KEY

        Log.d("query", query.toString())

        return query
    }
    /**
     * Función que comprueba el estado de red y muestra un mensaje en caso de que no haya
     * conexión. Pero si hubo algún momento en el que no hubo conexión y volvemos a tenerla
     * mostramos el mensaje correspondiente.
     * En función del estado de red, guardaremos un valor u otro en DataStore.
     * Si resulta que no tenemos red, guardaremos true para el estado de red. Es decir: "no tenemos
     * red por tnato guardamos backonline a true porque cuadno volvamos a tener red volveremos a estar
     * conectados a internet."
     * Por otro lado cuadno tengamos red, volvemos a poner backonline a false.
     */
    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No internet Connection.", Toast.LENGTH_SHORT).show()
            //when we lose our internet conenction and we want to set the value of that function to tru.
            saveBackOnline(true)
        } else if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(), "We are back online", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }
}