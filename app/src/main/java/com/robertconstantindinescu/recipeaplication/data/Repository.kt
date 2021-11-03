package com.robertconstantindinescu.recipeaplication.data

import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject


/**
 * Tis repository is used to chose from where we want to fetch data or to wich database conect and get
 * the data. So for taht we will ned to inject those datasoruces inside here.
 * For now only add the remote but in the furutre the other datasource
 * We need to anotate this clas with a scope anotation. @ActivityRetainedScope
 * this is a scope anotation for binding that should exists for the life of an activity and survive
 * So since we are going to inject this repository in the viewmodel later on,
 * by adding this @ this repository will survive till the activity survive and will survive to changes as wwll
 * i mean we will have the same instance while the activity is alive and some changes souch as rotating the screen
 */

@ActivityRetainedScoped
class Repository @Inject constructor(
    remoteDataSource: RemoteDataSource,
    localDataSource: LocalDataSource //now inject the local dataasource as well.
) {
    //store the remoteDatasource in this variable so we can acces this variable inside our viewmodel latter on
    val remote = remoteDataSource
    val local = localDataSource
}