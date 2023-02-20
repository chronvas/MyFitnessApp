package com.example.myfitnessapp

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

@OpenForTesting
class WgerApiService @Inject constructor(private val httpClient: HttpClient) {

    /**
     * Could use something fancier to construct the URLs, like android.net.Uri.Builder but that builder is not
     * possible to Unit test, and it would need an instrumented test due to it's android dependencies.
     *
     * The base urls can be placed somewhere else more central. Preferences for this may vary, some examples include
     * placing it in the buildconfig.
     */
    companion object {
        private const val limit = 20
    }

    suspend fun getMuscleResponse(): MusclesResponse {
        val baseUrl = "https://wger.de/api/v2/muscle"
        return httpClient.get(baseUrl).body()
    }

    suspend fun getExercise(muscleId: Long, page: Int): ExercisesResponse {
        val baseUrl = "https://wger.de/api/v2/exercise?muscles=$muscleId&limit=$limit"

        return if (page > 1) {
            val offset = page * 20
            httpClient.get("$baseUrl&offset=$offset").body()
        } else
            httpClient.get(baseUrl).body()
    }
}
