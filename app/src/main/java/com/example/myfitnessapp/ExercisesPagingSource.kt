package com.example.myfitnessapp

import androidx.paging.PagingSource
import androidx.paging.PagingState
import timber.log.Timber

class ExercisesPagingSource(
    private val wgerApiService: WgerApiService,
    private val query: String,
) : PagingSource<Int, Exercises>() {
    override fun getRefreshKey(state: PagingState<Int, Exercises>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Exercises> {
        return try {
            val page = params.key ?: 1

            if (query.isEmpty()) return LoadResult.Invalid()

            val muscleId = wgerApiService.getMuscleResponse().results
                .first { muscle ->
                    muscle.nameEn.contains(query, true)
                }.id

            /**
             * Here each response call can fail individually. This code does not take that into account.
             * To take that into account, it can start from the [wgerApiService] returning a state instead of
             * the data class directly.
             */
            val exercises = wgerApiService.getExercise(
                muscleId = muscleId,
                page = page
            ).results.distinct()

            LoadResult.Page(
                data = exercises,
                prevKey = if (page == 1) null else page.minus(1),
                nextKey = if (exercises.isEmpty()) null else page.plus(1),
            )
        } catch (e: Exception) {
            Timber.e(e)
            LoadResult.Error(e)
        }
    }
}