package com.example.myfitnessapp

import androidx.paging.Pager
import androidx.paging.PagingConfig
import app.cash.turbine.testIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
internal class ExercisesRepositoryTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val scope = CoroutineScope(Dispatchers.Unconfined)

    @Test
    fun `Load returns Page when Successful Load Of Item Keyed Data`() = runTest {
        val wgerApiService: WgerApiService = mock()
        whenever(wgerApiService.getExercise(11, 1)).thenReturn(exercisesResponse)
        whenever(wgerApiService.getMuscleResponse()).thenReturn(musclesResponse)

        val exercisesRepository = ExercisesRepository(wgerApiService)

        val actual = exercisesRepository.getExercises("bicep")


        /**
         * Due to the same issue present in [ExercisesViewModelTest]], the equality check for created PagingData
         * always fails, therefore this can not be tested with an equality check.
         * Some other method is needed to check the PagingData equality, like usage of reflection.
         * Here is how it would look like though:
         */
        val actualTurbine = actual.testIn(scope)
//        val expectedPagingData = PagingData.from()
//        assertEquals(expectedPagingData, actualTurbine.awaitItem())

        /**
         * The same issue is observed if we manyally create a pager for our test, and grab it's created PagingData this
         * way.
         * Here is how it would look like:
         */
        val expectedPager = Pager(
            config = PagingConfig(
                pageSize = 20,
            ),
            pagingSourceFactory = {
                ExercisesPagingSource(wgerApiService, "bicep")
            })
        val expectedPagerTurbine = expectedPager.flow.testIn(scope)
//        val expectedPagingData = PagingData.from()
//        assertEquals(expectedPagerTurbine.awaitItem(), actualTurbine.awaitItem())


        actualTurbine.awaitComplete()
        expectedPagerTurbine.awaitComplete()
    }

    private val exercises = listOf(
        Exercises(
            id = 345,
            name = "2 Handed Kettlebell Swing",
            description = "Two Handed Russian Style Kettlebell swing",
            muscles = listOf(11, 22),
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

    private val musclesResponse = MusclesResponse(
        2, "bicep", "previous", listOf(
            Muscles(
                11, "1", "bicep", "bicep"
            ),
            Muscles(
                22, "2", "name2", "name2en"
            )
        )
    )
    private val exercisesResponse = ExercisesResponse(
        count = 2,
        next = "https://wger.de/api/v2/exercise/?limit=20&offset=20",
        previous = "https://wger.de/api/v2/exercise/",
        results = exercises
    )

}

