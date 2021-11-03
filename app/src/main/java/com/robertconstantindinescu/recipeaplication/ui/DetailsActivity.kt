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

    private val args by navArgs<DetailsActivityArgs>() //esta clase la crea el sistema cuando tu haces las actions. Relemnte contieen on objeto Result con todoos los datos

    /**
     * 20--Favoirtes
     * initialize the viewmodel to insert the data
     */
    private val mainViewModel: MainViewModel by viewModels() //because we are using this viewmodel which has injected a repository inside. From here whichh is the entrypoint we need to specify that

    private var recipeSaved = false
    private var savedRecipeId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        /**Make that activity suport the action bar*/
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //to go back with the arrow

        /**
         * 16---> DetailsActivity
         * Create an array of fragments
         * and add to that array each of the fragments we created to that arraylist
         * in a specific order.
         */
        val fragments = ArrayList<Fragment>()
        fragments.add(OverViewFragment())
        fragments.add(IngredientsFragment())
        fragments.add(InstructionsFragment())

        /**
         * Create arrya list of strings and add the 3 titles for the 3 framgnts
         */

        val titles = ArrayList<String>()
        titles.add("Overview")
        titles.add("Ingredients")
        titles.add("Instructions")

        /**Before we initialize the page adapter, we need to create a bundle object
         * but in order to create a bundel object, we need to get the values or the data from our safe args
         * which we have passed from our recipes fragment to our detailed activity
         * so on the top add a args variable. */
        val resultBundle = Bundle()
        /**Because our result variable from our args is type of result which is actually a parcelable we use that funciton to
         * pass that variable
         * Resulta que cundo hemos hehco la acction para pasar info de recipeFragment a detailactivity pues
         * tenemo que pasamos un argumento de tipo Result que es parecelable, lo hemos ocnvertido nosotors para
         * que se pueda pasar como argumento. entonces args es decir esos argumetnos vana a ser de tupo de la clase
         * Result. Entonces lo que pasamos por bundle es args.result (result esta definido en el my_nav
         * en la activity concretamtne en us argumet. )*/
        resultBundle.putParcelable(RECIPE_RESULT_KEY, args.result) //here we bind the recipe data to the bundle and pass pass the data to the pager adatpater and this adapter fed the data to our framents.
        /**Now initialize pageAdapter
         * the parameters are bundle, list of fragments, title, managerframgnts*/

        val adapter = com.robertconstantindinescu.recipeaplication.adapters.PagerAdapter(
            /**This is actually the args from safeargs. */
            resultBundle,
            fragments,
            titles,
            supportFragmentManager
        )

        /**Call the viewpager from activity detials layouit and we can set the adapter*/
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

    }

    /**
     * 20--Favorites
     * create the menu with the start menu we created
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //inflate the menu we just created
        menuInflater.inflate(R.menu.details_menu, menu)
        val menuItem = menu?.findItem(R.id.save_to_favorites_menu) //find the star menu
        //create new function to check the start and pass the actual star --> !! because never will be null and we know that
        chackSavedRecipes(menuItem!!)

        return true
    }

    /**
     * to chek if the  recipe that we are passing as argument from fragment recypè(recylcer)
     * exists in the database firts we have to read the database and observe that
     */
    private fun chackSavedRecipes(menuItem: MenuItem) {
        mainViewModel.readFavoriteRecipes.observe(this, Observer { favoritesEntity ->
            try {
                //create a for loop and go thoru eahc saved recipe from the favorite entitty
                    //and compare the id from each result. the id is in the Result class
                    //compare it with the object that we obtine through args
                for (savedRecipe in favoritesEntity){
                    if (savedRecipe.result.recipeId == args.result.recipeId){
                        //if there are the same the recipe is already saved and change the color start to yellor using the fucntion
                        changeMenuItemColor(menuItem, R.color.yellow)
                        //save the recipe id to the global variable for haveing the id prepared.
                        savedRecipeId = savedRecipe.id //this id es el de la tabla misma no de la propia receta. y ese id de la tabala lo usaremos para eliminar la fila de esa receta. mas a abajo
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
     * 20--favorites
     */
    private fun saveToFavorites(item: MenuItem) {
        //create new variable favoritesEntity to create the new object to be inserted
        val favoritesEntity = FavoritesEntity(
            0,
            args.result //The resutl class(object that is needed can be obtenied from args. when we navigated from recipes fragment to detauils asctiivty.
        )

        //here init the viewmodel by calling it and then insert
        mainViewModel.insertFavoriteRecipe(favoritesEntity)

        /**Change the color of the start selected item from menu
         * as a parameter we chose the item menu which is a start and the color*/
        changeMenuItemColor(item, R.color.yellow)
        //call a snack bar with a text as argument
        showSnackBar("Recipe saved.")
        recipeSaved = true // whenever we save the recipe we change the value of recipeSaved to true
    }

    /**
     * this method is called from onOptionsItemSelected
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

    private fun showSnackBar(message: String) {
        //the firts parameter is the actual view which is detailslayput because we named it. This is the constraint layout from activity_details_layout
        Snackbar.make(
            detailsLayout,
            message,
            Snackbar.LENGTH_LONG

        ).setAction("Okay"){}.show()
    }

    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        //contextcompat help us to acces some features like getcolor.
        item.icon.setTint(ContextCompat.getColor(this, color))
    }
}