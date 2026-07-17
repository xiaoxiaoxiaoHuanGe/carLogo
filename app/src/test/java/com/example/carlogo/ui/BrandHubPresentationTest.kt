package com.example.carlogo.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class BrandHubPresentationTest {
    @Test
    fun `brand hub uses the approved labels`() {
        assertEquals("首页", BrandHubPresentation.HOME_NAV_LABEL)
        assertEquals("品牌", BrandHubPresentation.BRAND_NAV_LABEL)
        assertEquals("学习记录", BrandHubPresentation.LEARNING_RECORD_SHORTCUT_LABEL)
        assertEquals("错题记录", BrandHubPresentation.MISTAKE_RECORD_LABEL)
        assertEquals("开始练习", BrandHubPresentation.START_PRACTICE_LABEL)
        assertEquals("全部车型", BrandHubPresentation.ALL_MODELS_LABEL)
    }
}
