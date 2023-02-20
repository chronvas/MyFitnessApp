package com.example.myfitnessapp

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert
import org.junit.Test


internal class WgerApiServiceTest {

    @Test
    fun `returns deserialized response when statusCode is OK`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel(s),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val mockApiClient = HttpClient(mockEngine) {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                        encodeDefaults = true
                    })

                }
            }

            val wgerApiService = WgerApiService(mockApiClient)

            val actual: ExercisesResponse = wgerApiService.getExercise(1, 1)

            val expected = ExercisesResponse(
                count = 2,
                next = "https://wger.de/api/v2/exercise/?limit=20&offset=20",
                previous = null,
                results = listOf(
                    Exercises(
                        id = 345,
                        name = "2 Handed Kettlebell Swing",
                        description = "Two Handed Russian Style Kettlebell swing",
                        muscles = emptyList(),
                        musclesSecondary = emptyList()
                    ),
                    Exercises(
                        id = 1061,
                        name = "Abdominal Stabilization",
                        description = "",
                        muscles = listOf(6),
                        musclesSecondary = listOf(14)
                    )
                )
            )

            Assert.assertEquals(expected, actual)
        }
    }

    /**
     * Assert that it throws an exception. Junit 5 has a better exception testing mechanism.
     */
    @Test(expected = Throwable::class)
    fun `returns deserialized response when statusCode is Error`() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel(s),
                    status = HttpStatusCode.BadRequest,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val mockApiClient = HttpClient(mockEngine) {
                install(ContentNegotiation) {
                    json(Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                        encodeDefaults = true
                    })

                }
            }

            val wgerApiService = WgerApiService(mockApiClient)
            wgerApiService.getExercise(1, 1)
        }
    }


    val s = """
       {
  "count": 2,
  "next": "https://wger.de/api/v2/exercise/?limit=20&offset=20",
  "previous": null,
  "results": [
    {
      "id": 345,
      "uuid": "c788d643-150a-4ac7-97ef-84643c6419bf",
      "name": "2 Handed Kettlebell Swing",
      "exercise_base": 9,
      "description": "Two Handed Russian Style Kettlebell swing",
      "creation_date": "2015-08-03",
      "category": 10,
      "muscles": [],
      "muscles_secondary": [],
      "equipment": [
        10
      ],
      "language": 2,
      "license": 2,
      "license_author": "deusinvictus",
      "variations": [
        345,
        249
      ],
      "author_history": [
        "deusinvictus"
      ]
    },
    {
      "id": 1061,
      "uuid": "60d8018d-296f-4c62-a80b-f609a25d34ea",
      "name": "Abdominal Stabilization",
      "exercise_base": 56,
      "description": "",
      "creation_date": "2022-10-11",
      "category": 10,
      "muscles": [
        6
      ],
      "muscles_secondary": [
        14
      ],
      "equipment": [
        4
      ],
      "language": 2,
      "license": 2,
      "license_author": "wger.de",
      "variations": [],
      "author_history": [
        "wger.de"
      ]
    }
  ]
}
    """.trimIndent()
}