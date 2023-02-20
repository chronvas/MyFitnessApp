package com.example.myfitnessapp

import androidx.paging.PagingData
import app.cash.turbine.testIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

@OptIn(ExperimentalCoroutinesApi::class)
internal class ExercisesViewModelTest {

    @get:Rule
    val coroutineRule = MainDispatcherRule()

    private val scope = CoroutineScope(Dispatchers.Unconfined)

    @Test
    @Ignore
    fun fails2(): Unit = runBlocking {
        val pagingData = PagingData.from(expected.results)
        val pagingDataFlow: Flow<PagingData<Exercises>> = flowOf(pagingData)

        val repository = mock<ExercisesRepository> {
            on { getExercises("") }.thenReturn(pagingDataFlow)
        }

        val viewModel = ExercisesViewModel(repository)

        val actual = viewModel.exercises("")

        verify(repository).getExercises("")
        verifyNoMoreInteractions(repository)

        /**
         * The equality check for created PagingData does not work. You can verify that by running:
        if (PagingData.from(expected.results) != PagingData.from(expected.results)) {
        throw java.lang.RuntimeException()
        }
         * This test will always fail unless a new way of testing the PagingData is added here, like reflection.
         * Here is how it would look like though:
         */
        runTest {
            val turbine1 = actual.testIn(scope)
            assertEquals(pagingData, turbine1.awaitItem())
            turbine1.awaitComplete()
        }
    }

    private val expected = ExercisesResponse(
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
}

