package com.robertconstantindinescu.recipeaplication.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 *Función de extensión para live data. Esta función nos permite observar un objeto livedata solo
 * una vez y no de forma contía. Es útil cuadno hacemos la petición a la base de datos.
 */
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            removeObserver(this)
            observer.onChanged(t)
        }
    })
}