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
 * Este fragmento, no hereda de fragment como es habitual. Lo hace de BottomSheetDialogFragment
 *puesto que será un fragmento que se mostrará sobre el RecipesFragment a modo de diálogo.
 */
class RecipesBottomSheet : BottomSheetDialogFragment() /*Fragment()*/ {

    private  lateinit var recipesViewModel: RecipesViewModel

    /**
     * Nos creamos las diferentes variables para los chip que usaremos en el método saveMealAndDietType
     * de RecipesViewModel.
     * Id 0 hace referencia al primero de los chip selecionado.
     */
    private var mealTypeChip = DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0
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

        /**
         * Para implementar la perssitencia de datos en el fragment doalog debemos de leer los datos de
         * dataStore preferences y mostrarlos en el fragmento dialog con la selección anterior.
         * Para ello vamos a coger ese Flow<MealAndDietType> almacenado en readMealAndDietType
         * y lo pasmos a livedata para que pueda ser observado el cambio de valor.
         *
         */
        recipesViewModel.readMealAndDietType.asLiveData().observe(viewLifecycleOwner, androidx.lifecycle.Observer { value ->
            //de este readMealAndDietType que es un Flow<MealAndDietType convertido a livedata vamos
            // a coger dos valores y almacenarlos en mealTypeChip y dietTypeChip par amostrar los chip selecioandos
            mealTypeChip = value.selectedMealType
            dietTypeChip = value.selectedDietType
            /**Siempre que abramos el dialogbottom el observer se llamara pq se hace una lectura de la
             * datastore preference y con esos datos que recogemos se los asignamos a esas variables
             * Ahora usaremos esas variables para hacer la seleccion de esos chip y mostrale al usaurio
             * su anterior selecion. Para ello usaremos una funcion en al que pasaremos los id del
             * chip y el grupo al que pertence*/
            updateChip(value.selectedMealTypeId, mView.mealType_chipGroup)
            updateChip(value.selectedDietTypeId, mView.dietType_chipGroup)
        })
        /**Now we have to implement onCheked listener and whenever we check one chipp we want to store its value
         * and its Id. And when press the applu button we wwant to save taht selected chip id and name to the datastore*/

        /**
         * Implementamos el setOnCheckedChangeListener, de modo que cuando selecionemos un nuevo chip
         * alamcenaremos su valor e id.
         */
        mView.mealType_chipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            //de ese grupo de chip vamos a seleccionar el id del que se ha chequeado usando
            // findview el cual tiene como tipo de busqueda una vista de tipo Chip.
            // Es decir que poneindole la clase Chip el sabe que tipo de vista tiene que buscar
            val chip = group.findViewById<Chip>(selectedChipId)
            //almavenas el texto del chip con el id que hemos detemrinado arriba
            val selectedMealType = chip.text.toString().lowercase(Locale.ROOT)
            //sseteamos las variables con los valores obtenidos de los chip.
            mealTypeChip = selectedMealType
            mealTypeChipId = selectedChipId
        }


        /**
         * ídem pero para el tipo de dieta.
         */
        mView.dietType_chipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId)
            val selectedDietType = chip.text.toString().lowercase(Locale.ROOT)
            dietTypeChip = selectedDietType
            dietTypeChipId = selectedChipId
        }

        /**
         * Listener asignado al botón para guardar en DataStore la selección del usuario.
         */
        mView.apply_btn.setOnClickListener {
            /**here we are going to use our datastore through the recipes viewmodel*/
            recipesViewModel.saveMealAndDietType(
                mealTypeChip,
                mealTypeChipId,
                dietTypeChip,
                dietTypeChipId

            )
            /**when we prpess the button apply set the boolean argument to true*/
            /*Ponemos los argumentos que pasamos al RecipeFragment, a true. Puesto que esto
            * lo usaremos para hacer que la app leea de la base de datos o del servidor.
            * Si resulta que venimos del dialog fragmet la app hara la petición al servior. */
            val action = RecipesBottomSheetDirections.actionRecipesBottomSheetToRecipesFragment(true)
            //hacemos la navegación
            findNavController().navigate(action)
        }
        return mView
    }

    /**
     * Método utilizado para actualizar la selección de los chip.
     */
    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {
        if (chipId != 0){
            try {
                chipGroup.findViewById<Chip>(chipId).isChecked = true

            }catch (e: Exception){
                Log.d("RecipesBottomSheet", e.message.toString())
            }
        }
    }


}