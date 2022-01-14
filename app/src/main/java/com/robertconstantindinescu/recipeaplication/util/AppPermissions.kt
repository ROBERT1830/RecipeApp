package com.robertconstantindinescu.recipeaplication.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val STORAGE_REQUEST_CODE = 1000

class AppPermissions {
    fun isStorageOk(context: Context): Boolean {
        //compruebas si hay se han aceptado para leer el almacenamiento externo.
        /*checkSelfPermission devolvera granted(true) si se los permisos están aceptados. */
        return ContextCompat.checkSelfPermission( //ContextCompat nos permite acceder  los recuross del contexto de la aplicación.
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestStoragePermission(activity: Activity) {
        //pedir permisos al usuario.
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            STORAGE_REQUEST_CODE //se devuelve este código cuadno los permisos se acepten o no.
        )
    }
}