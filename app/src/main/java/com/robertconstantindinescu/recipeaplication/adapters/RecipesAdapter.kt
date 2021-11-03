package com.robertconstantindinescu.recipeaplication.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.robertconstantindinescu.recipeaplication.databinding.RecipesRowLayoutBinding
import com.robertconstantindinescu.recipeaplication.models.FoodRecipe
import com.robertconstantindinescu.recipeaplication.models.Result
import com.robertconstantindinescu.recipeaplication.util.RecipesDiffUtil

class RecipesAdapter: RecyclerView.Adapter<RecipesAdapter.MyViewHolder>() {

    //nos declaramos una variable que tendra una lista de los objetos result
    private var recipes = emptyList<Result>()

    /**
     * whenever we fetch new data from the api, we are going to call this set data function from the fragment
     * and we are going to pass aour new data and the list is updated automatically.
     *
     */
    fun setData(newData: FoodRecipe){
        val recipesDiffUtil = RecipesDiffUtil(recipes, newData.results)
        val diffUtilResult  = DiffUtil.calculateDiff(recipesDiffUtil)

        recipes = newData.results  //set the recipe with the list of Results
        //now tell the reccler to update the values when we recive new daa
        //con esto le decimos al recycler qu ehay nuevos datos y ahora este va a solicitar un update al adapter
        /*Pero tiene un inconvenietne y es que relantiza el proceso porque lo que ghace es decirle al recycler que
        * hay nuevos datos y que este pida un update. Pero al indicarle eso, no tiene en cuenta qeu puede haber
        * elementos en la antigua lista que pueden exisitir en la lista actual.
        * En este caso podemos usar DiffUtil, que comprueba y comparar todoa la lista de recipe con la nuva lista que le estmaos pasando
        * and update only those recibes or views which are new. I mean  update only the data in the class
        * when he founds a diference between old and new and then modifies the vierw.  */
        //notifyDataSetChanged()

        //Distribuye los eventos de actualización al adaptador dado.
        diffUtilResult.dispatchUpdatesTo(this)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //we get the parent from this function
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe)

    }

    override fun getItemCount(): Int {
        return  recipes.size
    }

    /**
     * Because we used daba Binding here in the viewholder we need to add in the constructor the
     * binding and the type is the name of the layput + Binding. This class is generated when you convert
     * the layout to databinding
     * And we are going to use this binding to actually bind our Api data with these recipes row layout
     */
     class MyViewHolder(private val binding: RecipesRowLayoutBinding): //es una clase que genera el sistema con todos los compoenetnes entnoeces puedes usar binding para acceder a todos los elementos del layout
        RecyclerView.ViewHolder(binding.root) { //we passe the root of the rowlayout

        //en la funcion bind, usaremos un objeto Result
        fun bind(result: Result){
            //now we use the bindig to acces the viewws. and using that we are able to acces the variable we defined in the recipes_row_layout
            //and the type of that variable y de model class Result.
            /**
             * so now as we can see we chose that class ad we are going to combine that with our result from the parameters
             * of this bind function. Entonces le dices que la variable result del layout que nos hemos definido y que es de tipo
             * Result sea igual o la seteas mejor dicho al objeto result que obtenemo del onBindviewHolder.
             * Es decir que estas haceidno un set en la variable result que hemos definido en el layout y como es de tipo Result
             * pues se setean las propiedades. y ahora vamos a ejecutar pendingbindings. Esta es una funcion that basically update our layout when there is
             * a change inside our data
             */
            binding.result = result
            binding.executePendingBindings()

        }

        /**
         * This function we are returning the MyViewHolder class
         * with that function we recive the parent from onCreate method and use that parent
         * to creeate the layput inflater and binding.
         */
        companion object{
            fun from(parent: ViewGroup): MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context) //preparas el recylcer para aceptar el inlflado
                val binding = RecipesRowLayoutBinding.inflate(layoutInflater, parent, false) //inflas el layout
                return MyViewHolder(binding)
            }
        }

    }

  /*  inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {


    }*/

}