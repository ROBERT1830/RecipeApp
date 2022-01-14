package com.robertconstantindinescu.recipeaplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.navArgs
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.recipeaplication.R
import com.robertconstantindinescu.recipeaplication.data.database.entities.FavoritesEntity
import com.robertconstantindinescu.recipeaplication.ui.fragments.ingredients.IngredientsFragment
import com.robertconstantindinescu.recipeaplication.ui.fragments.instructions.InstructionsFragment
import com.robertconstantindinescu.recipeaplication.ui.fragments.overview.OverViewFragment
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.RECIPE_RESULT_KEY
import com.robertconstantindinescu.recipeaplication.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_details.*

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private val args by navArgs<DetailsActivityArgs>()
    private val mainViewModel: MainViewModel by viewModels()
    private var recipeSaved = false
    private var savedRecipeId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        /*Hacemos que la activity suporte la actionbar.*/
        setSupportActionBar(toolbar)
        //establecemos el color
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        //habilitamos la flecha de ir hácia atrás.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        /**
         * Vamos a utilizar ViewPager por tanto nos creamos un array de los fragmentos que
         * lo van a componer. Los añadimos en un orden específico.
         */
        val fragments = ArrayList<Fragment>()
        fragments.add(OverViewFragment())
        fragments.add(IngredientsFragment())
        fragments.add(InstructionsFragment())

        /**
         * Creamos un array de strings, que harán referencia a los títulos de cada uno de los fragmentos
         */

        val titles = ArrayList<String>()
        titles.add("Overview")
        titles.add("Ingredients")
        titles.add("Instructions")

        /**Before we initialize the page adapter, we need to create a bundle object
         * but in order to create a bundel object, we need to get the values or the data from our safe args
         * which we have passed from our recipes fragment to our detailed activity
         * so on the top add a args variable. */

        /**
         * Antes de inicializar el pager adapter, creamos un objeto bundle y utilizaremos los argumetnos
         * que nos vienen del fragmento RecipesFragment para cargar el bundle.
         */
        val resultBundle = Bundle()

        /**
         * Relmente lo que estamos pasando como argumentos, es un objeto de tipo Result.
         * Cuando hemos generado la acción para pasar  de RecipeFragment a DetailsActivity
         * hemos puesto que queremos que esta ultima obtenga un argumento de tipo Result. Para ello,
         * hemos tenido que hacer la clase Result parcelable para que pueda pasarse como argumento.
         * Por tanto la variable args, será de tipo Result.
         */
        resultBundle.putParcelable(RECIPE_RESULT_KEY, args.result) //here we bind the recipe data to the bundle and pass pass the data to the pager adatpater and this adapter fed the data to our framents.
        /**
         * Inicializamos el pager adapter, con el bundle, la lista de fragmentos, títulos
         * y el supportmanager.
         */
        val adapter = com.robertconstantindinescu.recipeaplication.adapters.PagerAdapter(
            resultBundle,
            fragments,
            titles,
            supportFragmentManager
        )
        /**
         * seteamos el adapter coordinamos el tablayout para mostrar los títulos de cada fragmento.
         */
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

    }

    /**
     * Función que genera el menú de la details activity.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //inflamos el menú creado
        menuInflater.inflate(R.menu.details_menu, menu)
        //buscamos el icono del menu, que realmente es la estrella.
        val menuItem = menu?.findItem(R.id.save_to_favorites_menu)
        //función para comprobar si una determinada receta existe o no en la base de datos
        //puede o no existir.
        chackSavedRecipes(menuItem!!)

        return true
    }

    /**
     * to chek if the  recipe that we are passing as argument from fragment recypè(recylcer)
     * exists in the database firts we have to read the database and observe that
     */
    /**
     * Función que chequea si una determinada receta existe o no en la base de datos.
     * Para ello, con un for vamos a recorrenos cada una de las recetas guardadas en favoritos
     * y la comparamos el id que tenemos en la base de datos con el id de la receta que nos
     * viene por bundle. Si resulta que son iguales, se cambia el color de la estrella a amarillo
     * ,nos guardamos el id e indicamos que se ha guardado la receta.
     * Si no está guardad la receta, cambiamos el color de la estrella a blanco.
     */
    private fun chackSavedRecipes(menuItem: MenuItem) {
        mainViewModel.readFavoriteRecipes.observe(this, Observer { favoritesEntity ->
            try {

                for (savedRecipe in favoritesEntity){
                    if (savedRecipe.result.recipeId == args.result.recipeId){
                        changeMenuItemColor(menuItem, R.color.yellow)
                        //Este id es el de la base de datos (no receta de args). Este lo usaremos
                        //para eliminar la fila de esa receta más abajo.
                        savedRecipeId = savedRecipe.id
                        recipeSaved = true //that means that  if we have the recipe passed from args saved in the databse becasue its id are the same that means that is saved so true.
                    }else{ //when are not equals change star to white
                        changeMenuItemColor(menuItem, R.color.white)
                    }
                }
            }catch (e: Exception){
                Log.d("DetailsActivity", e.message.toString())
            }
        })

    }

    /**
     * THIS FUNCTIONS ALLOW PERFORMING AN ACTION TO A SELECTED ITEM FROM MENU
     */
    /**
     * Esta función nos permite realizar una determinada acción cuadno seleccionamos
     * una receta. Si el item selecionado es la flecha de volver hácia atrás
     * se acaba la activity.
     *
     * Poer el contrario si presionamos la estrella y no está guardad la receta, la guardaremos
     * y cambiaremos el color de la estrella.
     * Si ya está insertada de forma previa y presionamos la estrella eliminaremos la receta
     * de favoritos.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish() //close details activity
            /**20---Favorites*/
        }else if(item.itemId == R.id.save_to_favorites_menu && !recipeSaved){
            //when press the star, we will call an other function. and pass the item clicked or selected
            saveToFavorites(item)
        }else if(item.itemId == R.id.save_to_favorites_menu && recipeSaved){
        //si se presiona la estrella y la variabel bool esta a true entonves
            removeFromFavorites(item)
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Esta función, en primer lugar se crea un objeto de tipo FvoritesEntity
     * para almacenar en la base de datos la receta selecionada como varotios.
     * Se utiliza el objeto pasado por bundle.
     * Se llama a viewmodel para que gestione la inserción de la receta en la base de datos.
     * Una vez guardada, se cambia el color de la estrella a amarillo y ponemos true recipeSaved
     *
     */
    private fun saveToFavorites(item: MenuItem) {
        val favoritesEntity = FavoritesEntity(
            0,
            args.result
        )
        mainViewModel.insertFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.yellow)
        //call a snack bar with a text as argument
        showSnackBar("Recipe saved.")
        recipeSaved = true
    }

    /**
     * Función que elimina una receta seleccionada de la base de datos de favoritos.
     * Nos  creamos el objeto, a eliminar con el id que nos habíamos preparado antes.
     * Llamamos a deleteFavoriteRecipe para eliminar la receta y ponemos la variable
     * recipeSaved a false.
     */
    private fun removeFromFavorites(item: MenuItem){
        //create the objet of the recipe to be removed
        val favoritesEntity = FavoritesEntity(
            savedRecipeId,
            args.result
        )
        mainViewModel.deleteFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.white)
        showSnackBar("Removed from Favorites.")
        recipeSaved = false

    }

    /**
     * Función que muestra un snackbar con un mensaje.
     */
    private fun showSnackBar(message: String) {
        Snackbar.make(
            detailsLayout,
            message,
            Snackbar.LENGTH_LONG

        ).setAction("Okay"){}.show()
    }

    /**
     * Método para cambiar el color del icono estrella.
     */
    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        //contextcompat help us to acces some features like getcolor.
        item.icon.setTint(ContextCompat.getColor(this, color))
    }
}