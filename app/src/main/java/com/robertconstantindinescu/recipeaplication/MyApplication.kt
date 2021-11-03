package com.robertconstantindinescu.recipeaplication

import android.app.Application
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

/**
 * This cals should exend from aplicaciton class an we need to ad the anotation
 * hiltandroidaaApp. tHIS GENERATE SOME CODE BEHIND. and here is where dagger componenets are generated autoamtically
 * for us. Fro example when we anotate a activity with @AndroiEntryPoitn hilt creates a component and savet here
 * now open android amnifest and ad name fro the new application class. Rememeber this is the satart point
 * from the app, the application class. so this clas will be as well
 */
@HiltAndroidApp
class MyApplication: Application() {
}