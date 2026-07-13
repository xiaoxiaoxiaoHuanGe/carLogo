package com.example.carlogo.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SeedDataTest {
    @Test
    fun `seed data contains forty brands split into balanced exclusive categories`() {
        assertEquals(40, SeedData.brands.size)

        val categories = SeedData.brands.groupBy { it.category }
        assertEquals(
            setOf("新能源主流", "新能源新势力", "豪华品牌", "合资与国际品牌", "国产自主与高端"),
            categories.keys,
        )
        assertTrue(categories.values.all { it.size in 6..10 })
    }

    @Test
    fun `byd high end brands remain independently selectable with parent labels`() {
        val yangwang = SeedData.brands.firstOrNull { it.id == "yangwang" }
        val fangchengbao = SeedData.brands.firstOrNull { it.id == "fangchengbao" }

        assertNotNull(yangwang)
        assertNotNull(fangchengbao)
        assertEquals("比亚迪旗下", yangwang?.parentBrand)
        assertEquals("比亚迪旗下", fangchengbao?.parentBrand)
    }
}
