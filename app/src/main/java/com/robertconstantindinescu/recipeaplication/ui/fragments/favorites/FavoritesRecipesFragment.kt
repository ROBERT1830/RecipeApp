package com.robertconstantindinescu.recipeaplication.ui.fragments.favorites

import android.os.Bundle
import android.os.Message
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoritesRecipesFragment : Fragment() {

    private val mainViewModel: MainViewModel by viewModels()
    private val mAdapter: FavoriteRecipesAdapter by lazy { FavoriteRecipesAdapter(requireActivity(), mainViewModel) } //because we need the mainViewmodel inside the favrecipeadapter we need to initialize the mainviewmodel firts so cut and puit it above

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
        /**
         * Usamos dataBindig, con lo que usando binding vamos a poder acceder a cada una de las
         * variables indicadas en el layout de favoriteRecipesFragment y setear tanto el
         * viewmodel y el adapter con el viewmodel y adapter definidos.
         */
        _binding = FragmentFavoritesRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        binding.mAdapter = mAdapter


        //para ver el menu en la barra de navegacion
        setHasOptionsMenu(true)
        setUpRecyclerView(binding.favoriteRecipesRecyclerView)
        return binding.root
    }

    //setemos el recycler.
    private fun setUpRecyclerView(recyclerView: RecyclerView){
        recyclerView.adapter = mAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }


    override fun onDestroy() {
        super.onDestroy()
        //con esto eliminamos la referencia a todos esos elementos y ya no tenemos que tenerlos en memoria
        _binding = null
        //cerramos el modo contextual.
        mAdapter.clearContextualAcitonMode()
    }


    /**
     * M??todo para crear el men?? del fragment.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_recipe_menu, menu)
    }

    /**
     * m??todo para determinar la selecci??n del icono y ejecutar su acci??n de eliminar.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
             R.id.delete_all_favorite_recipes_menu -> {
                 mainViewModel.deleteAllFavoriteRecipes()
                 showSnackbar()
             }
            R.id.order_alfabetically -> {
                lifecycleScope.launch(Dispatchers.Main) {
                    mainViewModel.sortByName().collect {
                        mAdapter.setData(it)

                    }
                }
            }
            R.id.order_by_timeDuration -> {
                lifecycleScope.launch(Dispatchers.Main) {
                    mainViewModel.sortByTimeDuration().collect {
                        mAdapter.setData(it)

                    }
                }
            }
            R.id.order_by_likes -> {
                lifecycleScope.launch(Dispatchers.Main) {
                    mainViewModel.orderByLikes().collect {
                        mAdapter.setData(it)

                    }
                }
            }
        }


        if (item.itemId == R.id.delete_all_favorite_recipes_menu){
            mainViewModel.deleteAllFavoriteRecipes()
            showSnackbar()
        }
        return super.onOptionsItemSelected(item)
    }

    //m??todo para mostrar el n??mero de recetas elminadas.
    private fun showSnackbar(){
       Snackbar.make(
           binding.root,
           "All recipes removed",
           Snackbar.LENGTH_SHORT
       ).setAction("Ok"){}.show()
    }
}