package com.robertconstantindinescu.recipeaplication

import android.app.Application
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

/**
 * En este proyecto hemos utilizado dagger Hilt para inyección de dependencias.
 * De modo que cada vez que vayamos a anotar una clase con un anotador @AndroiEntryPoitn
 * hilt creara un componenete de esa clase automáticamente aqui en est clase. Debidoa a que esta clase
 * hereda de Application, esta clase será un start point cuadno se inicie la app. Y contendrá todos
 * esos componenetes  que ha creado Hilt para hacer la inyección de dependencias.
 *
 */
@HiltAndroidApp
class MyApplication: Application() {
}