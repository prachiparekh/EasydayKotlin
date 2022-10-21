package com.app.easyday.app.sources.remote.di

import com.app.easyday.BuildConfig
import com.app.easyday.app.sources.remote.HeaderInterceptor
import com.app.easyday.app.sources.remote.apis.EasyDayApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    companion object {
        var BASE_URL_RESOURCES = "http://63.32.98.218:3000/api/v1/"
    }

    /*
    ** Api
    */

    @Provides
    @Singleton
    internal fun providePostcardApi(retrofit: Retrofit) = retrofit.create(EasyDayApi::class.java)

    @Provides
    @Singleton
    internal fun provideRxRetrofit(
        rxCallAdapterFactory: RxJavaCallAdapterFactory,
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ) =
        Retrofit.Builder()
            .baseUrl(BASE_URL_RESOURCES)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(rxCallAdapterFactory)
            .client(okHttpClient)
            .build()


    @Provides
    @Singleton
    internal fun provideRxCallAdapterFactory() = RxJavaCallAdapterFactory.create()

    @Provides
    @Singleton
    internal fun provideGson() = GsonConverterFactory.create()

    @Provides
    @Singleton
    internal fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        headerAuthInterceptor: HeaderInterceptor
    ) =
        OkHttpClient.Builder()
            .addInterceptor(headerAuthInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

    @Provides
    @Singleton
    internal fun provideHeaderAuthInterceptor() = HeaderInterceptor()

    @Provides
    @Singleton
    internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level =
            if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE

        return httpLoggingInterceptor
    }


}