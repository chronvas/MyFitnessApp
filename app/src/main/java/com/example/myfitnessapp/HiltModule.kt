package com.example.myfitnessapp

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class HiltModule {
    @Provides
    fun provideExercisesRepository(wgerApiService: WgerApiService): ExercisesRepository =
        ExercisesRepository(wgerApiService)
}