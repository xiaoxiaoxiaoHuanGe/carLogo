package com.example.carlogo.ui

import com.example.carlogo.domain.model.Brand
import org.junit.Assert.assertEquals
import org.junit.Test

class BrandPracticeFilterTest {

    private val brands = listOf(
        Brand(
            id = "bmw",
            nameZh = "宝马",
            nameEn = "BMW",
            category = "豪华品牌",
            cars = emptyList(),
        ),
        Brand(
            id = "tesla",
            nameZh = "特斯拉",
            nameEn = "Tesla",
            category = "新能源主流",
            cars = emptyList(),
        ),
        Brand(
            id = "byd",
            nameZh = "比亚迪",
            nameEn = "BYD",
            category = "国产自主与高端",
            cars = emptyList(),
        ),
    )

    @Test
    fun `all brands filter returns every brand before a search`() {
        assertEquals("全部品牌", ALL_BRANDS_FILTER_LABEL)
        assertEquals(brands, filterPracticeBrands(brands, selectedCategory = null, query = ""))
    }

    @Test
    fun `category filter keeps only matching brands`() {
        assertEquals(
            listOf(brands[1]),
            filterPracticeBrands(brands, selectedCategory = "新能源主流", query = ""),
        )
    }

    @Test
    fun `search is applied inside the selected category`() {
        assertEquals(
            listOf(brands[1]),
            filterPracticeBrands(brands, selectedCategory = "新能源主流", query = "tesla"),
        )
    }
}
