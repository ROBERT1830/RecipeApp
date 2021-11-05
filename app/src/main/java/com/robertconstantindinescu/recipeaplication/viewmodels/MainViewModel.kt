package com.robertconstantindinescu.recipeaplication.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.robertconstantindinescu.recipeaplication.data.Repository
import com.robertconstantindinescu.recipeaplication.data.database.entities.FavoritesEntity
import com.robertconstantindinescu.recipeaplication.data.database.entities.PersonalizedRecipeEntity
import com.robertconstantindinescu.recipeaplication.data.database.entities.RecipesEntity
import com.robertconstantindinescu.recipeaplication.models.FoodRecipe
import com.robertconstantindinescu.recipeaplication.models.PersonalizedRecipeResult
import com.robertconstantindinescu.recipeaplication.models.Result
import com.robertconstantindinescu.recipeaplication.models.foodrecipepersonalized.PersonalizedFoodRecipe
import com.robertconstantindinescu.recipeaplication.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

/**
 * THis class extends biewmodel but since we are gpint to need  aplication reference in this viremodel
 * we use diferent one AndroidViewModel. Remeber that  the aplication class is that one from where the app starts and if we focuse on the amnifes
 * there we defined our Myaplication which is were are placed the hilt components. So because of that,
 * and becasue here in viewmodel we will use injectDepencey as well @Viewmodel.. we need acces to the
 * aplication to save this new hilt component. Becasue each time we put an anotation @authorin a alcas hilt
 * creates a component and store in muyaplciation class.
 *
 * Now inject the repository insithe the viemodel using viewmodelInject
 *
 * Identifies a androidx.lifecycle.ViewModel's constructor for injection.
Similar to javax.inject.Inject, a ViewModel containing a constructor annotated with ViewModelInject
will have its dependencies defined in the constructor parameters injected by Dagger's Hilt.

We do not need to crete a viewmodel facotry to pass the repository
 */

class MainViewModel @ViewModelInject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    /**ROOM DATABASE */

    /**
     * here we are going to use local datasource to get my room database.
     * so we call  the database function readDatabase from our interface dao
     * and then create a funciton to insert the data into the database.
     */
    //THIS variable is gona be used in the fragment to read the database instead of call the api each time.
    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readRecipes().asLiveData()  //we need to convert the flow to a live data. THIS

    val readPersonalizedRecipes: LiveData<List<PersonalizedRecipeEntity>> = repository.local.readPersonalizedRecipes().asLiveData()

    val readFavoriteRecipes: LiveData<List<FavoritesEntity>> = repository.local.readFavoriteRecipes().asLiveData()



    private fun insertRecipes(recipesEntity: RecipesEntity) =
        //we use the dispatcher io becasue we perform operation with database
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity)
        }

    private fun insertPersonalizedRecipes(personalizedRecipes: PersonalizedRecipeEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertPersonalizedRecipes(personalizedRecipes)
        }

    /**
     * 19 room favor
     */

    fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavoriteRecipes(favoritesEntity)
        }

    fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteRecipe(favoritesEntity)
        }

    fun deleteAllFavoriteRecipes() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllFavoriteRecipes()
        }




    /** RETORFIT */
    //Create the mutablelive data object
    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData() //becasue we are goint to get the data from the NetWorkResult class souch succes witch can have inside a generic type we must have our mutablelivedata with NetworkResult<FoodRecipe
    /**Search--->*/
    var searchRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    //create the function to get the recipes. and launc a coroutine to do the work in a background thread
    /**Nueva variable para hacer la b´´queda personalizada*/
    var personalizedRecipeResponse: MutableLiveData<NetworkResult<PersonalizedFoodRecipe>> = MutableLiveData()

    var recipeResponseById: MutableLiveData<NetworkResult<PersonalizedRecipeResult>> = MutableLiveData()

    /**
     * this coroutine when we launch this it will creates a job that handles the live of that cororutine
     * and the live of that coroutine is bases here according to the viewmodel life.
     */
    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    fun getRecipesById(recipeId: Int, queryEndPoint: Map<String, String>) = viewModelScope.launch {
        getRecipesByIdSafeCall(recipeId,queryEndPoint )
    }

    private suspend fun getRecipesByIdSafeCall(recipeId: Int, queryEndPoint: Map<String, String>) {
        recipeResponseById.value = NetworkResult.Loading()
        if (hasInternerConnection()){
            try {
                val response = repository.remote.getRecipeById(recipeId, queryEndPoint)
                recipeResponseById.value = handleFoodRecipesIdResponse(response)
            }catch (e: java.lang.Exception){
                recipeResponseById.value = NetworkResult.Error("Recipes not found. ")
            }
        }else{
            recipeResponseById.value = NetworkResult.Error("No Internet Connection.")
        }
    }


    /**Search--->*/
    fun searchRecipes(searchQuery:Map<String, String> ) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    fun getPersonalizedRecipe(personalizedQuery: Map<String, String>) = viewModelScope.launch {
        getPersonalizedRecipesSafeCall(personalizedQuery)
    }

    private suspend fun getPersonalizedRecipesSafeCall(personalizedQuery: Map<String, String>) {
        personalizedRecipeResponse.value = NetworkResult.Loading()
        if (hasInternerConnection()){
            try {
                val response = repository.remote.getPersonalizedRecipe(personalizedQuery)
                personalizedRecipeResponse.value = handlePersonalizedFoodRecipesResponse(response)
                // TODO: 2/11/21 Crearme mi propia entidad para poder guardar los datos de la api en local de este modoo cada vez que vayamos a este fragment nos saldra los datos de la bd que guardamos por ultima vez.
                val foodRecipe = personalizedRecipeResponse.value!!.data
                if (foodRecipe != null){
                    offlineCachePersonalizedRecipes(foodRecipe)
                }
            }catch (e: java.lang.Exception){
                personalizedRecipeResponse.value = NetworkResult.Error("Recipes not found. ")
            }
        }else{
            personalizedRecipeResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }


    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        //every time we call this funtion we response first with the load state data and then we we get the data we responthe either with succes o error.
        recipesResponse.value = NetworkResult.Loading()
        //si tenemos internet we will make a request to our api. And we wil store the request response inside
        //the recipesResponse variable
        if (hasInternerConnection()){
            try {


                //here we will use the injected repository to get the api datare.
                /*so because of the injection we can acces the repository and then the remote datasource
                * because we injected the repository with the remotedasource and finally getRecipes*/
                val response = repository.remote.getRecipes(queries) //---> WARNING: we need suspend function because getRecipes is a suspend funciton
                //now set recipesResponse value to the value we get from the request. ya tenemos los datos alamacenado en esaa liceda de la api
                //ahora le decimos a nuestro live data ei que tu valor va a cambiar. Pero resulat que de la request podemos obtener diferentes respuestas (dato en diferentes estados y por eso usamos la clase Networ sealed class), entre ellas como un json con error messsage entonces en funcion de como va a se esa respuesta tenemos que hacer una cosa u otr.
                recipesResponse.value = handleFoodRecipesResponse(response) //si recordamos del Succes devuevle un Network con un tipo <T> con lo cual gracias a eso podemos igualar NetworkResult<FoodRecipe>  al tipo de dato que tenemos devuelto
                /**Catche the data inmediatly when we receive from api. */
                val foodRecipe = recipesResponse.value!!.data //accedes al Network que tiene almacenado y almacenas en sa varible la respuesta que es realmete un objeto de la clase FoodRecipes
                if(foodRecipe != null){ //chek if the response is not null
                    offlineCacheRecipes(foodRecipe) //cache the api response into the database.
                }


            }catch (e: Exception){
                recipesResponse.value = NetworkResult.Error("Recipes not found. ")
            }
            //if has internet coneection is false that menas tha toour app dont have aacces to intener and we need to pass error emsage here
        }else{
            //if there is an error we are seting the mutable recupesReposnse to the networkresult  error
            recipesResponse.value = NetworkResult.Error("No Internet Connection.") //here we need to pass some parameters lke the message but not the data becasue we seted to null in case of error
        }
    }

    suspend private fun searchRecipesSafeCall(searchQuery: Map<String, String>) {

        searchRecipesResponse.value = NetworkResult.Loading()

        if (hasInternerConnection()){
            try {
                val response = repository.remote.searchRecipes(searchQuery)
                searchRecipesResponse.value = handleFoodRecipesResponse(response)


                /**
                 * Here we are not going to cache the data
                 */
                //val foodRecipe = searchRecipesResponse.value!!.data
//                if(foodRecipe != null){ //chek if the response is not null
//                    offlineCacheRecipes(foodRecipe) //cache the api response into the database.
//                }


            }catch (e: Exception){
                searchRecipesResponse.value = NetworkResult.Error("Recipes not found. ")
            }

        }else{

            searchRecipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }
    private fun offlineCachePersonalizedRecipes(foodRecipe: PersonalizedFoodRecipe) {
        /**Nos creamos el objeto de nuestra entidad*/
        val personalizedFoodEntity = PersonalizedRecipeEntity(foodRecipe)
        insertPersonalizedRecipes(personalizedFoodEntity)


    }
    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        //aqui te vas a crear tu propio objeto de la clase ReipesEntity que contiene dentro la clase FoodRecipe con la lista de result
        //y le meteras los resulatdos pq le pasas por constructor un objeto de la clase FoodRecipe
        /**
         * in order to insert the data into the database we need to convert the FoodRecipe from the api
         * into an database entity in that case recipesEntity
         */
        val recipesEntity = RecipesEntity(foodRecipe) //entonces esta variable contiene un objeto de la clase RecipeseNTTITY que contiene dentro un objeto de tipo FooDrecipe que a asu vez tiene una lista de Result
        //ahora vamos a insertar los datos en room
        insertRecipes(recipesEntity)
    }


    /**
     * this funciton will take response from out PI. and here we are going to parse the response from the api
     */
    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe>? {
        when{
            //if the message that we have from the request is timeoout that means the it take to long for the api to respond to our request
            //mensaje http
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout") //da igual si retornar este tipo de netwok con el erro pq lo hemos peusto en generico
            }
            response.code() == 402 ->{
                return NetworkResult.Error("Api key Limited. ") // becasue the pooneculaar api has a limit in tthe request
            }
            response.body()!!.results.isNullOrEmpty() ->{ //pone !! pq el body puede ser null o vacio
                return NetworkResult.Error("Recipes not found.") //alguans veces puede apdasr que te devuevlea la query un resutlao vacoi con lo que eso signitifa que no hay resultado para los paramteros de busqeuda indicados.


            }
            response.isSuccessful ->{
                val foodRecipes = response.body() //el cuerpo de la respuesta guardamelo en esa variable
                return  NetworkResult.Succes(foodRecipes!!) //devolvemos la respuesta almacenada en un tipo de dato en particualr. //estamos pasando data de tipo FoodRECIPE PERO EN NETWORK TU puedes alamcenar en data cualquie data pq usamos T
            }
            else -> {
                return NetworkResult.Error(response.message()) //get the messge from the api
            }
        }
        //siemrpe have to return the Network tipe daata becase the mutable live data is like this.

    }
    private fun handleFoodRecipesIdResponse(response: Response<PersonalizedRecipeResult>): NetworkResult<PersonalizedRecipeResult>? {
        when{
            //if the message that we have from the request is timeoout that means the it take to long for the api to respond to our request
            //mensaje http
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout") //da igual si retornar este tipo de netwok con el erro pq lo hemos peusto en generico
            }
            response.code() == 402 ->{
                return NetworkResult.Error("Api key Limited. ") // becasue the pooneculaar api has a limit in tthe request
            }

//            response.body()!!.isNullOrEmpty() ->{ //pone !! pq el body puede ser null o vacio
//                 //alguans veces puede apdasr que te devuevlea la query un resutlao vacoi con lo que eso signitifa que no hay resultado para los paramteros de busqeuda indicados.
//
//
//            }
            response.isSuccessful ->{
                val foodRecipes = response.body() //el cuerpo de la respuesta guardamelo en esa variable
                return  NetworkResult.Succes(foodRecipes!!) //devolvemos la respuesta almacenada en un tipo de dato en particualr. //estamos pasando data de tipo FoodRECIPE PERO EN NETWORK TU puedes alamcenar en data cualquie data pq usamos T
            }
            else -> {
                return NetworkResult.Error(response.message()) //get the messge from the api
            }
        }
        //siemrpe have to return the Network tipe daata becase the mutable live data is like this.

    }

    private fun handlePersonalizedFoodRecipesResponse(response: Response<PersonalizedFoodRecipe>):
            NetworkResult<PersonalizedFoodRecipe>? {

        when{
            //if the message that we have from the request is timeoout that means the it take to long for the api to respond to our request
            //mensaje http
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout") //da igual si retornar este tipo de netwok con el erro pq lo hemos peusto en generico
            }
            response.code() == 402 ->{
                return NetworkResult.Error("Api key Limited. ") // becasue the pooneculaar api has a limit in tthe request
            }
            response.body()!!/*.results*/.isNullOrEmpty() ->{ //pone !! pq el body puede ser null o vacio
                return NetworkResult.Error("Recipes not found.") //alguans veces puede apdasr que te devuevlea la query un resutlao vacoi con lo que eso signitifa que no hay resultado para los paramteros de busqeuda indicados.


            }
            response.isSuccessful ->{
                val foodRecipes = response.body() //el cuerpo de la respuesta guardamelo en esa variable
                return  NetworkResult.Succes(foodRecipes!!) //devolvemos la respuesta almacenada en un tipo de dato en particualr. //estamos pasando data de tipo FoodRECIPE PERO EN NETWORK TU puedes alamcenar en data cualquie data pq usamos T
            }
            else -> {
                return NetworkResult.Error(response.message()) //get the messge from the api
            }
        }




    }

    /**
     * Create a funtion that checks for internet connection
     *
     * ConnectivityManager--->
     * ---> Class that answers queries about the state of network connectivity. It also notifies applications when network connectivity changes.
    The primary responsibilities of this class are to:
    Monitor network connections (Wi-Fi, GPRS, UMTS, etc.)
    Send broadcast intents when network connectivity changes
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasInternerConnection(): Boolean{
        /*to check if the app has internet conection firts we get the application class
        * and the getSystemService return A ConnectivityManager for handling management of network connections.*/
        //
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        )as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false // activeNetwork--->Returns a Network (si tienes coenxion) object corresponding to the currently active default data network This will return null when there is no default network.
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false  // lo que hace es coger e identificar la capacidad de red, es decir si hay o no capacidad de tner red con el objeto network que identifica la red y que pasamos cmo parmetro. Si con esa red de internet no tenemos capacidad de conexion devuelveme falso.
        //se coge ee objeto networ y se usa pra ver que tipo de conexxion estamos teniendo
        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false



        }
    }
}