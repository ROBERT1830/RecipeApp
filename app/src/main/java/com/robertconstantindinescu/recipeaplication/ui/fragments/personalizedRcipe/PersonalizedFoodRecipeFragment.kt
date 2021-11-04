package com.robertconstantindinescu.recipeaplication.ui.fragments.personalizedRcipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.robertconstantindinescu.recipeaplication.R
import com.robertconstantindinescu.recipeaplication.adapters.PersonalizedFoodRecipeAdapter
import com.robertconstantindinescu.recipeaplication.databinding.FragmentPersonalizedRecipeBinding
import com.robertconstantindinescu.recipeaplication.ui.fragments.recipes.RecipesFragmentArgs

import com.robertconstantindinescu.recipeaplication.util.NetworkListener
import com.robertconstantindinescu.recipeaplication.util.NetworkResult
import com.robertconstantindinescu.recipeaplication.util.observeOnce
import com.robertconstantindinescu.recipeaplication.viewmodels.MainViewModel
import com.robertconstantindinescu.recipeaplication.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonalizedFoodRecipeFragment : Fragment() {


    private val args by navArgs<PersonalizedFoodRecipeFragmentArgs>()

    private var _binding: FragmentPersonalizedRecipeBinding? = null
    private val binding get() = _binding!!





    /**Instancia del viewModel para hacer la query*/
    private lateinit var mainViewModel: MainViewModel

    /**Instancia de recipeViewModel para obtener la query*/
    private lateinit var recipesViewModel: RecipesViewModel

    /**inicialización lazy del adapter*/
    // TODO: 2/11/21 After get api data build the adapter dor this class
    private val mAdapter by lazy { PersonalizedFoodRecipeAdapter() }
    //private val mAdapterIngr by lazy { PersonalizedFoodRecipeIngredientsAdapter(emptyList()) }

    /**Network para informar del estado de la red*/
    private lateinit var networklistener: NetworkListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        /**Inicializamos los viewModels*/
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPersonalizedRecipeBinding.inflate(inflater, container, false)
        /**Asociar la variable mainViewmodel del layout a mainViewModel*/
        binding.lifecycleOwner = this
        binding.mainviewModel = mainViewModel

        setupRecyclerView()


        recipesViewModel.readBackOnline.observe(viewLifecycleOwner, Observer {
            recipesViewModel.backOnline = it
        })


        lifecycleScope.launch {
            networklistener = NetworkListener()
            networklistener.checkNetworkAvailability(requireContext()).collect { status ->
                recipesViewModel.networkStatus = status
                recipesViewModel.showNetworkStatus()
                readDataBase()

            }
        }

        binding.fabPersonalizeRecipe.setOnClickListener {
            if(recipesViewModel.networkStatus){
                findNavController().navigate(R.id.action_foodJokeFragment_to_ingredientsBottomSheet) // con el controlador navegamos y le ponemos la accion que hemos indicado con la flecha en el my_nav.
            }else{
                recipesViewModel.showNetworkStatus()
            }
        }
        return binding.root
    }



    private fun setupRecyclerView() {
        binding.recyclerviewPersonalizedRecipe.adapter = mAdapter
        binding.recyclerviewPersonalizedRecipe.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

    }

    private fun readDataBase() {
        lifecycleScope.launch {
            mainViewModel.readPersonalizedRecipes.observeOnce(
                viewLifecycleOwner,
                Observer { database ->
                    if (database.isNotEmpty() && !args.backFromIngredientsBottomSheet) {
                        mAdapter.setData(database[0].personalizedRecipe)
                    } else {
                        requestApiData()
                    }

                })
        }
    }

    private fun requestApiData() {
        mainViewModel.getPersonalizedRecipe(recipesViewModel.applyPersonalizedRecipeQuery())
        mainViewModel.personalizedRecipeResponse.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is NetworkResult.Succes -> {
                    response.data?.let {
                        mAdapter.setData(it)

                    }
                }
                is NetworkResult.Error -> {
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                }
            }

        })

    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readPersonalizedRecipes.observe(viewLifecycleOwner, Observer { database ->
                if (database.isNotEmpty()) {
                    mAdapter.setData(database[0].personalizedRecipe)
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}