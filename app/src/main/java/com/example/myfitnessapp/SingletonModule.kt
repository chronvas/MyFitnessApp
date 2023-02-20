package com.example.myfitnessapp

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.FileStorage
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import java.io.File
import javax.inject.Singleton
import kotlinx.serialization.json.Json


@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Singleton
    @Provides
    fun provideHttpClient(@ApplicationContext appContext: Context): HttpClient {
        return HttpClient(CIO) {
            install(HttpCache) {
                publicStorage(FileStorage(File(appContext.cacheDir.absolutePath)))
            }

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                    encodeDefaults = true
                })

            }

            install(Logging) {
                logger = object : Logger {
                    @SuppressLint("LogNotTimber")
                    override fun log(message: String) {
                        Log.v("http log: ", message)
                    }
                }
                level = LogLevel.ALL
            }

            install(DefaultRequest) {
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }

}
