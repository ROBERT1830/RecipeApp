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
 * This class is going to act as a module and therefore build and provide the retorfit instnace to the datasource
 * in order to to t hat we need to mark this object with @Module because module is where we
 * Then add the InstallIn(ApplicationComponent) thath means that the componenets that are created here in the module
 * its lifetime is of application. So we can use it from everywhere. I mean get instances from every poknt of the app
 * and the instance that it wil be given is the same til the app is alive.
 */

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

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
    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory{
        return GsonConverterFactory.create()
        //so with this function we satify the first dependecy for the retriofit
    }


    /**
     * nosootros quremos provernos retrofit. Entonces nos creamos una funcion
     * privideretrofit y para que el retrofit se pueda proveer tenemos que poner la etiqueta Provides
     * Esta etiqueta nos va a ayudar a proveernos retrofit cuadno se haya completado el codigo de la funciuon
     * Esta funcion va a retornar un objeto retrofit. ahora dentro de esta funcion vamos a
     * pegar el codigo que teniamos en la clase rETROFIThELPER.
     *
     * eNTONCES ahora si cualquiera de nuestras clases necesita una isntancia de retrofit dager hilt va a saber como darsela
     * Ahora por ejemplo en alguna de las clases le decimos: inyectame retorift y dager dira vale a ver odnde tengo
     * a retorfit lo tengo aqui en el modulo, y si lo puede provver te lo madna
     * Pero aun asi nos queda un problema. Y es qeu cada ve que se llaama a retrofit se va a crear una instancia
     * nueva y eso es un consumo de memoria muy grande.
     * Entonces para ello a la funcion le añadimos la etiwuta de @sINGLETON
     * cUANDO hacemos esto le decimos que use el patron de diseño singleton es decir que manetenga una únnica instanica de retrofit.
     * Imaignate una clase pide retrofit, pued dira dager
     * esta credo el retrofit? no pues te lo creo y te lo mando.
     * Ahora otra clase diferente   dice inyectameten retrofit.. Y dagger dice anda ya lo habia creado pues te mando
     * la misma instancia. y asi no creas mas instancias inncesearias.
     */

    @Singleton //esto lo qe hace es que hilt va a crear esta instainca de retorfit a nivel de application pq estamos usando singleton. con lo cual mientras que la app viva, y se hagan lalamdas a retroffit, siemrpe vamos a nter la misma instnaica de retrofit.
    @Provides
    fun provideRetrofitInstance(
        /*To create retrfoit instance we wil need to satify two dependencies okHttpCliente and tjhe gSonconverter facotry*/
        okHttpClient: OkHttpClient, //First dependecy of retrofit --> used to send http requests and read their response
        gsonConverterFactory: GsonConverterFactory //secod dependecy of retrofit-->converter JSON
    //--> como ambas van en el construcitr tambien necesitamos hacer la inyeccion. y como son librerias pues hay que hacerloa qui en module. lo hacemos arriba
    ):Retrofit{

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) //the client is http used for request so we are going to use http for requesting
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    /**
     * here by specifiing the retuned tipe we are tellin hilt library which class we want to inject
     *
     */
    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): FoodRecipesApi { //devolvemos la interfaz con todas las dependencias ya creadas
       return retrofit.create(FoodRecipesApi::class.java)
    }


    /**
     * So in order to provide the api service (la que hace el request) we need to provide all the dependecies before that.
     * So to provide the FoodRecipeApi to the class (RemoteDarasource) that will implement it we need to satify retorfit dependecy
     * and in order to satify retrofit dependecy we need to provide OkHttpClient, GsonConverterFactory
     */
}