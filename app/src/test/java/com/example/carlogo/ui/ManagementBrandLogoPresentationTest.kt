package com.example.carlogo.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class ManagementBrandLogoPresentationTest {

    @Test
    fun `management brand logo uses the approved square tile dimensions`() {
        assertEquals(48, ManagementBrandLogoPresentation.TILE_SIZE_DP)
        assertEquals(14, ManagementBrandLogoPresentation.TILE_CORNER_RADIUS_DP)
        assertEquals(6, ManagementBrandLogoPresentation.TILE_PADDING_DP)
    }

    @Test
    fun `management brand cards use one fixed height and one line names`() {
        assertEquals(78, ManagementBrandLogoPresentation.CARD_HEIGHT_DP)
        assertEquals(1, ManagementBrandLogoPresentation.BRAND_NAME_MAX_LINES)
    }
}
