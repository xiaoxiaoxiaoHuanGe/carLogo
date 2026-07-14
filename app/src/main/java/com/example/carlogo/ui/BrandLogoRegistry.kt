package com.example.carlogo.ui

import androidx.annotation.DrawableRes
import com.example.carlogo.R

object BrandLogoRegistry {
    @DrawableRes
    fun resourceIdFor(brandId: String): Int? = when (brandId) {
        "tesla" -> R.drawable.brand_tesla
        "byd" -> R.drawable.brand_byd
        "yangwang" -> R.drawable.brand_yangwang
        "fangchengbao" -> R.drawable.brand_fangchengbao
        "nio" -> R.drawable.brand_nio
        "xpeng" -> R.drawable.brand_xpeng
        "li-auto" -> R.drawable.brand_li_auto
        "zeekr" -> R.drawable.brand_zeekr
        "aion" -> R.drawable.brand_aion
        "im" -> R.drawable.brand_im
        "leapmotor" -> R.drawable.brand_leapmotor
        "neta" -> R.drawable.brand_neta
        "avatr" -> R.drawable.brand_avatr
        "deepal" -> R.drawable.brand_deepal
        "voyah" -> R.drawable.brand_voyah
        "arcfox" -> R.drawable.brand_arcfox
        "luxeed" -> R.drawable.brand_luxeed
        "bmw" -> R.drawable.brand_bmw
        "mercedes" -> R.drawable.brand_mercedes
        "audi" -> R.drawable.brand_audi
        "lexus" -> R.drawable.brand_lexus
        "volvo" -> R.drawable.brand_volvo
        "porsche" -> R.drawable.brand_porsche
        "cadillac" -> R.drawable.brand_cadillac
        "land-rover" -> R.drawable.brand_land_rover
        "hongqi" -> R.drawable.brand_hongqi
        "toyota" -> R.drawable.brand_toyota
        "volkswagen" -> R.drawable.brand_volkswagen
        "nissan" -> R.drawable.brand_nissan
        "honda" -> R.drawable.brand_honda
        "mazda" -> R.drawable.brand_mazda
        "mg" -> R.drawable.brand_mg
        "geely" -> R.drawable.brand_geely
        "changan" -> R.drawable.brand_changan
        "great-wall-haval" -> R.drawable.brand_great_wall_haval
        "chery" -> R.drawable.brand_chery
        "wuling" -> R.drawable.brand_wuling
        "aito" -> R.drawable.brand_aito
        "stelato" -> R.drawable.brand_stelato
        "maextro" -> R.drawable.brand_maextro
        else -> null
    }
}
