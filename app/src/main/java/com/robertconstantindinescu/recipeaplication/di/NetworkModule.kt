package com.robertconstantindinescu.recipeaplication.di

import com.robertconstantindinescu.recipeaplication.util.Constants.Companion.BASE_URL
import com.robertconstantindinescu.recipeaplication.data.network.FoodRecipesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


/**
 * Este objeto va a actuar como un modulo que nos proveera una instancia de retrofit para el remoteDataSource
 * Para ello vamos a marcar este objeto como @Module.
 * @InstallIn(ApplicationComponent::class) --> significa que todos los componentes que se crearn en
 * este objeto, su vida esta atado al ciclo de vida de la aplicación. Entonces podemos obtener una
 * instancia de retrofit desde cualquier punto de la aplicación, y esa instancia permanecera la misma
 * mientras que la app siga viva.
 *
 *
 */
@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    /**
     * Función para proveer una de las dependencias de retrofit.El cliente.
     */
    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient{
        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS) //tiempo de espera de lectura predeterminado para nuevas conexiones
            .connectTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    /**
     * in order to satify the retrofit instance dependecy we create both dependecy here and provide them
     * we return the Gsonconverter becasue the @provides will grab it when the code finishes and pass it to the retrogif fucniton
     */
    /**
     * Función que provee la segunda de las dependenias de retrofit, el GsonConverterFctory para
     * serializar el JSON.
     */
    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory{
        return GsonConverterFactory.create()

    }


    /**
     * Nosotros queremos proveernos retrofit. Entonces nos creamos una funcion
     * provideretrofit y para que el retrofit se pueda proveer tenemos que poner la etiqueta Provides
     * Esta etiqueta nos va a ayudar a proveernos retrofit.
     * Esta funcion va a retornar un objeto retrofit
     *
     * Entonces ahora si cualquiera de nuestras clases necesita una isntancia de retrofit dager
     * hilt va a saber como darsela.
     * Cada ve que se llaama a retrofit se va a crear una instancia
     * nueva y eso es un consumo de memoria muy grande.
     * Entonces para ello a la funcion le añadimos la etiwuta de @Singleton es decir que manetenga
     * una únnica instanica de retrofit.
     */

    @Singleton
    @Provides
    fun provideRetrofitInstance(
        //okHttpClient se usa para enviar peticiones
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ):Retrofit{

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    /**
     * here by specifiing the retuned tipe we are tellin hilt library which class we want to inject
     *Esta función se encarga de proveer los servicios Api. Necesita de retrofit para ello que ya lo
     * hemos provisto arriba.
     * Indicando el tipo de retorno de la función, estamos indicando el tipo de dato a inyectar. En
     * este caso una interfaz que realiza las peticiones al servidor.
     */
    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): FoodRecipesApi { //devolvemos la interfaz con todas las dependencias ya creadas
       return retrofit.create(FoodRecipesApi::class.java)
    }

    /**
     * @Resumen
     * Entonces para proveer los servicios Api que hacen las peticiones, necesitamos proveer
     * todas las dependencias. Con lo cual para proveer FoodRecipeApi al remoteDatasoruce necesitamos
     * satisfacer sus dependecias de retrofit que a su vez necesita satisfacer las dependecias de
     * OkHttpClient, GsonConverterFactory
     */


}