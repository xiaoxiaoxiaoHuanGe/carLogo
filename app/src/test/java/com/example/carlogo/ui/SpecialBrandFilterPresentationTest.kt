package com.example.carlogo.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class SpecialBrandFilterPresentationTest {
    @Test
    fun `special filter uses the approved 3 to 7 control ratio and wide menu`() {
        assertEquals(3f, SpecialBrandFilterPresentation.FILTER_WEIGHT)
        assertEquals(7f, SpecialBrandFilterPresentation.SEARCH_WEIGHT)
        assertEquals(220, SpecialBrandFilterPresentation.MENU_MIN_WIDTH_DP)
        assertEquals("筛选汽车品牌", SpecialBrandFilterPresentation.BUTTON_LABEL)
        assertEquals(56, SpecialBrandFilterPresentation.CONTROL_HEIGHT_DP)
        assertEquals("选择品牌，针对练习", SpecialBrandFilterPresentation.SUBTITLE_LABEL)
        assertEquals("当前：", SpecialBrandFilterPresentation.CURRENT_FILTER_PREFIX)
    }
}
