package com.robertconstantindinescu.recipeaplication.data

import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * Este repositorio se utiliza para escoger el lugar de dónde vamos a buscar los datos, es decir,
 * a que sitio nos conectamos para traer los datos. Para ello es necesario inyectar los dataSource
 *
 * @ActivityRetainedScoped esto define que el ámbito de este repositorio está al ciclo de vida de la activity
 * De modo que tendremos la misma instancia de ambos datasource minetras qeu la activity siga viva.
 *
 */
@ActivityRetainedScoped
class Repository @Inject constructor(
    remoteDataSource: RemoteDataSource,
    localDataSource: LocalDataSource
) {
    val remote = remoteDataSource
    val local = localDataSource
}