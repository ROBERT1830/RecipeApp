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
 * Tenemos que poner esto pq en el viewmodel usamos inyeccion de dependencias. Con lo cual se debe de cumplir
 * que la clase que tiene inyectaas dependencias debe estar preparadas asl igual aeu las clases a inyectar
 *
 * Y como mainactivity hold the recipe fragment we need to anootate the same
 */
@AndroidEntryPoint //search debe de set el de appcompat no el otro sino no funciona.
class RecipesFragment : Fragment(), SearchView.OnQueryTextListener {
    /**variable that store the Argument from the dialog fragment  ---> RecipesFragmentArgs is automatically
     * added when we specify the argument in my_nav in the section arguments
     * we will use this args to acces this argument wichh we ahve specified in the navigation graph.
     * we will use it in readDatabase function*/
    private val args by navArgs<RecipesFragmentArgs>()




    //bind the Fragmentlayout
    private var _binding: FragmentRecipesBinding? = null  //cuando usas layout y por tanto databaindig en los layoput se crea una clase autoamticamente que contendra todas las vistas de esa layout  alli almacenadas par aque puedas acceder. Entonces _binding apunta a aesa clase
    private val binding get() = _binding!!//solo lees esos eleemtnos de la clase

    private lateinit var mainViewModel: MainViewModel
    //declare the other viewmodel RecipesViewModel
    private lateinit var recipesViewModel: RecipesViewModel



    private val mAdapter by lazy { RecipesAdapter() } //siemrpe el adaptador se inicia despues pq sera luego cuando le seteemos los datos.
    //private lateinit var mView: View

    /**NETWORK CONNECTION*/
    //Instanciate the newtworklistener class
    private lateinit var networkListener: NetworkListener


    //INSIDE this method is where we inicialize the viewMODELEs in order to make the onCreateView simplier
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java) //init the viewmodel ewntonces ya se intentna leer de la base de datos
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java) //este tien que ver con las queries y con los chip no con sacar y meter datos en la base de datos.
        //aqui tu inicias el recipesviewmodel y ya se lee datastorepreferences
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecipesBinding.inflate(inflater, container, false) //because we need to acces the variable from other function we will make it global
        /**When we bind our data with the xml layput we have to set the lifecyclerOwner.
         * because in our fragment recipes layput we are going to use life data object so this needs a lifecicler owner. */
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel //we bind the viewmodel variable from the layout with the instance of viewmodel.
        //activate the shimmer in our own reculcerView
        //mView.recyclerview.showShimmer()
        //return the view Inflated

        //inicialize the viewmodel
        /**
         * habilitart option menu en el framgnet uy luego override el metodo para crear el menu(inflarlo)
         */
        setHasOptionsMenu(true)

        setupRecyclerView()

        /**
         * Network
         * using this observer we want to get the latest value from the datasortture and sotre that to
         * our backOnline variable iside recipes viewmodel
         *
         * SO BASICALLY WE ARE PERSISTING THIS BACKONLINE VALUE USING DATASTORE. AND EVERY TIEME WE RUN THE APP
         *WE ARE OBSERVING THE THAT READBACK ONLINE VALUE AND WE STORE ITS VALUE TO OUR BACKONLINE VARIABLE FROM RECIPESVIEWMODEL. AND FROM THERE
         * ITS VALUE WILL CHANGE
         */
        /**Resulta que tu estas cogiendo aqui los datos de un lice data
         * readBackOnline que lo conviertes cuando te vienen lso datos de un flow.Y lo conviertes
         * con un aslivedata. */
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
            networkListener = NetworkListener()
            //call the funciton inside. If we foccus on the funciton we wiulll se that we have a returnning MutableStteFlo
            //which is a boolena. So this bassically will reteurn a aboolena.
            //so we need to collect that boolean here using the collect function. like we this in a normal flow
            networkListener.checkNetworkAvailability(requireContext()).collect { status ->
                //log the status network
                Log.d("NetworkListener", status.toString())
                //cal the recipesviewModel and asign it networkStatus variable the to value of
                //checknetworkavailability funciton
                //usasa la clase recipes viewmodel pq es la qeu tiene que manejar todo eso
                recipesViewModel.networkStatus = status
                recipesViewModel.showNetworkStatus()
                /**Make a function to read from the database. Allways requets from the database but there are cases when you read from
                 * the api.for exapmple when the database is empty or when we need to perform an other type of request, i meann
                 * a new request with different parameters. That will be donde by pressing the floating button. */
                //whenever the internet cahnges we are going to read the database.
                readDatabase() //se llama cada vez que volvemos del dialog o de otro framgnet

            }
        }


        binding.recipesFab.setOnClickListener {
            /**Only if recipesViewModel.networkStatus is true  then we want to navigate to the bottom sheet
             * if there is no internet conection we cant.
             * si intentamos hacer lcick en el fab y no tenemos internet nonos va a dejar y nos dria que no hay conexion.
             * */
            if(recipesViewModel.networkStatus){
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet) // con el controlador navegamos y le ponemos la accion que hemos indicado con la flecha en el my_nav.
            }else{
                recipesViewModel.showNetworkStatus()
            }

        }


        /**This should be called whenever our database is empty so by noe we will not use that*/
        //requestApiData()


        return binding.root



    }

    private fun setupRecyclerView(){
        binding.recyclerview.adapter = mAdapter //el adapter se inicia
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffec()
    }

    /**
     * Create the search barr.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //inflate the menu and pass the resource file and pass the menu parameter
        inflater.inflate(R.menu.recipes_menu, menu)
        //use the menu to find the item int he menu we defined earlier.
        val search = menu.findItem(R.id.menu_search)
        //indicamos que search es una view de tipo search
        val searchView = search.actionView as? SearchView
        //habilitar que el propio icono actue como submit par ahacer la busqueda
        searchView?.isSubmitButtonEnabled = true
        //ahora le asociamos un listner que hay dos oveeride que podemos usar, uno para cuadno clicas una vez itnrpducida la palabara y el otro para cuadno vas metiedno palabras y te sale a teimpo real.
        searchView?.setOnQueryTextListener(this) //we have to extends this framgne with SarchView.onWQUERYLEISGTENER
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null){
            searchApiData(query)
        }

        return true

    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun readDatabase() {

        //coroutine is atatched to the life of the fragment.
        lifecycleScope.launch {
            /**here we wil call the maniviewmodel and observe the readRecipes livedata*/
            /**
             * //resulta que cuando desinstalamos la app y la iniciamos vamos a tener primero requestApiData called pq vamos a ir a buscar los datos a al
            // abase de datos. Pero tambien resultaq eu ene le viewmodel estamos insertando los datos de
            // la request en la base de datos local y este observer se da cuenta de que hay datos en la local y se activa y por eso tienes ese mensaje
             Entonces este observer se esta activando dos veces y no deberia de hacerlo pq o leemos de la api o
             leemos de la base de datos local, pero on de las dos, aunue relamet no estas leyendo solo que se vuelve a activar el observer pq ve que hay cambios en la abse de datos.
             So to avoid that we will create an extension function. in util package
             */
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner, Observer { database ->
                /**si la base de datos no esta vacia y no venimos del dialogo es decir el args es false
                 * entonces es cuando mostramos los datos de la base de datos. y no de la api.
                 * pero en cambio si tu vines de haber sleecionado algo en el bottomsheet entonces
                 * vas a ahacer una nueva request al api con nuevos valores*/
                if (database.isNotEmpty() && !args.backFromBottomSheet){ //means that the database is not empty and have some data so we want to display in the reculcerview.
                    Log.d("RecipesFragment", "readDatabase called!")
                    mAdapter.setData(database[0].foodRecipe) //allways we get only one row. so we can acces foodrecipe
                    hideShimmerEffect()
                }else { //if the database is empty we will requet the api data.
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
    private fun requestApiData(){
        Log.d("RecipesFragment", "requestApiData called!")
        mainViewModel.getRecipes(recipesViewModel.applyQueries()) //--> use the RecipeViewModel eich contains the endpoint queries. We use thar for not havving the applyquery function here and make it clenear.
        mainViewModel.recipesResponse.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is NetworkResult.Succes ->{
                    //hide shimmer response becase we getdata
                    hideShimmerEffect()
                    //get acces to the food recipe
                    //accedes a alos datos de NetworkResult que es una clase foodRecipe qeu tiene una lista de result y le pasas esa lsita al adapter.
                    response.data?.let {
                        mAdapter.setData(it) }
                }
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
     * 15-Search menu--->
     *this funciton will be called from the onQueryTextsUBMIT so that when we press
     * the button search this funciton will run and search the data.
     *
     */

    private fun searchApiData(searchQuery: String){
        showShimmerEffec()
        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery)) //here we pass the queries and that will be created in recipesviewmodel
        //observe the response
        mainViewModel.searchRecipesResponse.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is NetworkResult.Succes ->{
                    //hide shimmer response becase we getdata
                    hideShimmerEffect()
                    //otra forma de hacerlo con mas pasos
                    val foodRecipe = response.data
                    foodRecipe?.let {
                        mAdapter.setData(it) }
                }
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
     * Imaginate que tiens datos en la base de datos pero ahora tu solicitas unos nuevos, pero resutla que hay un error en la comunicacion con la api
     * pues en ese caso vamos a enseñarle al usuario los datos antiguos alamacenados en la base datos.
     * De este modo el usuario no vera un recuclerview vacio y siempre tendra datos.
     */

    private fun loadDataFromCache(){
        /**Como readRecipes no es una funcion suspend y se lee los datos en la base de datos en la ui thread,
         * tenemos que hacer que se haga en un backgroud thread pr tanto usaremos corrutinas*/
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
        /**with this we avoid the ememory leaks
         * so when the recipe fragment is destroyed this binding will be set to null. */
        _binding = null
    }

}