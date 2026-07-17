package com.example.carlogo.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class ManagementBrandLogoPresentationTest {

    @Test
    fun `brand hub retains model list and practice labels`() {
        assertEquals("全部车型", BrandHubPresentation.ALL_MODELS_LABEL)
        assertEquals("开始练习", BrandHubPresentation.START_PRACTICE_LABEL)
    }

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
