package com.example.carlogo.ui

internal object BrandNameAutoFit {
    val candidateScales = listOf(1f, 0.9f, 0.8f, 0.7f, 0.6f, 0.5f)

    fun selectLargestFittingScale(fits: (Float) -> Boolean): Float? =
        candidateScales.firstOrNull(fits)
}
