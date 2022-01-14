package com.robertconstantindinescu.recipeaplication.ui.fragments.recipes

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.robertconstantindinescu.recipeaplication.viewmodels.MainViewModel
import com.robertconstantindinescu.recipeaplication.R
import com.robertconstantindinescu.recipeaplication.adapters.RecipesAdapter
import com.robertconstantindinescu.recipeaplication.databinding.FragmentRecipesBinding
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.API_KEY
import com.robertconstantindinescu.recipeaplication.util.NetworkListener
import com.robertconstantindinescu.recipeaplication.util.NetworkResult
import com.robertconstantindinescu.recipeaplication.util.observeOnce
import com.robertconstantindinescu.recipeaplication.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recipes.view.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Tenemos que poner el anotador  pq en el viewmodel usamos inyeccion de dependencias para el repo
 * y los datasource tanto local como remoto.
 * Con lo cual se debe de cumplir que la clase que tiene inyectaas dependencias debe estar preparadas
 * al igual que las clases a inyectar.
 */

@AndroidEntryPoint
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener { //el SearchView debe ser el de appCompat


    //variable que almacena el argumento de tipo boolean que llega desde el dialog fragment.
    /*La clase RecipesFragmentArgs se crea automáticamente cuando especificamos que este fragment
    * va a necesitar tener un argumento devuelto para empezar su actividad.
    * Con lo cual usaremos la variable args para acceder a ese argumeto.
    * Este argumento lo usaremos en la función readDatabase() para hacer una comprobación previa a
    * realizar la lectura. */
    private val args by navArgs<RecipesFragmentArgs>()


    //Cuando usamos dataBinding, automáticamente se crean este tipo de clases para cada uno de
    /*los fragmentos. Esta clase  contendrá todas las vistas de esa layout allí almacenadas para que puedas
    acceder. Entonces _binding apunta a aesa clase. Se usara esa para modificar alguna de las vistas
    y binding solamente para leer. */
    private var _binding: FragmentRecipesBinding? = null  //cuando usas layout y por tanto databaindig en los layoput se crea una clase autoamticamente
    //variable de solo lectura.
    private val binding get() = _binding!!//solo lees esos elementos de la clase

    //Hacemos una intancia lateinit de ambos viewModels.
    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel



    //Inicialización lazy del adapter. siemrpe el adaptador se inicia despues pq sera luego cuando
    // le seteemos los datos
    private val mAdapter by lazy { RecipesAdapter() }


    /**NETWORK CONNECTION*/
    //Instanciamos la clase Networklistener para manejar la conexión a internet.
    private lateinit var networkListener: NetworkListener



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        //Instanciamos los viewModels. Usamos requireActivity para el contexto que nos devuelve
        //la activity a lla que está unido este fragment. En este momento se intentará leer de la
        //base de datos.
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        ////este tien que ver con las queries y con los chip no con sacar y meter datos en la base de datos.
        //Aquí se inicia el recipesviewmodel y ya se lee DataStorePreferences
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflamos el layout
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        /**When we bind our data with the xml layput we have to set the lifecyclerOwner.
         * because in our fragment recipes layput we are going to use life data object so this needs a lifecicler owner. */

        //Como en el layout vamos a usar dataBinding y uniremos a algunas vistas datos reactivos
        /*necesitaremos el ciclo de vida del padre, es decir de este fragment. */
        binding.lifecycleOwner = this
        //Unimos la variable mainViewModel del layout con el mainViewModel instanciado.
        binding.mainViewModel = mainViewModel

        //habilitamos las opciones el menú superior. Entonces tendremos que hacer un override
        //del método onCreateOptionsMenu()
        setHasOptionsMenu(true)

        setupRecyclerView()


        /**
         * La idea es que vemos a observar el estado de la red y almacenar ese estado en DataStorePreferences
         * De modo que cada vez que se vaya a iniciar la aplicación o navegamos de vuelta a este
         * fragment, se hara una lectura del estado de la red.
         * En este caso observamos la variable readBackOnline (livedata) y guardamos su valor
         * en una variable "backOnline" que usaremos para determinar el estado de la red.
         */
        recipesViewModel.readBackOnline.observe(viewLifecycleOwner, Observer {
            recipesViewModel.backOnline = it
        })


        /**
         * NETWORK---->
         * Initialize NetworklISTener to listeen its variable mutablestate flow
         * because collect is a suspend funciton we need to use cororutine
         * Heach time the onCreteView is triggered the internet conection will be cheked.
         */

        lifecycleScope.launch {
            /**Inicializamos el networkListener. Este nada mas ser inicializado, escucha el estado de
             * la red y en función de si hay conexión o no llamará a un metodo u otro que
             * ayudará a notificar el estaod de la red. */
            networkListener = NetworkListener()
            //call the funciton inside. If we foccus on the funciton we wiulll se that we have a returnning MutableStteFlo
            //which is a boolena. So this bassically will reteurn a aboolena.
            //so we need to collect that boolean here using the collect function. like we this in a normal flow

            /**Entonces tenemos que por un lado escuchamos una variable que cambia automáticamente
             * cuando cambia el estado de la red y coleccionamos su estado. Por otor lado, cada vez
             * que volvamos a este fragmetn se ejecuta este método checkNetworkAvailability
             * que colecciona los datos MutableStateFlow. */
            networkListener.checkNetworkAvailability(requireContext()).collect { status ->
                //log the status network
                Log.d("NetworkListener", status.toString())
                //cal the recipesviewModel and asign it networkStatus variable the to value of
                //checknetworkavailability funciton
                //usasa la clase recipes viewmodel pq es la qeu tiene que manejar todo eso
                /*A la variable networkStatus le asignamos el valor obtenido de la función, es
                * decir el estado. */
                recipesViewModel.networkStatus = status
                //Informamos al usuario del estado de la red.
                recipesViewModel.showNetworkStatus()

                //whenever the internet cahnges we are going to read the database.


                /*Siempre que sea posible vamos a leer de la base de datos local. Pero habrán casos
                * en los que deberemos de obtener datos del API. Estas situaciones son, cuadno
                * nuestra base de datos local está vacía o cuadno quremos hacer implícitamente
                * una petición al servidor. Que en este caso es cuadno presionamos el botón fab.
                * Fíjate que cada vez que se cabie la red, vamos a leer de la base de datos. */
                readDatabase()


            }
        }


        /**
         * Solo podemos navegar al fragment dialog cuadno tengamos conexión de red.
         */
        binding.recipesFab.setOnClickListener {
            if(recipesViewModel.networkStatus){
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet) // con el controlador navegamos y le ponemos la accion que hemos indicado con la flecha en el my_nav.
            }else{
                recipesViewModel.showNetworkStatus()
            }

        }


        //devolvemos el root del bindig.
        return binding.root



    }

    /**
     * Seteamos el recycler con el adapter, el layoutmanager. E iniciamos el shimmer.
     */
    private fun setupRecyclerView(){
        binding.recyclerview.adapter = mAdapter //el adapter se inicia
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffec()
    }

    /**
     * Creamos la barra del menú que contendrá un simple search menu
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //inflamos el menu pasando el recurso menu que hemos creado y diseñado.
        inflater.inflate(R.menu.recipes_menu, menu)
        //buscamos el menu_search dentro de ese menu.
        val search = menu.findItem(R.id.menu_search)
        //indicamos que search es una view de tipo search
        val searchView = search.actionView as? SearchView
        //habilitar que el propio icono actúe como submit par ahacer la busqueda
        searchView?.isSubmitButtonEnabled = true
        //ahora le asociamos un listener.
        searchView?.setOnQueryTextListener(this) //we have to extends this framgne with SarchView.onWQUERYLEISGTENER
    }

    /**
     * Hay dos oveeride que podemos usar, uno para cuadno clicas una vez introducida la palabara
     * y el otro para cuadno vas metiedno palabras y te sale a teimpo real la información actualizada
     * Es decir que se hace la petición al servidor de froma contínua. He utilizado la priemra opción
     */
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null){
            //llalmamos al servidor con la query. Que es un string.
            searchApiData(query)
        }

        return true

    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun readDatabase() {

        //coroutine is attatched to the life of the fragment.
        lifecycleScope.launch {

            //observamos la variable livedata readRecipes que se actualiza conforme se hagan cambios en
            //la base de datos.
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner, Observer { database ->
                /**si la base de datos no esta vacia y no venimos del dialogo es decir el args es false
                 * entonces es cuando mostramos los datos de la base de datos. y no de la api.
                 * pero en cambio si tu vines de haber sleecionado algo en el bottomsheet dialog entonces
                 * vas a hacer una nueva request al api con nuevos valores*/
                if (database.isNotEmpty() && !args.backFromBottomSheet){ //means that the database is not empty and have some data so we want to display in the reculcerview.
                    Log.d("RecipesFragment", "readDatabase called!")
                    mAdapter.setData(database[0].foodRecipe) //allways we get only one row. so we can acces foodrecipe
                    hideShimmerEffect()
                }else { //Si la base de datos está vacía, hacemos petición al servidor.
                    /**Aqui se llega siempre y cuadno hemos entrado en el bottom sheet y hemos hecho una nueva peticion al api*/
                    requestApiData()
                }
            })
        }

    }

    /**
     * function to fetch data from api.
     * Call the funciton getRecipes form the viewModel and this function will get the data from the api
     * and it will store the response inside the mutablelivedata. So we want to observe this livedata.
     */
    /**
     * Esta función la utilizamos para coger datos de la API.
     */
    private fun requestApiData(){
        Log.d("RecipesFragment", "requestApiData called!")
        //primero construimos la query en recipesViewModel y con eso hacemos la petición
        mainViewModel.getRecipes(recipesViewModel.applyQueries())
        //observamos la variable recipesResponse cuyo valor cambia en función del la respuesta que
        //recibimos del servidor.
        mainViewModel.recipesResponse.observe(viewLifecycleOwner, Observer { response ->
            //tenemos que la respuesta puede estar en tres estados diferentes.
            when(response){
                //si obtenemos datos informamos al adapter.
                is NetworkResult.Succes ->{
                    //ocultamos el shimmer porque tenemos datos.
                    hideShimmerEffect()
                    //get acces to the food recipe
                    //accedemos a los datos NetworkResult y si hay datos seteamos el adapter.
                    response.data?.let {
                        mAdapter.setData(it) }
                }
                //si obtenemos un error cargamos los datos de la cache.
                is NetworkResult.Error ->{
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(), response.message.toString(),
                        Toast.LENGTH_SHORT).show()
                }
                //minetras se estan cargando los datos, mostramos el shimmer.
                is NetworkResult.Loading -> {
                    showShimmerEffec()
                }
            }
        })
    }
    /**
     * Esta funcion se llama desde onQueryTextSubmit cuando buscamos por palabra. Entonces cuando
     * apretamos el botón de buscar se lllama esta función.
     */

    private fun searchApiData(searchQuery: String){
        showShimmerEffec()
        //montamos lla query y hacemos la busqeuda al servidor.
        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery)) //here we pass the queries and that will be created in recipesviewmodel
        //observamos la respuesta.
        mainViewModel.searchRecipesResponse.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                //si el body tiene datos seteamos el adapter.
                is NetworkResult.Succes ->{

                    hideShimmerEffect()
                    //otra forma de hacer lo mismo que antes pero con un paso mas. Para practicar
                    val foodRecipe = response.data
                    foodRecipe?.let {
                        mAdapter.setData(it) }
                }
                //si la respuesta es un error se cargan datos locales y se muestra un mensaje,
                is NetworkResult.Error ->{
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(), response.message.toString(),
                        Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffec()
                }
            }
        })
    }


    /**
     * Funcion LoadDta from cache.
     * Imaginate que tiens datos en la base de datos pero ahora tu solicitas unos nuevos, pero resutla que
     * hay un error en la comunicacion con la api
     * pues en ese caso vamos a enseñarle al usuario los datos antiguos y alamacenados en la base datos.
     * De este modo el usuario no vera un recuclerview vacio y siempre tendra datos.
     */

    private fun loadDataFromCache(){
        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner, Observer { database ->
                if (database.isNotEmpty()){
                    mAdapter.setData(database[0].foodRecipe)
                }
            })
        }

    }




    /**
     * Function to show and hide shimmer effect from recycler view.
     * Funciones para mostrar y ocultar el shimmer.
     */
    private fun showShimmerEffec(){
        //get the recylcerview using the view inflated
        binding.recyclerview.showShimmer()
    }

    private fun hideShimmerEffect(){
        binding.recyclerview.hideShimmer()
    }

    override fun onDestroy() {
        super.onDestroy()
       //cuadno se destruye el fragmento tambien destruimos la cllase binding con las vistas
        //para evitar leaks de memoria.
        _binding = null
    }

}