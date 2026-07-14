package com.example.carlogo.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class SpecialBrandLogoPresentationTest {

    @Test
    fun `special brand logo uses the approved square tile dimensions`() {
        assertEquals(64, SpecialBrandLogoPresentation.TILE_SIZE_DP)
        assertEquals(14, SpecialBrandLogoPresentation.TILE_CORNER_RADIUS_DP)
        assertEquals(8, SpecialBrandLogoPresentation.TILE_PADDING_DP)
    }
}
