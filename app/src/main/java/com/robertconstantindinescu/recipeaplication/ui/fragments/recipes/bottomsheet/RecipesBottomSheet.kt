package com.robertconstantindinescu.recipeaplication.ui.fragments.recipes.bottomsheet

import android.os.Bundle
import android.util.Log
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
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.DEFAULT_DIET_TYPE
import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.DEFAULT_MEAL_TYPE
import com.robertconstantindinescu.recipeaplication.viewmodels.RecipesViewModel
import kotlinx.android.synthetic.main.recipes_bottom_sehet.view.*
import java.lang.Exception
import java.util.*


/**
 * this fragment will not hereda from Fragment because it will not be a nirmal fragment. Instead
 * we will use BottomSheetDialogFrafgment
 */
class RecipesBottomSheet : BottomSheetDialogFragment() /*Fragment()*/ {

    private  lateinit var recipesViewModel: RecipesViewModel

    /**Create the different variables for the chips that are needed to pass as a parameter for the saveMealAndDietType
     * function in RecipesViewModel. */
    private var mealTypeChip = DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0 //this id represents the actuall id for one of those chip of our chip group. the first one
    private var dietTypeChip = DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0

    /**Now we have to implement onCheked listener and whenever we check one chipp we want to store its value
     * and its Id. And when press the applu button we wwant to save taht selected chip id and name to the datastore*/


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
        val mView = inflater.inflate(R.layout.recipes_bottom_sehet, container, false) //esto lo hemos hecho asi, es decir nos hemos defindo una val view para poder acceder a esa vista inflada pq si lo dejamos con el return pues no podemos acceder a los diferentes elemntos de la vista. si que podriamso implementar el viewBinding

        /**Tambien tenmos que implementar el hehco de que cuadno abramos el dialog del chip
         * pues que podamos leer los datos de la store preferences y mostrarlos en el dialog
         * con la seleccion que habia hecho el usuaio antes.
         * para ello vamos a coger ese Flow<MealAndDietType> almacenado en readMealAndDietType y que es como un live data
         * y lo vamos a convertir a livedata para que pueda ser observado su valor o el cambio de valor. */
        recipesViewModel.readMealAndDietType.asLiveData().observe(viewLifecycleOwner, androidx.lifecycle.Observer { value ->
            //de este readMealAndDietType que es un  vamosFlow<MealAndDietType convertido a livedata vamos  a coger dos valores
            //y almacenarlos en mealTypeChip y dietTypeChip par amostrar los chip selecioando
            mealTypeChip = value.selectedMealType
            dietTypeChip = value.selectedDietType
            /**Siempre que abramos el dialogbottom el observer se llamara pq se hace una lectura de la datastore preference
             * y con esos datos que recogemos se los asignamos a esas variables
             * Ahora usaremos esas variables para hacer la seleccion de esos chip y mostrale al usaurio su anterior selecion para ello usaremos
             * una funcion en al que pasarmeo los id del chip y el grupo al que pertence*/

            updateChip(value.selectedMealTypeId, mView.mealType_chipGroup)
            updateChip(value.selectedDietTypeId, mView.dietType_chipGroup)
        })
        //accedemos al mealtype chip group
        mView.mealType_chipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            //de ese grupo de chip vamos a seleccionar el id del que se ha chequeado usando findview el cual tiene como tipo de
            //busqueda una vista de tipo Chip. Es decir que poneindole la clase Chip el sabe que tipo de vista tiene que buscar
            val chip = group.findViewById<Chip>(selectedChipId) //entonces en chip tendremos ese id
            val selectedMealType = chip.text.toString().lowercase(Locale.ROOT) //almavenas el texto del chip con el id que hemos detemrinado arriba
            //set the values of the top variables
            mealTypeChip = selectedMealType
            mealTypeChipId = selectedChipId
        }

        mView.dietType_chipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId) //entonces en chip tendremos ese id
            val selectedDietType = chip.text.toString().lowercase(Locale.ROOT) //almavenas el texto del chip con el id que hemos detemrinado arriba
            //set the values of the top variables
            dietTypeChip = selectedDietType
            dietTypeChipId = selectedChipId
        }

        //lsitener to apply button
        mView.apply_btn.setOnClickListener {
            /**here we are going to use our datastore through the recipes viewmodel*/
            recipesViewModel.saveMealAndDietType(
                mealTypeChip,
                mealTypeChipId,
                dietTypeChip,
                dietTypeChipId

            )
            /**when we prpess the button apply set the boolean argument to true*/
            val action = RecipesBottomSheetDirections.actionRecipesBottomSheetToRecipesFragment(true)
            /**And when we navigate to the recipe fragment we will pas the argument seted to true*/
            findNavController().navigate(action)
        }

        /**
         * Entones lo que hemso hechop es qeu cuadno clicamos cada uno de los chip vamos a guardar su id
         * y su nombr en als variabel euq tenemos arriba. Entones ahora cuadno apretamos apply guardamos esos datos en el
         * el datastore preferences repo.
         */


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