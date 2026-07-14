package com.example.carlogo.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SpecialBrandCardPresentationTest {

    @Test
    fun `special brand card reserves a fixed logo row and up to four name lines`() {
        assertEquals(64, SpecialBrandCardPresentation.INFO_ROW_HEIGHT_DP)
        assertEquals(2, SpecialBrandCardPresentation.INFO_MAX_LINES)
        assertEquals(4, SpecialBrandCardPresentation.BRAND_NAME_MAX_LINES)
    }

    @Test
    fun `brand name auto fit chooses the largest scale that fits`() {
        assertEquals(1f, BrandNameAutoFit.selectLargestFittingScale { it == 1f })
        assertEquals(0.8f, BrandNameAutoFit.selectLargestFittingScale { it <= 0.8f })
        assertNull(BrandNameAutoFit.selectLargestFittingScale { false })
    }

    @Test
    fun `brand name auto fit stops measuring after the first fitting scale`() {
        val measuredScales = mutableListOf<Float>()

        val selected = BrandNameAutoFit.selectLargestFittingScale { scale ->
            measuredScales += scale
            scale <= 0.8f
        }

        assertEquals(0.8f, selected)
        assertEquals(listOf(1f, 0.9f, 0.8f), measuredScales)
    }
}
