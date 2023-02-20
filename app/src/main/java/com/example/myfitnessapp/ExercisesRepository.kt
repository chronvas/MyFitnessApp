package com.example.myfitnessapp

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

@OpenForTesting
class ExercisesRepository @Inject constructor(
    private val wgerApiService: WgerApiService
) {
    fun getExercises(query: String): Flow<PagingData<Exercises>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
            ),
            pagingSourceFactory = {
                ExercisesPagingSource(wgerApiService, query)
            }
        ).flow
    }
}