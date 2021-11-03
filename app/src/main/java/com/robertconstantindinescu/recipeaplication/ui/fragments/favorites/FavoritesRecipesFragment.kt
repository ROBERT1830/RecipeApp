package com.robertconstantindinescu.recipeaplication.ui.fragments.favorites

import android.os.Bundle
import android.os.Message
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.recipeaplication.R
import com.robertconstantindinescu.recipeaplication.adapters.FavoriteRecipesAdapter
import com.robertconstantindinescu.recipeaplication.data.database.entities.FavoritesEntity
import com.robertconstantindinescu.recipeaplication.databinding.FragmentFavoritesRecipesBinding
import com.robertconstantindinescu.recipeaplication.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_favorites_recipes.view.*

@AndroidEntryPoint
class FavoritesRecipesFragment : Fragment() {

    /**
     * 22--change style of row recipe variables
     */


    /**
     * 21--Favorite recipesfragment
     */
    private val mainViewModel: MainViewModel by viewModels()
    private val mAdapter: FavoriteRecipesAdapter by lazy { FavoriteRecipesAdapter(requireActivity(), mainViewModel) } //because we need the mainViewmodel inside the favrecipeadapter we need to initialize the mainviewmodel firts so cut and puit it above
    //private val mainViewModel: MainViewModel by viewModels()

    /**
     * 21--FAVORITES RECIPES
     */
    private var _binding:FragmentFavoritesRecipesBinding? = null
    private val binding get() = _binding!!  //is a read only variable



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //val view = inflater.inflate(R.layout.fragment_favorites_recipes, container, false)
        //inflate the layout or the clas in order to get those elements
        _binding = FragmentFavoritesRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        binding.mAdapter = mAdapter


        //to show the menu in the action bar
        setHasOptionsMenu(true)
        setUpRecyclerView(binding.favoriteRecipesRecyclerView)

        //cal mainviewmodel and read recipes from database
        /**
         * 21 no longer needed that observe because we used binding. and we are observing from the binding adapter
         */
//        mainViewModel.readFavoriteRecipes.observe(viewLifecycleOwner, Observer { favoriteEntity ->
//            mAdapter.setData(favoriteEntity)
//        })


        return binding.root
    }

    private fun setUpRecyclerView(recyclerView: RecyclerView){
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null //con esto eliminamos la referencia a todos esos elementos y ya no tenemos que tenerlos en memoria
        mAdapter.clearContextualAcitonMode() //close the contextual action mode
    }

    /**
     * 22
     * create menu of the fragment
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_recipe_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_all_favorite_recipes_menu){
            mainViewModel.deleteAllFavoriteRecipes()
            showSnackbar()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showSnackbar(){
       Snackbar.make(
           binding.root,
           "All recipes removed",
           Snackbar.LENGTH_SHORT
       ).setAction("Ok"){}.show()
    }
}