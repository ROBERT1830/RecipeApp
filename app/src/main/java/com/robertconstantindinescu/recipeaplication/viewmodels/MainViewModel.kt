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

/**
 * Esta clase al necesitar contener Application, extiende de AndroidViewModel.
 * Inyectamos con  @ViewModelInject el repo.
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

    //variable que se usa para leer los datos de las recetas almacenadas en local.
    /*De la base de datos obtenemos un flow, pero aqui lo pasamos a un liveData.
    * para escuchar el estado de la variable desde el fragment. */
    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readRecipes().asLiveData()

    //??dem
    val readPersonalizedRecipes: LiveData<List<PersonalizedRecipeEntity>> = repository.local.readPersonalizedRecipes().asLiveData()

    //??dem
    val readFavoriteRecipes: LiveData<List<FavoritesEntity>> = repository.local.readFavoriteRecipes().asLiveData()

    val sortByName: LiveData<List<FavoritesEntity>> = repository.local.sortByName().asLiveData()


    /**
     * M??todo utilizado para insertar en la base de datos las recetas que obtenemos del api.
     */
    private fun insertRecipes(recipesEntity: RecipesEntity) =
        //we use the dispatcher io becasue we perform operation with database
        viewModelScope.launch(Dispatchers.IO) {
            //usamos el repo local e insertamos las recetas con la funci??n definida en esa clase.
            repository.local.insertRecipes(recipesEntity)
        }

    /**
     * M??etodo utilizado para insertar en la base de dagos las recetas personalizadas.
     */
    private fun insertPersonalizedRecipes(personalizedRecipes: PersonalizedRecipeEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            //??dem
            repository.local.insertPersonalizedRecipes(personalizedRecipes)
        }

    /**
     * M??todo usado para insertar recetas en la tabla favoritos.
     */
    fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertFavoriteRecipes(favoritesEntity)
        }

    /**
     * M??odo usado para eliminar una receta espec??fica de favoritos.
     */
    fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteFavoriteRecipe(favoritesEntity)
        }

    /**
     * M??todo para eliminar todas las recetas favoritas.
     */
    fun deleteAllFavoriteRecipes() =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteAllFavoriteRecipes()
        }

    fun sortByName()  =
        repository.local.sortByName()

    fun sortByTimeDuration() = repository.local.sortByTimeDuration()

    fun orderByLikes() = repository.local.orderByLikes()

    /** RETORFIT */

    //variable livedata que se leera desde el fragmentRecipes. Tendra toda la lsita de recetas obtenidas
    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData() //becasue we are goint to get the data from the NetWorkResult class souch succes witch can have inside a generic type we must have our mutablelivedata with NetworkResult<FoodRecipe

    //variable livedata que tendr?? la lista de recetas obtenidas del api cuadno hacemos b??squeda por
    //palabras.
    var searchRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    //variabel para observar los datos desde el fragmetn cuadno hagamos una busqueda personalizada
    var personalizedRecipeResponse: MutableLiveData<NetworkResult<PersonalizedFoodRecipe>> = MutableLiveData()
    //Variable que almacena un objeto PersonalizedRecipeResult cuando hacemos b??squeda por id
    var recipeResponseById: MutableLiveData<NetworkResult<PersonalizedRecipeResult>> = MutableLiveData()


    /**
     * M??todo que devuelve las recetas a partir de la respuesta del api. Cuando lanzamos esta corrutina
     * se crea un job que lo que hace es definir el contexto y ciclo de vida de la corrutina, de tal
     * modo que la vida de la corrutina acabar?? cuando acabe la vida del vireModel.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    /**
     * ??dem pero necesitamos pasar el Id de la receta en cuesti??n y el enpoint.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun getRecipesById(recipeId: Int, queryEndPoint: Map<String, String>) = viewModelScope.launch {
        getRecipesByIdSafeCall(recipeId,queryEndPoint )
    }

    /**
     * ??dem pero para cuadno realizamos una b??squeda por palabra.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun searchRecipes(searchQuery:Map<String, String> ) = viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    /**
     * ??dem pero para cuando realizamos b??squeda personallizada.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun getPersonalizedRecipe(personalizedQuery: Map<String, String>) = viewModelScope.launch {
        getPersonalizedRecipesSafeCall(personalizedQuery)
    }

    /**
     * En este m??todo siempre que haya conexi??n a internet, realizar?? la petici??n a la bnase de datos
     * para una receta en particular. Luego modifica el valor de recipeResponseById
     * en funci??n de tres estados de los datos. Estos estados se determinan en la funci??n
     * handleFoodRecipesIdResponse.
     * Si no hay conexi??n el valor es un mensaje de error.
     * Al ser solo una receta no se hace persistencia en la base de datos.
     *
     */
    @RequiresApi(Build.VERSION_CODES.M)
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


    /**
     * Este m??todo, si tenemos conexi??na internet haremos una petici??n a la API
     * Y luego cambiar?? el valor de la variable livedata personalizedRecipeResponse
     * en funci??n del estado de la respiesta del servidor.
     * Cuadno se cambia el valor de dicha variable, inmediatamente se escucha desde la vista
     * y pinta los datos.
     * Pero inmediatametne este m??todo guarda en local los datos. Para no tener que hacer peticiones
     * seguidas a la base de datos.
     */
    @RequiresApi(Build.VERSION_CODES.M)
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


    /**
     * ??dem pero para recetas generales.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        if (hasInternerConnection()){
            try {
                val response = repository.remote.getRecipes(queries) //---> WARNING: we need suspend function because getRecipes is a suspend funciton
                // Succes devuevle un Network con un tipo <T> con lo cual gracias a eso podemos igualar NetworkResult<FoodRecipe>  al tipo de dato que tenemos devuelto
                recipesResponse.value = handleFoodRecipesResponse(response)
                //accedes al Network que tiene almacenado y almacenas en sa varible la respuesta
                // que es realmete un objeto de la clase FoodRecipes
                val foodRecipe = recipesResponse.value!!.data
                if(foodRecipe != null){
                    offlineCacheRecipes(foodRecipe)
                }
            }catch (e: Exception){
                recipesResponse.value = NetworkResult.Error("Recipes not found. ")
            }
        }else{
            recipesResponse.value = NetworkResult.Error("No Internet Connection.") //here we need to pass some parameters lke the message but not the data becasue we seted to null in case of error
        }
    }

    /**
     * ??dem pero no hacemos el guardado en la base de datos cuadno buscamos solo por una palabra.
     *
     */
    @RequiresApi(Build.VERSION_CODES.M)
    suspend private fun searchRecipesSafeCall(searchQuery: Map<String, String>) {

        searchRecipesResponse.value = NetworkResult.Loading()

        if (hasInternerConnection()){
            try {
                val response = repository.remote.searchRecipes(searchQuery)
                searchRecipesResponse.value = handleFoodRecipesResponse(response)

            }catch (e: Exception){
                searchRecipesResponse.value = NetworkResult.Error("Recipes not found. ")
            }

        }else{

            searchRecipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    /**
     * este m??todo, guarda en la base de datos las recetas personaliazdas.
     * Para ello crea un objeto de la entidad correspondiente que tenemos en la bse de datos,
     * a partir del objeto que pasamos.
     */
    private fun offlineCachePersonalizedRecipes(foodRecipe: PersonalizedFoodRecipe) {
        /**Nos creamos el objeto de nuestra entidad*/
        val personalizedFoodEntity = PersonalizedRecipeEntity(foodRecipe)
        insertPersonalizedRecipes(personalizedFoodEntity)


    }

    /**
     * ??dem pero para recetas generales.
     */
    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        //aqui te vas a crear tu propio objeto de la clase ReipesEntity que contiene dentro la
        // clase FoodRecipe con la lista de result y le meteras los resulatdos pq le pasas por
        // constructor un objeto de la clase FoodRecipe

        val recipesEntity = RecipesEntity(foodRecipe)
        //ahora vamos a insertar los datos en room
        insertRecipes(recipesEntity)
    }



    /**
     * Este m??todo, se encarga de distribuir la informaci??n que obtenemos de la api
     * a un tipo de dato en concreto. Si obtenemos alg??n tipo de c??digo de error
     * o mensaje devolveremos un objeto NetworkResult con ell tipo de dato en cuesti??n.
     */
    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe>? {
        when{

            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 ->{
                return NetworkResult.Error("Api key Limited. ")
            }
            response.body()!!.results.isNullOrEmpty() ->{
                return NetworkResult.Error("Recipes not found.")
            }
            response.isSuccessful ->{
                val foodRecipes = response.body()
                return  NetworkResult.Succes(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }

    }
    private fun handleFoodRecipesIdResponse(response: Response<PersonalizedRecipeResult>): NetworkResult<PersonalizedRecipeResult>? {
        when{
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 ->{
                return NetworkResult.Error("Api key Limited. ")
            }
            response.isSuccessful ->{
                val foodRecipes = response.body()
                return  NetworkResult.Succes(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun handlePersonalizedFoodRecipesResponse(response: Response<PersonalizedFoodRecipe>):
            NetworkResult<PersonalizedFoodRecipe>? {
        when{
            //mensaje http
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 ->{
                return NetworkResult.Error("Api key Limited. ")
            }
            response.body()!!/*.results*/.isNullOrEmpty() ->{
                return NetworkResult.Error("Recipes not found.")
            }
            response.isSuccessful ->{
                val foodRecipes = response.body()
                return  NetworkResult.Succes(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
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
    /**
     * Esta funci??n comprueba la conexi??n a internet para poder realizar la petici??n al servidor.
     *
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasInternerConnection(): Boolean{
        //obtengo la application class para comprobar la conexi??n y con getSystemService devuelvo
        //un ConnectivityManager para gestionar la conexi??n.
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        )as ConnectivityManager

        //activeNetwork nos devuelve un objeto Network o falso si no hay red.
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        // Lo que se hace es coger e identificar la capacidad de red, es decir si hay o no capacidad de
        // tener red con el objeto network que identifica la red y que pasamos como parmetro. Si con esa
        // red de internet no tenemos capacidad de conexion devuelveme falso.
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        //se coge ese objeto network y se usa para ver que tipo de conexion estamos teniendo
        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false



        }
    }
}