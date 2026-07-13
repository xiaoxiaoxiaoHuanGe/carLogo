package com.example.carlogo.data

import com.example.carlogo.domain.model.Brand
import com.example.carlogo.domain.model.CarModel

/**
 * Offline built-in question bank, sourced from the user-provided brand/model list.
 * Categories are mutually exclusive so the brand-special dropdown stays short.
 */
object SeedData {
    const val MAINSTREAM_NEV = "新能源主流"
    const val EMERGING_NEV = "新能源新势力"
    const val LUXURY = "豪华品牌"
    const val INTERNATIONAL = "合资与国际品牌"
    const val DOMESTIC = "国产自主与高端"

    val categoryOrder = listOf(MAINSTREAM_NEV, EMERGING_NEV, LUXURY, INTERNATIONAL, DOMESTIC)

    val brands: List<Brand> = listOf(
        // 新能源主流（7）
        brand("tesla", "特斯拉", "Tesla", MAINSTREAM_NEV,
            "Model 3" to "Model 3", "Model Y" to "Model Y", "Model S" to "Model S", "Model X" to "Model X", "Cybertruck" to "Cybertruck", "Roadster（已发布，未量产）" to "Roadster"),
        brand("byd", "比亚迪", "BYD", MAINSTREAM_NEV,
            "秦 PLUS" to "Qin Plus", "汉" to "Han", "唐" to "Tang", "宋 PLUS" to "Song Plus", "海鸥" to "Seagull", "海豚" to "Dolphin", "海豹" to "Seal", "海狮 07" to "Sealion 07", "元 PLUS" to "Atto 3", "宋 Pro" to "Song Pro", "宋 L" to "Song L", "秦 L" to "Qin L", "驱逐舰 05" to "Destroyer 05", "海豹 06 DM-i" to "海豹 06 DM-i", "e2 / e3" to "e2 / e3", "腾势 D9（子品牌 Denza）" to "Denza D9", "腾势 N7" to "Denza N7"),
        brand("nio", "蔚来", "NIO", MAINSTREAM_NEV,
            "ET5" to "ET5", "ET7" to "ET7", "EC6" to "EC6", "ES6" to "ES6", "ES8" to "ES8", "ET5T（旅行版）" to "ET5T", "EC7" to "EC7", "ES7" to "ES7", "乐道 L60（子品牌 ONVO）" to "ONVO L60", "萤火虫（子品牌 Firefly）" to "Firefly"),
        brand("xpeng", "小鹏", "XPeng", MAINSTREAM_NEV,
            "P7" to "P7", "MONA M03" to "MONA M03", "G6" to "G6", "G9" to "G9", "X9" to "X9", "P5" to "P5", "G3i" to "G3i", "MONA M03 Max" to "MONA M03 Max"),
        brand("li-auto", "理想", "Li Auto", MAINSTREAM_NEV,
            "L6" to "L6", "L7" to "L7", "L8" to "L8", "L9" to "L9", "MEGA" to "MEGA", "理想 i6（纯电）" to "i6", "理想 i8（纯电）" to "i8"),
        brand("zeekr", "极氪", "Zeekr", MAINSTREAM_NEV,
            "极氪001" to "001", "极氪007" to "007", "极氪X" to "X", "极氪009" to "009", "极氪7X" to "7X"),
        brand("aion", "埃安", "Aion", MAINSTREAM_NEV,
            "AION S" to "AION S", "AION Y" to "AION Y", "AION V" to "AION V", "AION LX" to "AION LX", "昊铂 GT（Hyper GT）" to "Hyper GT"),

        // 新能源新势力（8）
        brand("im", "智己", "IM", EMERGING_NEV, "智己LS7" to "LS7", "智己LS6" to "LS6", "智己L7" to "L7", "智己L6" to "L6"),
        brand("leapmotor", "零跑", "Leapmotor", EMERGING_NEV, "C01" to "C01", "C11" to "C11", "C10" to "C10", "T03" to "T03", "B10" to "B10"),
        brand("neta", "哪吒", "Neta", EMERGING_NEV, "哪吒U" to "U", "哪吒V" to "V", "哪吒S" to "S", "哪吒GT" to "GT", "哪吒AYA" to "AYA"),
        brand("avatr", "阿维塔", "Avatr", EMERGING_NEV, "阿维塔11" to "11", "阿维塔12" to "12", "阿维塔07" to "07", "阿维塔06" to "06"),
        brand("deepal", "深蓝", "Deepal", EMERGING_NEV, "SL03" to "SL03", "S7" to "S7", "G318" to "G318", "L07" to "L07", "S05" to "S05"),
        brand("voyah", "岚图", "Voyah", EMERGING_NEV, "FREE" to "FREE", "梦想家" to "Dreamer", "追光" to "Passion", "知音" to "Courage"),
        brand("arcfox", "极狐", "Arcfox", EMERGING_NEV, "阿尔法 T" to "Alpha T", "阿尔法 S" to "Alpha S", "考拉" to "Koala", "T1" to "T1"),
        brand("luxeed", "智界", "Luxeed", EMERGING_NEV, "智界S7" to "S7", "智界R7" to "R7"),

        // 豪华品牌（9）
        brand("bmw", "宝马", "BMW", LUXURY,
            "3 系" to "3 Series", "5 系" to "5 Series", "X3" to "X3", "X5" to "X5", "i3" to "i3", "1系" to "1 Series", "2系" to "2 Series", "4系" to "4 Series", "7系" to "7 Series", "X1" to "X1", "X2" to "X2", "X4" to "X4", "X6" to "X6", "X7" to "X7", "i4（纯电）" to "i4", "iX（纯电）" to "iX", "iX3（纯电）" to "iX3", "Z4" to "Z4", "M3 / M4" to "M3 / M4"),
        brand("mercedes", "奔驰", "Mercedes-Benz", LUXURY, "C级" to "C-Class", "E级" to "E-Class", "S级" to "S-Class", "GLC" to "GLC", "GLE" to "GLE", "EQE（纯电）" to "EQE", "EQS（纯电）" to "EQS"),
        brand("audi", "奥迪", "Audi", LUXURY, "A4L" to "A4L", "A6L" to "A6L", "Q5L" to "Q5L", "Q3" to "Q3", "e-tron（纯电）" to "e-tron", "Q4 e-tron（纯电）" to "Q4 e-tron"),
        brand("lexus", "雷克萨斯", "Lexus", LUXURY, "ES" to "ES", "RX" to "RX", "NX" to "NX", "LS" to "LS", "UX" to "UX", "RZ（纯电）" to "RZ"),
        brand("volvo", "沃尔沃", "Volvo", LUXURY, "S60" to "S60", "S90" to "S90", "XC60" to "XC60", "XC90" to "XC90", "XC40" to "XC40", "EX30（纯电）" to "EX30"),
        brand("porsche", "保时捷", "Porsche", LUXURY, "911" to "911", "卡宴" to "Cayenne", "Macan" to "Macan", "Panamera（帕纳梅拉）" to "Panamera", "Taycan（纯电）" to "Taycan"),
        brand("cadillac", "凯迪拉克", "Cadillac", LUXURY, "CT5" to "CT5", "CT4" to "CT4", "XT5" to "XT5", "XT6" to "XT6", "LYRIQ 锐歌（纯电）" to "LYRIQ"),
        brand("land-rover", "路虎", "Land Rover", LUXURY, "揽胜" to "Range Rover", "揽胜运动版" to "Range Rover Sport", "发现" to "Discovery", "卫士" to "Defender", "极光" to "Range Rover Evoque"),
        brand("hongqi", "红旗", "Hongqi", LUXURY, "H5" to "H5", "H9" to "H9", "HS5" to "HS5", "HQ9" to "HQ9", "E-HS9（纯电）" to "E-HS9"),

        // 合资与国际品牌（6）
        brand("toyota", "丰田", "Toyota", INTERNATIONAL,
            "卡罗拉" to "Corolla", "凯美瑞" to "Camry", "RAV4 荣放" to "RAV4", "汉兰达" to "Highlander", "皇冠" to "Crown", "雷凌" to "Levin", "亚洲龙" to "Avalon", "威驰" to "Vios", "奕泽 / C-HR" to "IZOA", "普拉多" to "Prado", "兰德酷路泽" to "Land Cruiser", "格瑞维亚" to "Granvia", "赛那" to "Sienna", "bZ3（纯电）" to "bZ3", "bZ4X（纯电）" to "bZ4X"),
        brand("volkswagen", "大众", "Volkswagen", INTERNATIONAL,
            "朗逸" to "Lavida", "速腾" to "Sagitar", "帕萨特" to "Passat", "途观 L" to "Tiguan L", "ID.4" to "ID.4", "高尔夫" to "Golf", "Polo" to "Polo", "迈腾" to "Magotan", "宝来" to "Bora", "凌渡" to "Lamando", "途昂" to "Teramont", "途岳" to "Tharu", "探岳" to "Tayron", "探歌" to "T-Roc", "ID.3（纯电）" to "ID.3", "ID.6 X / ID.6 CROZZ（纯电）" to "ID.6", "ID.7（纯电）" to "ID.7"),
        brand("nissan", "日产", "Nissan", INTERNATIONAL, "轩逸" to "Sylphy", "天籁" to "Teana", "奇骏" to "X-Trail", "逍客" to "Qashqai", "途乐" to "Patrol", "骐达" to "Tiida", "蓝鸟" to "Lannia", "劲客" to "Kicks", "楼兰" to "Murano", "途达" to "Terra", "艾睿雅（Ariya，纯电）" to "Ariya"),
        brand("honda", "本田", "Honda", INTERNATIONAL, "思域" to "Civic", "雅阁" to "Accord", "CR-V" to "CR-V", "奥德赛" to "Odyssey", "飞度" to "Fit", "型格" to "Integra", "皓影" to "Breeze", "缤智" to "XR-V / HR-V", "冠道" to "Avancier", "e:NP1 极湃 1（纯电）" to "e:NP1", "e:NS1（纯电）" to "e:NS1"),
        brand("mazda", "马自达", "Mazda", INTERNATIONAL, "昂克赛拉" to "Mazda3", "阿特兹" to "Atenza", "CX-5" to "CX-5", "CX-30" to "CX-30", "CX-50" to "CX-50", "CX-4" to "CX-4", "CX-8" to "CX-8", "马自达6" to "Mazda6", "EZ-6（长安马自达，新能源）" to "EZ-6"),
        brand("mg", "名爵", "MG", INTERNATIONAL, "MG5" to "MG5", "MG6" to "MG6", "MG ONE" to "MG ONE", "MG4 EV（纯电）" to "MG4 EV", "MG ES5" to "MG ES5"),

        // 国产自主与高端（10）
        brand("geely", "吉利", "Geely", DOMESTIC, "帝豪" to "Emgrand", "博越 L" to "Boyue L", "星越 L" to "Xingyue L", "银河 L7" to "Galaxy L7", "熊猫 MINI" to "Panda Mini", "缤越" to "Coolray / Binyue", "星瑞" to "Preface / Xingrui", "ICON" to "ICON", "嘉际" to "Jiaji", "豪越" to "Haoyue / Okavango", "银河 E8（纯电）" to "Galaxy E8", "银河 L6" to "Galaxy L6", "领克 03" to "Lynk & Co 03", "领克 08 EM-P" to "Lynk & Co 08 EM-P"),
        brand("changan", "长安", "Changan", DOMESTIC, "逸动" to "Eado", "UNI-V" to "UNI-V", "UNI-K" to "UNI-K", "CS75 PLUS" to "CS75 PLUS", "深蓝SL03（新能源子品牌）" to "Deepal SL03"),
        brand("great-wall-haval", "长城 / 哈弗", "Great Wall / Haval", DOMESTIC, "哈弗H6" to "Haval H6", "哈弗大狗" to "Haval Dargo", "坦克300" to "Tank 300", "坦克500" to "Tank 500", "欧拉好猫" to "Ora Good Cat"),
        brand("chery", "奇瑞", "Chery", DOMESTIC, "瑞虎8" to "Tiggo 8", "瑞虎7" to "Tiggo 7", "艾瑞泽8" to "Arrizo 8", "星途揽月" to "Exeed VX", "捷途旅行者" to "Jetour Traveller"),
        brand("wuling", "五菱", "Wuling", DOMESTIC, "宏光MINIEV" to "Hongguang Mini EV", "缤果" to "Bingo", "星光" to "Starlight", "佳辰" to "Jiachen", "凯捷" to "Cortez"),
        brand("aito", "问界", "AITO", DOMESTIC, "问界 M5" to "M5", "问界 M7" to "M7", "问界新M7" to "New M7", "问界M9" to "M9", "问界新M5" to "New M5"),
        brand("stelato", "享界", "Stelato", DOMESTIC, "享界S9" to "S9"),
        brand("maextro", "尊界", "Maextro", DOMESTIC, "尊界S800" to "S800"),
        childBrand("yangwang", "仰望", "Yangwang", DOMESTIC, "比亚迪旗下", "仰望U8" to "U8", "仰望U9" to "U9", "仰望U7" to "U7"),
        childBrand("fangchengbao", "方程豹", "Fangchengbao", DOMESTIC, "比亚迪旗下", "豹5" to "Bao 5", "豹8" to "Bao 8"),
    )

    private fun brand(id: String, nameZh: String, nameEn: String, category: String, vararg models: Pair<String, String>) =
        buildBrand(id, nameZh, nameEn, category, null, models)

    private fun childBrand(id: String, nameZh: String, nameEn: String, category: String, parentBrand: String, vararg models: Pair<String, String>) =
        buildBrand(id, nameZh, nameEn, category, parentBrand, models)

    private fun buildBrand(
        id: String,
        nameZh: String,
        nameEn: String,
        category: String,
        parentBrand: String?,
        models: Array<out Pair<String, String>>,
    ) = Brand(
        id = id,
        nameZh = nameZh,
        nameEn = nameEn,
        category = category,
        cars = models.mapIndexed { index, (modelZh, modelEn) -> CarModel("$id-${index + 1}", id, modelZh, modelEn) },
        parentBrand = parentBrand,
    )
}
