package com.example.myfitnessapp


/**
 * This annotation allows to open some classes for mocking purposes while they are final in release builds.
 * Full package of this class is used at gradle global variable named "allopen_annotation_package_location"
 * Include file "allopen-config.gradle" in modules where this functionality is needed
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class OpenClass

/**
 * Annotate a class with [OpenForTesting] if you want it to be extendable in debug builds.
 */
@OpenClass
@Target(AnnotationTarget.CLASS)
annotation class OpenForTesting

// Forked from https://github.com/googlesamples/android-architecture-components/blob/d81da2cb1e3d61e40f052e631bb15883d0f9f637/GithubBrowserSample/app/src/debug/java/com/android/example/github/testing/OpenForTesting.kt