package com.example.carlogo.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.window.Dialog
import androidx.room.Room
import com.example.carlogo.R
import com.example.carlogo.data.CarRepository
import com.example.carlogo.data.SeedData
import com.example.carlogo.data.local.AppDatabase
import com.example.carlogo.data.local.MistakeEntity
import com.example.carlogo.data.local.QuizSessionEntity
import com.example.carlogo.domain.QuizGenerator
import com.example.carlogo.domain.QuizAnswerTransition
import com.example.carlogo.domain.QuizSessionController
import com.example.carlogo.domain.QuizSessionState
import com.example.carlogo.domain.model.Brand
import com.example.carlogo.domain.model.CarModel
import com.example.carlogo.domain.model.QuestionType
import com.example.carlogo.domain.model.QuizMode
import com.example.carlogo.domain.model.displayText
import com.example.carlogo.domain.model.letteredText
import com.example.carlogo.ui.theme.CardNavy
import com.example.carlogo.ui.theme.CardNavyLight
import com.example.carlogo.ui.theme.CyanGlow
import com.example.carlogo.ui.theme.DeepNavy
import com.example.carlogo.ui.theme.ElectricBlue
import com.example.carlogo.ui.theme.ErrorPink
import com.example.carlogo.ui.theme.MistWhite
import com.example.carlogo.ui.theme.NightSky
import com.example.carlogo.ui.theme.SoftBlue
import com.example.carlogo.ui.theme.SuccessMint
import java.io.File
import java.util.UUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val AppGradient = Brush.verticalGradient(listOf(NightSky, DeepNavy, NightSky))
private val CardGradient = Brush.linearGradient(listOf(CardNavyLight, CardNavy))
private val HeroGradient = Brush.linearGradient(listOf(ElectricBlue, Color(0xFF0750CC)))

@Composable
fun CarLogoApp() {
    val context = LocalContext.current
    val repository = remember {
        CarRepository(
            Room.databaseBuilder(context, AppDatabase::class.java, "car_logo.db")
                .addMigrations(AppDatabase.MIGRATION_1_2)
                .build(),
        )
    }
    var brands by remember { mutableStateOf<List<Brand>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var controller by remember { mutableStateOf<QuizSessionController?>(null) }
    var sessionState by remember { mutableStateOf<QuizSessionState?>(null) }
    var sessionId by remember { mutableStateOf<Long?>(null) }
    var page by remember { mutableStateOf("random") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        runCatching { repository.loadBrands() }
            .onSuccess { brands = it }
            .onFailure { errorMessage = "题库加载失败：${it.message ?: "未知错误"}" }
    }

    val startRound: (QuizMode) -> Unit = { mode ->
        scope.launch {
            runCatching {
                val questions = QuizGenerator().createRound(mode, brands)
                questions to repository.startSession(
                    mode.javaClass.simpleName,
                    (mode as? QuizMode.BrandPractice)?.brandId,
                    questions.size,
                )
            }.onSuccess { (questions, persistedId) ->
                controller = QuizSessionController(questions)
                sessionState = controller?.current()
                sessionId = persistedId
                errorMessage = null
            }.onFailure { errorMessage = it.message ?: "无法生成题目" }
        }
    }

    val activeState = sessionState
    when {
        activeState != null && !activeState.isFinished -> QuizScreen(
            state = activeState,
            onBack = { controller = null; sessionState = null; sessionId = null },
            onAnswer = { optionId ->
                val beforeAnswer = sessionState ?: return@QuizScreen
                val selected = beforeAnswer.currentQuestion.options.firstOrNull { it.id == optionId }
                sessionState = controller?.answer(optionId)
                if (selected?.isCorrect == false) scope.launch {
                    repository.recordMistake(beforeAnswer.currentQuestion.targetBrandId, beforeAnswer.currentQuestion.targetCarId, beforeAnswer.currentQuestion.type.name)
                }
            },
            onPrevious = { sessionState = controller?.previous() },
            onNext = {
                val next = controller?.next()
                sessionState = next
                if (next?.isFinished == true && sessionId != null) scope.launch {
                    repository.finishSession(sessionId!!, next.correctCount)
                }
            },
        )
        activeState?.isFinished == true -> ResultScreen(activeState) {
            controller = null; sessionState = null; sessionId = null
        }
        else -> MainShell(
            page = page,
            brands = brands,
            error = errorMessage,
            onStart = startRound,
            repository = repository,
            onBrandsChanged = { brands = repository.loadBrands() },
            onOpenPage = { page = it },
        )
    }
}

@Composable
private fun MainShell(
    page: String,
    brands: List<Brand>,
    error: String?,
    onStart: (QuizMode) -> Unit,
    repository: CarRepository,
    onBrandsChanged: suspend () -> Unit,
    onOpenPage: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var managingBrandId by remember { mutableStateOf<String?>(null) }
    val selectedTab = if (page in listOf("random", "special", "settings")) page else "settings"
    Box(Modifier.fillMaxSize().background(AppGradient)) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(containerColor = DeepNavy.copy(alpha = 0.96f)) {
                    TechNavItem("练习", "◉", selectedTab == "random") { onOpenPage("random") }
                    TechNavItem("专项", "◇", selectedTab == "special") { onOpenPage("special") }
                    TechNavItem("我的", "◎", selectedTab == "settings") { onOpenPage("settings") }
                }
            },
        ) { insets ->
            when (page) {
                "random" -> RandomPracticePage(brands.isNotEmpty(), error, { onStart(QuizMode.Random) }, onOpenPage, insets)
                "special" -> BrandPracticePage(brands, onStart, insets)
                "history" -> RecordsScreen("学习记录", insets, { onOpenPage("settings") }) { repository.history() }
                "mistakes" -> MistakesScreen(insets, { onOpenPage("settings") }) { repository.mistakes() }
                "manage" -> ManagementScreen(
                    brands = brands,
                    insets = insets,
                    onBack = { onOpenPage("settings") },
                    onOpenBrandDetail = { brandId -> managingBrandId = brandId; onOpenPage("manageDetail") },
                    onAddBrand = { zh, en -> scope.launch { repository.addBrand(zh, en, "自定义"); onBrandsChanged() } },
                    onDeleteBrand = { id -> scope.launch { repository.deleteCustomBrand(id); onBrandsChanged() } },
                    onUpdateBrand = { id, zh, en -> scope.launch { repository.updateCustomBrand(id, zh, en, "自定义"); onBrandsChanged() } },
                )
                "manageDetail" -> {
                    val selectedBrand = brands.firstOrNull { it.id == managingBrandId }
                    if (selectedBrand != null) {
                        BrandDetailManagementScreen(
                            brand = selectedBrand,
                            insets = insets,
                            onBack = { onOpenPage("manage") },
                            onAddCar = { brandId, zh, en, image -> scope.launch { repository.addCar(brandId, zh, en, image); onBrandsChanged() } },
                            onDeleteCar = { id -> scope.launch { repository.deleteCustomCar(id); onBrandsChanged() } },
                            onUpdateCar = { id, brandId, zh, en, image -> scope.launch { repository.updateCustomCar(id, brandId, zh, en, image); onBrandsChanged() } },
                        )
                    } else {
                        TechListPage("题库管理", insets, { onOpenPage("manage") }) {
                            item { EmptyCard("未找到该品牌，请返回品牌列表重新选择。") }
                        }
                    }
                }
                else -> SettingsPage(insets, onOpenPage)
            }
        }
    }
}

@Composable
private fun RowScope.TechNavItem(label: String, symbol: String, selected: Boolean, onClick: () -> Unit) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Text(symbol, style = MaterialTheme.typography.headlineSmall, color = if (selected) MistWhite else SoftBlue) },
        label = { Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
            selectedIconColor = MistWhite,
            selectedTextColor = CyanGlow,
            indicatorColor = ElectricBlue,
            unselectedIconColor = SoftBlue,
            unselectedTextColor = SoftBlue,
        ),
    )
}

@Composable
private fun RandomPracticePage(enabled: Boolean, error: String?, onStart: () -> Unit, onOpenPage: (String) -> Unit, insets: PaddingValues) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(insets).padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 28.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Column {
                Text("今日练习", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = MistWhite)
                Box(Modifier.padding(top = 12.dp).width(78.dp).height(5.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp)).background(ElectricBlue))
            }
        }
        item { LearningProgressCard() }
        item {
            TechCard {
                Box {
                    Image(
                        painter = painterResource(R.drawable.hero_car_blue),
                        contentDescription = "蓝色未来汽车主视觉",
                        modifier = Modifier.fillMaxWidth().height(230.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(22.dp)),
                        contentScale = ContentScale.Crop,
                    )
                    Column(Modifier.padding(24.dp)) {
                        Text("随机练习", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = MistWhite)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("20", style = MaterialTheme.typography.displayLarge, color = CyanGlow, fontWeight = FontWeight.Black)
                            Text(" 题", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MistWhite, modifier = Modifier.padding(bottom = 12.dp))
                        }
                    }
                    TechButton("开始练习", onStart, enabled, Modifier.align(Alignment.BottomCenter).padding(20.dp).fillMaxWidth())
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                ShortcutCard("品牌专项", "▱", Modifier.weight(1f)) { onOpenPage("special") }
                ShortcutCard("错题复盘", "▧", Modifier.weight(1f)) { onOpenPage("mistakes") }
            }
        }
        if (error != null) item { Text(error, color = ErrorPink) }
    }
}

@Composable
private fun LearningProgressCard() {
    TechCard {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row { Text("连续学习 ", style = MaterialTheme.typography.titleLarge); Text("7", style = MaterialTheme.typography.headlineLarge, color = ElectricBlue, fontWeight = FontWeight.Black); Text(" 天", style = MaterialTheme.typography.titleLarge) }
                Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    repeat(7) { index -> Text(if (index < 6) "●" else "○", color = if (index < 6) ElectricBlue else SoftBlue, style = MaterialTheme.typography.titleMedium) }
                }
            }
            Box(Modifier.size(82.dp).border(7.dp, CyanGlow, androidx.compose.foundation.shape.CircleShape), contentAlignment = Alignment.Center) {
                Text("68%", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun ShortcutCard(title: String, symbol: String, modifier: Modifier, onClick: () -> Unit) {
    TechCard(modifier.clickable(onClick = onClick)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MistWhite)
            Box(Modifier.fillMaxWidth().height(72.dp), contentAlignment = Alignment.Center) {
                Text(symbol, color = ElectricBlue, style = MaterialTheme.typography.displayMedium)
            }
        }
    }
}

@Composable
private fun BrandPracticePage(brands: List<Brand>, onStart: (QuizMode) -> Unit, insets: PaddingValues) {
    var query by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = (SeedData.categoryOrder + brands.map { it.category }.distinct().filter { it !in SeedData.categoryOrder })
        .filter { category -> brands.any { it.category == category } }
    var selectedCategory by remember { mutableStateOf(SeedData.categoryOrder.first()) }
    val filtered = brands.filter {
        it.category == selectedCategory && (it.nameZh.contains(query, true) || it.nameEn.contains(query, true))
    }
    Column(Modifier.fillMaxSize().padding(insets).padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(28.dp))
        Text("品牌专项", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = MistWhite)
        Text("选择品牌，开始针对练习", color = SoftBlue, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp, bottom = 18.dp))
        Box(Modifier.fillMaxWidth()) {
            Button(
                onClick = { categoryExpanded = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CardNavyLight, contentColor = MistWhite),
            ) {
                Text("$selectedCategory  ·  选择分类")
            }
            DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = { selectedCategory = category; categoryExpanded = false },
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("搜索品牌") },
            leadingIcon = { Text("⌕", style = MaterialTheme.typography.headlineSmall) },
        )
        Spacer(Modifier.height(16.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(filtered) { brand -> BrandCard(brand) { onStart(QuizMode.BrandPractice(brand.id)) } }
        }
    }
}

@Composable
private fun BrandCard(brand: Brand, onClick: () -> Unit) {
    TechCard(Modifier.clickable(onClick = onClick)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BrandMark(brand, Modifier.size(72.dp))
            Text(brand.displayText(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MistWhite, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("${brand.cars.size} 款车型 · ${brand.category}", color = SoftBlue, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun BrandMark(brand: Brand, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.clip(androidx.compose.foundation.shape.CircleShape).background(HeroGradient).border(1.dp, CyanGlow.copy(alpha = 0.8f), androidx.compose.foundation.shape.CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(brand.nameEn.take(2).uppercase(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MistWhite)
    }
}

@Composable
private fun SettingsPage(insets: PaddingValues, onOpenPage: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(insets).padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 28.dp, bottom = 22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { Text("我的学习", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = MistWhite) }
        item {
            TechCard {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(56.dp).clip(androidx.compose.foundation.shape.CircleShape).background(HeroGradient), contentAlignment = Alignment.Center) { Text("车", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black) }
                    Spacer(Modifier.width(14.dp))
                    Column { Text("汽车品牌学习者", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MistWhite); Text("继续完成今天的 20 题挑战", color = SoftBlue) }
                }
            }
        }
        item { SettingsEntry("学习记录", "查看每次练习结果", "▤") { onOpenPage("history") } }
        item { SettingsEntry("错题本", "复习易错车型和品牌", "◫") { onOpenPage("mistakes") } }
        item { SettingsEntry("题库管理", "新增或编辑自定义内容", "✦") { onOpenPage("manage") } }
    }
}

@Composable
private fun SettingsEntry(title: String, subtitle: String, symbol: String, onClick: () -> Unit) {
    TechCard(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(48.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(15.dp)).background(CardNavyLight), contentAlignment = Alignment.Center) { Text(symbol, color = CyanGlow, style = MaterialTheme.typography.titleLarge) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) { Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MistWhite); Text(subtitle, color = SoftBlue, style = MaterialTheme.typography.bodySmall) }
            Text("›", color = SoftBlue, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
private fun RecordsScreen(title: String, insets: PaddingValues, onBack: () -> Unit, load: suspend () -> List<QuizSessionEntity>) {
    var records by remember { mutableStateOf<List<QuizSessionEntity>>(emptyList()) }
    LaunchedEffect(Unit) { records = load() }
    TechListPage(title, insets, onBack) {
        if (records.isEmpty()) item { EmptyCard("还没有完成的练习记录。") }
        items(records.size) { index ->
            val record = records[index]
            TechCard { Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) { Text("${record.correctCount}/${record.totalCount}", color = CyanGlow, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black); Spacer(Modifier.width(14.dp)); Column { Text("练习成绩", fontWeight = FontWeight.Bold); Text(record.mode, color = SoftBlue, style = MaterialTheme.typography.bodySmall) } } }
        }
    }
}

@Composable
private fun MistakesScreen(insets: PaddingValues, onBack: () -> Unit, load: suspend () -> List<MistakeEntity>) {
    var records by remember { mutableStateOf<List<MistakeEntity>>(emptyList()) }
    LaunchedEffect(Unit) { records = load() }
    TechListPage("错题本", insets, onBack) {
        if (records.isEmpty()) item { EmptyCard("暂时没有错题，继续保持！") }
        items(records.size) { index ->
            val item = records[index]
            TechCard { Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) { Text("!", color = ErrorPink, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black); Spacer(Modifier.width(14.dp)); Column { Text(item.carId, fontWeight = FontWeight.Bold); Text("${item.questionType} · 错误 ${item.wrongCount} 次", color = SoftBlue, style = MaterialTheme.typography.bodySmall) } } }
        }
    }
}

@Composable
private fun TechListPage(title: String, insets: PaddingValues, onBack: () -> Unit, content: androidx.compose.foundation.lazy.LazyListScope.() -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(insets).padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 22.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { BackButton(onBack) }
        item { Text(title, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = MistWhite) }
        content()
    }
}

@Composable
private fun EmptyCard(message: String) {
    TechCard { Text(message, modifier = Modifier.padding(24.dp), color = SoftBlue, textAlign = TextAlign.Center) }
}

@Composable
private fun ManagementScreen(
    brands: List<Brand>,
    insets: PaddingValues,
    onBack: () -> Unit,
    onOpenBrandDetail: (String) -> Unit,
    onAddBrand: (String, String) -> Unit,
    onDeleteBrand: (String) -> Unit,
    onUpdateBrand: (String, String, String) -> Unit,
) {
    var brandZh by remember { mutableStateOf("") }
    var brandEn by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }
    var editingBrandId by remember { mutableStateOf<String?>(null) }
    val filteredBrands = brands.filter { it.nameZh.contains(query, true) || it.nameEn.contains(query, true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(insets).padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 20.dp, bottom = 22.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { BackButton(onBack) }
        item { Text("题库管理", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = MistWhite) }
        item { Text("选择一个品牌，进入独立的车型管理页面", color = SoftBlue) }
        item {
            TechCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("品牌信息", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MistWhite)
                    OutlinedTextField(brandZh, { brandZh = it }, label = { Text("品牌中文名") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(brandEn, { brandEn = it }, label = { Text("品牌英文名") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    TechButton(if (editingBrandId == null) "新增品牌" else "保存品牌修改", {
                        if (editingBrandId == null) onAddBrand(brandZh, brandEn) else onUpdateBrand(editingBrandId!!, brandZh, brandEn)
                        brandZh = ""; brandEn = ""; editingBrandId = null
                    }, brandZh.isNotBlank(), Modifier.fillMaxWidth())
                }
            }
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("搜索品牌") },
                leadingIcon = { Text("⌕", style = MaterialTheme.typography.headlineSmall) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }
        item { Text("品牌列表（${filteredBrands.size}）", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MistWhite) }
        items(filteredBrands.size) { index ->
            val brand = filteredBrands[index]
            TechCard(Modifier.fillMaxWidth().clickable { onOpenBrandDetail(brand.id) }) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    BrandMark(brand, Modifier.size(42.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(brand.displayText(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MistWhite)
                        Text("${brand.cars.size} 款车型 · 点击管理", color = SoftBlue, style = MaterialTheme.typography.bodySmall)
                    }
                    if (!brand.isBuiltIn) {
                        Text("编辑", color = CyanGlow, modifier = Modifier.padding(horizontal = 8.dp).clickable { editingBrandId = brand.id; brandZh = brand.nameZh; brandEn = brand.nameEn })
                        Text("删除", color = ErrorPink, modifier = Modifier.clickable { onDeleteBrand(brand.id) })
                    } else {
                        Text("›", color = SoftBlue, style = MaterialTheme.typography.headlineMedium)
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandDetailManagementScreen(
    brand: Brand,
    insets: PaddingValues,
    onBack: () -> Unit,
    onAddCar: (String, String, String, String?) -> Unit,
    onDeleteCar: (String) -> Unit,
    onUpdateCar: (String, String, String, String, String?) -> Unit,
) {
    var showEditor by remember { mutableStateOf(false) }
    var editingCar by remember { mutableStateOf<CarModel?>(null) }
    var carZh by remember { mutableStateOf("") }
    var carEn by remember { mutableStateOf("") }
    var imageRef by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageRef = uri?.let { copyImageToPrivateStorage(context, it.toString()) }
    }
    fun openEditor(car: CarModel? = null) {
        editingCar = car
        carZh = car?.nameZh.orEmpty()
        carEn = car?.nameEn.orEmpty()
        imageRef = car?.imageRef
        showEditor = true
    }

    Column(Modifier.fillMaxSize().padding(insets).padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackButton(onBack)
            Spacer(Modifier.width(12.dp))
            BrandMark(brand, Modifier.size(42.dp))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(brand.displayText(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MistWhite)
                Text("${brand.cars.size} 款车型", color = SoftBlue, style = MaterialTheme.typography.bodySmall)
            }
        }
        Spacer(Modifier.height(14.dp))
        TechButton("新增车型", { openEditor() }, true, Modifier.fillMaxWidth())
        Spacer(Modifier.height(14.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 22.dp)) {
            item { Text("车型列表", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MistWhite) }
            if (brand.cars.isEmpty()) item { EmptyCard("该品牌还没有车型，点击上方“新增车型”开始添加。") }
            items(brand.cars.size) { index ->
                val car = brand.cars[index]
                TechCard {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(13.dp)).background(CardNavyLight), contentAlignment = Alignment.Center) { Text("车", color = CyanGlow, fontWeight = FontWeight.Black) }
                        Spacer(Modifier.width(12.dp))
                        Text(car.displayText(), modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MistWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        if (!car.isBuiltIn) {
                            Text("编辑", color = CyanGlow, modifier = Modifier.padding(horizontal = 8.dp).clickable { openEditor(car) })
                            Text("删除", color = ErrorPink, modifier = Modifier.clickable { onDeleteCar(car.id) })
                        }
                    }
                }
            }
        }
    }

    if (showEditor) {
        Dialog(onDismissRequest = { showEditor = false }) {
            TechCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(if (editingCar == null) "新增车型" else "编辑车型", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = MistWhite)
                    Text(brand.displayText(), color = SoftBlue)
                    OutlinedTextField(carZh, { carZh = it }, label = { Text("车型中文名") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    OutlinedTextField(carEn, { carEn = it }, label = { Text("车型英文名") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    TechButton(if (imageRef == null) "从相册选择车型图片" else "已选择车型图片", { picker.launch("image/*") }, true, Modifier.fillMaxWidth())
                    TechButton(if (editingCar == null) "保存车型" else "保存修改", {
                        if (editingCar == null) onAddCar(brand.id, carZh, carEn, imageRef)
                        else onUpdateCar(editingCar!!.id, brand.id, carZh, carEn, imageRef)
                        showEditor = false
                    }, carZh.isNotBlank(), Modifier.fillMaxWidth())
                    Text("取消", color = SoftBlue, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().clickable { showEditor = false }.padding(8.dp))
                }
            }
        }
    }
}

private fun copyImageToPrivateStorage(context: android.content.Context, uri: String): String? = runCatching {
    val targetDir = File(context.filesDir, "car_images").apply { mkdirs() }
    val target = File(targetDir, "${UUID.randomUUID()}.jpg")
    context.contentResolver.openInputStream(android.net.Uri.parse(uri))?.use { input -> target.outputStream().use(input::copyTo) }
    target.absolutePath
}.getOrNull()

@Composable
private fun QuizScreen(
    state: QuizSessionState,
    onBack: () -> Unit,
    onAnswer: (String) -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    val question = state.currentQuestion
    val selectedOptionIsCorrect = question.options.firstOrNull { it.id == state.selectedOptionId }?.isCorrect == true
    val totalQuestions = state.questions.size
    val correctRate = if (state.answeredCount == 0) 0 else state.correctCount * 100 / state.answeredCount
    val edgeWidthPx = with(LocalDensity.current) { 48.dp.toPx() }
    val swipeDistancePx = with(LocalDensity.current) { 160.dp.toPx() }
    BackHandler(onBack = onBack)
    LaunchedEffect(state.currentIndex, state.selectedOptionId, state.isFreshAnswer) {
        if (QuizAnswerTransition.shouldAutoAdvance(state.isAnswered, selectedOptionIsCorrect, state.isFinished, state.isFreshAnswer)) {
            delay(QuizAnswerTransition.CorrectAnswerDelayMillis)
            onNext()
        }
    }
    var dragDistance = 0f
    var dragStartX = Float.MAX_VALUE
    Box(Modifier.fillMaxSize().background(AppGradient)) {
        AnimatedContent(
            targetState = state.currentIndex,
            transitionSpec = {
                (fadeIn(tween(180)) + slideInHorizontally(tween(220)) { it / 10 }) togetherWith
                    (fadeOut(tween(120)) + slideOutHorizontally(tween(160)) { -it / 12 })
            },
            label = "quiz-question-transition",
        ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().pointerInput(edgeWidthPx, swipeDistancePx) {
                detectHorizontalDragGestures(
                    onDragStart = { offset -> dragDistance = 0f; dragStartX = offset.x },
                    onHorizontalDrag = { _, amount -> dragDistance += amount },
                    onDragEnd = {
                        when {
                            dragDistance < -swipeDistancePx && state.canGoPrevious -> onPrevious()
                            dragDistance > swipeDistancePx && state.canGoNext -> onNext()
                            dragStartX <= edgeWidthPx && dragDistance > swipeDistancePx -> onBack()
                        }
                    },
                )
            }.padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item { BackButton(onBack) }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProgressTrack((state.currentIndex + 1) / totalQuestions.toFloat())
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("第 ${"%02d".format(state.currentIndex + 1)} / $totalQuestions 题", style = MaterialTheme.typography.titleLarge, color = MistWhite)
                        Text("正确率 ${correctRate}%", style = MaterialTheme.typography.titleLarge, color = CyanGlow, fontWeight = FontWeight.Bold)
                    }
                }
            }
            item {
                Text(if (question.type == QuestionType.BrandToModel) "车型识别" else "品牌识别", color = MistWhite, modifier = Modifier.clip(androidx.compose.foundation.shape.RoundedCornerShape(30.dp)).background(CardNavyLight).border(1.dp, ElectricBlue, androidx.compose.foundation.shape.RoundedCornerShape(30.dp)).padding(horizontal = 16.dp, vertical = 7.dp), fontWeight = FontWeight.Bold)
            }
            item { QuizPrompt(question.prompt) }
            items(question.options.size) { index -> QuizOptionCard(question.options[index].letteredText(index), state.selectedOptionId == question.options[index].id, question.options[index].isCorrect, state.isAnswered, state.canSelect) { onAnswer(question.options[index].id) } }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (state.hasPreviousQuestion) {
                        TechButton("上一题", onPrevious, state.canGoPrevious, Modifier.weight(1f))
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                    if (state.hasNextQuestion) {
                        TechButton("下一题", onNext, state.canGoNext, Modifier.weight(1f))
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun QuizPrompt(prompt: String) {
    val baseStyle = MaterialTheme.typography.headlineLarge
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val maxWidthPx = with(density) { maxWidth.roundToPx() }
        // Measure with the unscaled current typography; the resulting line count is the sole trigger.
        val measuredLineCount = remember(prompt, baseStyle, maxWidthPx) {
            textMeasurer.measure(
                text = AnnotatedString(prompt),
                style = baseStyle,
                constraints = Constraints(maxWidth = maxWidthPx),
            ).lineCount
        }
        val scale = QuizPromptLayout.fontScaleForMeasuredLineCount(measuredLineCount)
        val promptStyle = if (scale == 1f) baseStyle else baseStyle.copy(fontSize = baseStyle.fontSize * scale)
        // Three fixed line-heights accommodate a compact three-line prompt without moving the options.
        val containerHeight = with(density) { baseStyle.lineHeight.toDp() * 3 }

        Box(Modifier.fillMaxWidth().height(containerHeight)) {
            Text(
                text = prompt,
                style = promptStyle,
                fontWeight = FontWeight.Black,
                color = MistWhite,
                maxLines = if (scale == 1f) 2 else 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ProgressTrack(progress: Float) {
    Box(Modifier.fillMaxWidth().height(8.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp)).background(CardNavyLight)) {
        Box(Modifier.fillMaxWidth(progress).height(8.dp).background(HeroGradient))
    }
}

@Composable
private fun QuizOptionCard(label: String, selected: Boolean, correct: Boolean, answered: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val borderColor = when { answered && correct -> SuccessMint; answered && selected -> ErrorPink; selected -> CyanGlow; else -> SoftBlue.copy(alpha = 0.35f) }
    val fill = when { answered && correct -> Color(0xFF123F54); answered && selected -> Color(0xFF4B1E36); selected -> CardNavyLight; else -> CardNavy }
    Card(
        modifier = Modifier.fillMaxWidth().border(2.dp, borderColor, androidx.compose.foundation.shape.RoundedCornerShape(20.dp)).clickable(enabled = enabled, onClick = onClick),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = fill),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            val letter = label.substringBefore(".")
            Box(Modifier.size(46.dp).clip(androidx.compose.foundation.shape.CircleShape).background(if (selected || (answered && correct)) ElectricBlue else CardNavyLight), contentAlignment = Alignment.Center) { Text(letter, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MistWhite) }
            Text(label.substringAfter(". "), modifier = Modifier.weight(1f).padding(start = 14.dp), style = MaterialTheme.typography.titleLarge, color = MistWhite, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (answered && correct) Text("✓", color = SuccessMint, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            if (answered && selected && !correct) Text("×", color = ErrorPink, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun ResultScreen(state: QuizSessionState, onRestart: () -> Unit) {
    val rate = state.correctCount * 100 / state.questions.size
    Box(Modifier.fillMaxSize().background(AppGradient), contentAlignment = Alignment.Center) {
        TechCard(Modifier.fillMaxWidth().padding(24.dp)) {
            Column(Modifier.padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Text("✦", style = MaterialTheme.typography.displayLarge, color = CyanGlow)
                Text("本局完成", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black, color = MistWhite)
                Box(Modifier.size(128.dp).border(9.dp, CyanGlow, androidx.compose.foundation.shape.CircleShape), contentAlignment = Alignment.Center) { Text("$rate%", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = MistWhite) }
                Text("答对 ${state.correctCount}/${state.questions.size} 题", style = MaterialTheme.typography.titleLarge, color = MistWhite)
                TechButton("返回首页", onRestart, true, Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun BackButton(onBack: () -> Unit) {
    Text("‹", modifier = Modifier.size(46.dp).clip(androidx.compose.foundation.shape.CircleShape).background(CardNavy.copy(alpha = 0.85f)).clickable(onClick = onBack).padding(bottom = 5.dp), textAlign = TextAlign.Center, style = MaterialTheme.typography.displaySmall, color = MistWhite)
}

@Composable
private fun TechButton(text: String, onClick: () -> Unit, enabled: Boolean, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp).shadow(10.dp, androidx.compose.foundation.shape.RoundedCornerShape(28.dp)),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = MistWhite, disabledContainerColor = CardNavyLight, disabledContentColor = SoftBlue),
    ) { Text(text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
}

@Composable
private fun TechCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        modifier = modifier.border(1.dp, SoftBlue.copy(alpha = 0.38f), androidx.compose.foundation.shape.RoundedCornerShape(22.dp)),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy, contentColor = MistWhite),
    ) { Box(Modifier.background(CardGradient)) { content() } }
}
