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
import com.robertconstantindinescu.recipeaplication.util.Constants

import com.robertconstantindinescu.recipeaplication.util.NetworkListener
import com.robertconstantindinescu.recipeaplication.util.NetworkResult
import com.robertconstantindinescu.recipeaplication.util.observeOnce
import com.robertconstantindinescu.recipeaplication.viewmodels.MainViewModel
import com.robertconstantindinescu.recipeaplication.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_ingredients_bottom_sheet.view.*

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

@AndroidEntryPoint
class PersonalizedFoodRecipeFragment : Fragment() {


    /**Variables para almacenar los valores de los chips*/
    private var meatTypeChip = Constants.DEFAULT_MEAT_TYPE
    private var meatTypeChipId = 0
    private var vegyTypeChip = Constants.DEFAULT_VEGETABLE_TYPE
    private var vegyTypeChipId = 0
    private var fishTypeChip = Constants.DEFAULT_FISH_TYPE
    private var fishTypeChipId = 0

    private var fromFab = false

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
                requestApiData()

            }
        }

        /**Leer el dataStore y extraemos los datos de los diferentes tipos de ingredientes. Realmente
         * obtendremos un objeto que hemos creado en el datastore. Lo obtenemos a modoo de flow,
         * pero aqui lo convertimos ne livedata para ser observado*/
        recipesViewModel.readMeatVegyFishOtherType.asLiveData().observe(viewLifecycleOwner, androidx.lifecycle.Observer { value ->
            /**Nos guardamos el texto de cada chip*/
            meatTypeChip = value.selectedMeatType
            fishTypeChip = value.selectedFishType
            vegyTypeChip = value.selectedVegyType

            /**Actualizamos los chip con los valores que ha seleccionado el usuario y los persistimos
             * en dataStore. Pasamos el id y el chip group del chip seleccioando*/

            updateChip(value.selectedMeatTypeId, binding.meatTypeChipGroup)
            updateChip(value.selectedFishTypeId, binding.fishTypeChipGroup)
            updateChip(value.selectedVegyTypeId, binding.vegyTypeChipGroup)


        })


        binding.meatTypeChipGroup.setOnCheckedChangeListener { chipGroup, selectedChipId ->
            /**Nos guardamos el texto e id del chip seleccionado del grupo correspondiente*/
            val chip = chipGroup.findViewById<Chip>(selectedChipId)
            val selectedChipTxt = chip.text.toString().lowercase(Locale.ROOT)

            /**Apuntamos a esos valores ocn las variables globales*/
            meatTypeChip = selectedChipTxt
            meatTypeChipId = selectedChipId



        }

        binding.fishTypeChipGroup.setOnCheckedChangeListener { chipGroup, selectedChipId ->
            /**Nos guardamos el texto e id del chip seleccionado del grupo correspondiente*/
            val chip = chipGroup.findViewById<Chip>(selectedChipId)
            val selectedChipTxt = chip.text.toString().lowercase(Locale.ROOT)

            /**Apuntamos a esos valores ocn las variables globales*/
            fishTypeChip = selectedChipTxt
            fishTypeChipId = selectedChipId
        }
        binding.vegyTypeChipGroup.setOnCheckedChangeListener { chipGroup, selectedChipId ->
            /**Nos guardamos el texto e id del chip seleccionado del grupo correspondiente*/
            val chip = chipGroup.findViewById<Chip>(selectedChipId)
            val selectedChipTxt = chip.text.toString().lowercase(Locale.ROOT)

            /**Apuntamos a esos valores ocn las variables globales*/
            vegyTypeChip = selectedChipTxt
            vegyTypeChipId = selectedChipId
        }
        /**Cuando pulsamos el botón fab, se guarda en dataStore los valores seleccionados de todos
         * los chips.*/
        binding.fabPersonalizeRecipe.setOnClickListener {
            recipesViewModel.saveMeatFishVegyType(meatTypeChip, meatTypeChipId, vegyTypeChip,
                vegyTypeChipId, fishTypeChip, fishTypeChipId)
            requestApiData()
            fromFab = false

        }






        return binding.root
    }

    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {
        //our chip id will not be 0 if we hav emade a differnte selecion from the first one nd saved the data in the datastore
        if (chipId != 0){
            try {
                chipGroup.findViewById<Chip>(chipId).isChecked = true

            }catch (e: Exception){
                Log.d("RecipesBottomSheet", e.message.toString())
            }
        }
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
                    if (database.isNotEmpty()) {
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