package com.robertconstantindinescu.recipeaplication.util

import androidx.recyclerview.widget.DiffUtil
import com.robertconstantindinescu.recipeaplication.models.Result


/**
 *
 * This calss take 2 parameters the old list and it type and the second paramenter is a new list
 * of the same type.
 * This class extends Diffutil class
 * DiffUtil is a utility class that calculates the difference between two lists and outputs a list of update operations that converts the first list into the second one.
It can be used to calculate updates for a RecyclerView Adapter.
 */

/**
 * Esta clase la utilizaremos para comprar dos listas. Una lista antigua y una nueva por así decirlo.
 * Graccias a DiffUtil, calculamos la diferencia entre dos listas y convierte la priemra lista en
 * la segunda. Esto es muy útil para actualizar el recyclerView cuando se hace peticiones nuevas al
 * servidor. De este modo en vez de actualizar y cambiar toda la lista del recycler, lo que vamos a hacer
 * es cambiar solo aquellos datos que son nnuevos en aquellas posiciones de los datos antiguos.
 */
class RecipesDiffUtil<T>(
   /* private val oldList: List<Result>,
    private val newList: List<Result>*/
    private val oldList: List<T>,
    private val newList: List<T>
): DiffUtil.Callback() {

    /**
     * devuelve el tamaño ed la lista vieja.
     */
    override fun getOldListSize(): Int {
        return oldList.size
    }

    /**
     * revuelve el tamaño ed la lista nueva.
     */
    override fun getNewListSize(): Int {
        return newList.size
    }
    /**
     * Esta función se autollama para determinar si dos objetos representan el mismo ítem en la lista
     * nueva y antigua..
     */
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }
    /**
     * Función que comprueba si dos items tinen los mismos datos. Este método se llama
     * por diffutil si solo areItemsTheSame devuelve true.
     */
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}