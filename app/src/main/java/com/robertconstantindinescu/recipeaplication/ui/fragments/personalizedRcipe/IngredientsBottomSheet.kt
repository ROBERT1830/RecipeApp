package com.robertconstantindinescu.recipeaplication.ui.fragments.personalizedRcipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.robertconstantindinescu.recipeaplication.R
import com.robertconstantindinescu.recipeaplication.util.Constants
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.DEFAULT_FISH_TYPE
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.DEFAULT_MEAT_TYPE
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.DEFAULT_VEGETABLE_TYPE
import com.robertconstantindinescu.recipeaplication.viewmodels.RecipesViewModel
import kotlinx.android.synthetic.main.fragment_ingredients_bottom_sheet.view.*
import kotlinx.android.synthetic.main.fragment_personalized_recipe.view.*
import java.lang.Exception
import java.util.*

class IngredientsBottomSheet : BottomSheetDialogFragment() {


    private  lateinit var recipesViewModel: RecipesViewModel
    /**Variables para almacenar los valores de los chips*/
    private var meatTypeChip = DEFAULT_MEAT_TYPE
    private var meatTypeChipId = 0
    private var vegyTypeChip = DEFAULT_VEGETABLE_TYPE
    private var vegyTypeChipId = 0
    private var fishTypeChip = DEFAULT_FISH_TYPE
    private var fishTypeChipId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mView = inflater.inflate(R.layout.fragment_ingredients_bottom_sheet, container, false)


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

            updateChip(value.selectedMeatTypeId, mView.meatType_chipGroup)
            updateChip(value.selectedFishTypeId, mView.fishType_chipGroup)
            updateChip(value.selectedVegyTypeId, mView.vegyType_chipGroup)


        })


        mView.meatType_chipGroup.setOnCheckedChangeListener { chipGroup, selectedChipId ->
            /**Nos guardamos el texto e id del chip seleccionado del grupo correspondiente*/
            val chip = chipGroup.findViewById<Chip>(selectedChipId)
            val selectedChipTxt = chip.text.toString().lowercase(Locale.ROOT)

            /**Apuntamos a esos valores ocn las variables globales*/
            meatTypeChip = selectedChipTxt
            meatTypeChipId = selectedChipId



        }

        mView.fishType_chipGroup.setOnCheckedChangeListener { chipGroup, selectedChipId ->
            /**Nos guardamos el texto e id del chip seleccionado del grupo correspondiente*/
            val chip = chipGroup.findViewById<Chip>(selectedChipId)
            val selectedChipTxt = chip.text.toString().lowercase(Locale.ROOT)

            /**Apuntamos a esos valores ocn las variables globales*/
            fishTypeChip = selectedChipTxt
            fishTypeChipId = selectedChipId
        }
        mView.vegyType_chipGroup.setOnCheckedChangeListener { chipGroup, selectedChipId ->
            /**Nos guardamos el texto e id del chip seleccionado del grupo correspondiente*/
            val chip = chipGroup.findViewById<Chip>(selectedChipId)
            val selectedChipTxt = chip.text.toString().lowercase(Locale.ROOT)

            /**Apuntamos a esos valores ocn las variables globales*/
            vegyTypeChip = selectedChipTxt
            vegyTypeChipId = selectedChipId
        }
        /**Cuando pulsamos el botón fab, se guarda en dataStore los valores seleccionados de todos
         * los chips.*/
        mView.apply_btn.setOnClickListener {
            recipesViewModel.saveMeatFishVegyType(meatTypeChip, meatTypeChipId, vegyTypeChip,
            vegyTypeChipId, fishTypeChip, fishTypeChipId)

            val action = IngredientsBottomSheetDirections.actionIngredientsBottomSheetToFoodJokeFragment(true)
            findNavController().navigate(action)

        }

        return mView
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


}