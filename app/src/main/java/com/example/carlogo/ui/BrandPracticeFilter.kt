package com.example.carlogo.ui

import com.example.carlogo.domain.model.Brand

internal const val ALL_BRANDS_FILTER_LABEL = "全部品牌"

internal fun filterPracticeBrands(
    brands: List<Brand>,
    selectedCategory: String?,
    query: String,
): List<Brand> = brands.filter { brand ->
    (selectedCategory == null || brand.category == selectedCategory) &&
        (brand.nameZh.contains(query, ignoreCase = true) || brand.nameEn.contains(query, ignoreCase = true))
}

internal object SpecialBrandFilterPresentation {
    const val FILTER_WEIGHT = 3f
    const val SEARCH_WEIGHT = 7f
    const val MENU_MIN_WIDTH_DP = 220
    const val BUTTON_LABEL = "筛选汽车品牌"
    const val CONTROL_HEIGHT_DP = 56
    const val SUBTITLE_LABEL = "选择品牌，针对练习"
    const val CURRENT_FILTER_PREFIX = "当前："
}
