package com.example.myfitnessapp

import androidx.paging.PagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/**
 * This is a basic test for the successful result.
 * More tests can be added for the other load results, like error and loading.
 *
 * In case of a remote keyed mediator, the mediator could be provided in as a constructor parameter, giving the
 * opportunity to test that mediator separately, and provide it in here as a mock, similar to how WgerApiService is
 * provided.
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class ExercisesPagingSourceTest {

    @Test
    fun `Load returns Page when Successful Load Of Item Keyed Data`() = runTest {
        val wgerApiService: WgerApiService = mock()
        whenever(wgerApiService.getExercise(11, 1)).thenReturn(exercisesResponse)
        whenever(wgerApiService.getMuscleResponse()).thenReturn(musclesResponse)
        val query = "bicep"
        val pagingSource = ExercisesPagingSource(wgerApiService, query)
        val actual: PagingSource.LoadResult<Int, Exercises> =
            pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 1,
                    loadSize = 20,
                    placeholdersEnabled = false
                )
            )


        val expected = PagingSource.LoadResult.Page(
            data = exercises,
            prevKey = null,
            nextKey = 2
        )

        assertEquals(expected, actual)
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