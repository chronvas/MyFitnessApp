package com.example.myfitnessapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

@HiltViewModel
@OpenForTesting
class ExercisesViewModel @Inject constructor(
    private val repository: ExercisesRepository,
) : ViewModel() {

    /**
     * The .cacheIn does not seem to work as expected on configuration change, even though the query is saved.
     * This can be improved.
     */
    fun exercises(query: String): Flow<PagingData<Exercises>> {
        return repository.getExercises(query).cachedIn(viewModelScope)
    }

}