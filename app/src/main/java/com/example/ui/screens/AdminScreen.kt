package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.PersianNumberHelper
import com.example.ui.theme.DarkCharcoalBg
import com.example.ui.theme.DarkCharcoalSurface
import com.example.ui.theme.GoldenWheatAccent
import com.example.ui.theme.JadeGreenPrimary
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusHealthy
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.LivestockViewModel
import com.example.ui.viewmodel.ScreenDestination
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: LivestockViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(ScreenDestination.Marketplace)
    }

    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var activeAdminMenu by remember { mutableIntStateOf(0) } // 0: Dashboard, 1: Marketplace, 2: Pricing, 3: Vet, 4: Media, 5: Analytics

    // Dynamic Live Price per KG state
    var sheepPricePerKg by remember { mutableFloatStateOf(360_000f) }
    var goatPricePerKg by remember { mutableFloatStateOf(320_000f) }

    // Modern glass card background
    val cardBg = if (isDarkMode) Color(0xFF131D2D).copy(alpha = 0.95f) else Color.White
    val cardBorder = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0)
    val pageBg = if (isDarkMode) Color(0xFF0A0F1D) else Color(0xFFF1F5F9)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = if (isDarkMode) Color(0xFF0F172A) else Color.White
            ) {
                AdminSidebarContent(
                    isDarkMode = isDarkMode,
                    activeMenu = activeAdminMenu,
                    onMenuSelect = {
                        activeAdminMenu = it
                        scope.launch { drawerState.close() }
                        if (it == 1) {
                            viewModel.navigateTo(ScreenDestination.Marketplace)
                        }
                    },
                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                    onBackToMarket = { viewModel.navigateTo(ScreenDestination.Marketplace) }
                )
            }
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(pageBg)
                .statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp)
            ) {
                // Top Header (Matching Attachment)
                AdminHeaderBar(
                    isDarkMode = isDarkMode,
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onBackToApp = { viewModel.navigateTo(ScreenDestination.Marketplace) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Navigation Tabs Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val navItems = listOf(
                        "داشبورد جامع" to Icons.Default.Dashboard,
                        "قیمت‌گذاری پویا" to Icons.Default.Payments,
                        "آپلودر رسانه" to Icons.Default.PermMedia,
                        "گواهی‌های سلامت" to Icons.Default.LocalHospital,
                        "تحلیل فروش" to Icons.Default.BarChart
                    )
                    navItems.forEachIndexed { index, (label, icon) ->
                        val isSelected = activeAdminMenu == index
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) JadeGreenPrimary else cardBg
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) JadeGreenPrimary else cardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { activeAdminMenu = index }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Main 2x2 Dashboard Cards (Responsive Column / Layout)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // CARD 1: DYNAMIC LIVE WEIGHT PRICE PER KG (موتور قیمت‌گذاری پویا)
                    DynamicPricingCard(
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        sheepPrice = sheepPricePerKg,
                        goatPrice = goatPricePerKg,
                        onSheepPriceChange = { sheepPricePerKg = it },
                        onGoatPriceChange = { goatPricePerKg = it },
                        onUpdatePricesLive = {
                            viewModel.allLivestockList.value.firstOrNull()?.let {
                                viewModel.toastMessage.value = "نرخ پایه گوسفند (${PersianNumberHelper.formatToman(sheepPricePerKg.toLong())}) و بز (${PersianNumberHelper.formatToman(goatPricePerKg.toLong())}) سراسری بروزرسانی شد"
                            }
                        }
                    )

                    // CARD 2: GALLERY UPLOADER (SHEEP & GOATS) (آپلودر و گالری چندرسانه‌ای)
                    GalleryUploaderCard(
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        onAddMediaClick = {
                            viewModel.toastMessage.value = "فایل چندرسانه‌ای جدید بارگذاری و واترمارک اختصاصی درج شد"
                        }
                    )

                    // CARD 3: VETERINARY CERTIFICATE STATUS (وضعیت گواهی‌های سلامت دامپزشکی)
                    VeterinaryCertificateStatusCard(
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        onDownloadCertificate = { certId ->
                            viewModel.toastMessage.value = "گواهی رسمی دامپزشکی #$certId با موفقیت بارگیری شد"
                        }
                    )

                    // CARD 4: REAL-TIME SALES ANALYTICS (تحلیل و نمودار زنده فروش)
                    SalesAnalyticsChartCard(
                        cardBg = cardBg,
                        cardBorder = cardBorder,
                        isDarkMode = isDarkMode
                    )
                }
            }
        }
    }
}

// ---------------- HEADER BAR ----------------
@Composable
fun AdminHeaderBar(
    isDarkMode: Boolean,
    onOpenDrawer: () -> Unit,
    onBackToApp: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) Color(0xFF131D2D) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Right side: Brand & Drawer Hamburger
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onOpenDrawer) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "منوی ادمین",
                        tint = JadeGreenPrimary
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(JadeGreenPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "ببعی من (LIVESTOCK HUB)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "پنل مدیریت پیشرفته و قیمت‌گذاری روز",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            // Left side: Admin Profile & Back Button
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Notifications with indicator
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "اعلان‌ها",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(StatusDanger)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Profile Avatar & Title
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(JadeGreenPrimary.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(JadeGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("A", color = Color.White, style = MaterialTheme.typography.labelSmall)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "مدیر ارشد",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "امروز",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onBackToApp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت به بازارگاه",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ---------------- SIDEBAR CONTENT ----------------
@Composable
fun AdminSidebarContent(
    isDarkMode: Boolean,
    activeMenu: Int,
    onMenuSelect: (Int) -> Unit,
    onToggleDarkMode: () -> Unit,
    onBackToMarket: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(JadeGreenPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_app_logo),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "LIVESTOCK HUB",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Marketplace Admin",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        val menuItems = listOf(
            Triple(0, "داشبورد اصلی (Dashboard)", Icons.Default.Dashboard),
            Triple(1, "بازارگاه دام سبک (Marketplace)", Icons.Default.Storefront),
            Triple(2, "قیمت‌گذاری پویا (Pricing)", Icons.Default.Payments),
            Triple(3, "گواهی سلامت (Veterinary)", Icons.Default.LocalHospital),
            Triple(4, "آپلودر رسانه (Media Uploader)", Icons.Default.PermMedia),
            Triple(5, "تحلیل و آمار فروش (Sales Analytics)", Icons.Default.BarChart)
        )

        menuItems.forEach { (id, label, icon) ->
            NavigationDrawerItem(
                label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
                icon = { Icon(icon, contentDescription = null) },
                selected = activeMenu == id,
                onClick = { onMenuSelect(id) },
                shape = RoundedCornerShape(12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = JadeGreenPrimary.copy(alpha = 0.2f),
                    selectedIconColor = JadeGreenPrimary,
                    selectedTextColor = JadeGreenPrimary
                ),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Theme Toggle Pill (Dark / Light)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDarkMode) JadeGreenPrimary else Color.Transparent)
                        .clickable { if (!isDarkMode) onToggleDarkMode() }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "حالت شب",
                            color = if (isDarkMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (!isDarkMode) JadeGreenPrimary else Color.Transparent)
                        .clickable { if (isDarkMode) onToggleDarkMode() }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LightMode,
                            contentDescription = null,
                            tint = if (!isDarkMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "حالت روز",
                            color = if (!isDarkMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

// ---------------- CARD 1: DYNAMIC LIVE WEIGHT PRICE PER KG ----------------
@Composable
fun DynamicPricingCard(
    cardBg: Color,
    cardBorder: Color,
    sheepPrice: Float,
    goatPrice: Float,
    onSheepPriceChange: (Float) -> Unit,
    onGoatPriceChange: (Float) -> Unit,
    onUpdatePricesLive: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DYNAMIC LIVE WEIGHT PRICE PER KG",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "موتور تعیین پویای نرخ پایه هر کیلوگرم دام زنده (گوسفند و بز)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Sheep Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "گوسفند (Sheep Average)",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${PersianNumberHelper.formatToman(sheepPrice.toLong())} / کیلو",
                    style = MaterialTheme.typography.titleSmall,
                    color = JadeGreenPrimary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = sheepPrice,
                    onValueChange = onSheepPriceChange,
                    valueRange = 250_000f..500_000f,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = JadeGreenPrimary,
                        activeTrackColor = JadeGreenPrimary
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Increment / Decrement Steppers
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onSheepPriceChange((sheepPrice - 5000f).coerceAtLeast(250000f)) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "کاهش", modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = { onSheepPriceChange((sheepPrice + 5000f).coerceAtMost(500000f)) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "افزایش", modifier = Modifier.size(16.dp))
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = null,
                    tint = StatusDanger,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "روند هفتگی: ۲.۱٪ کاهش نسبت به هفته قبل (Increase/Decrease ↘)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Goats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "بز و بزغاله (Goats Average)",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${PersianNumberHelper.formatToman(goatPrice.toLong())} / کیلو",
                    style = MaterialTheme.typography.titleSmall,
                    color = JadeGreenPrimary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = goatPrice,
                    onValueChange = onGoatPriceChange,
                    valueRange = 200_000f..450_000f,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = GoldenWheatAccent,
                        activeTrackColor = GoldenWheatAccent
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { onGoatPriceChange((goatPrice - 5000f).coerceAtLeast(200000f)) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "کاهش", modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = { onGoatPriceChange((goatPrice + 5000f).coerceAtMost(450000f)) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "افزایش", modifier = Modifier.size(16.dp))
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = null,
                    tint = StatusHealthy,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "روند تقاضا: ۳.۸٪ رشد تقاضای پروار (Live market trends ↗)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Blue / Cyan Glowing Action Button (Matching Attachment)
            Button(
                onClick = onUpdatePricesLive,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(14.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0284C7)
                )
            ) {
                Text(
                    text = "بروزرسانی زنده و سراسری نرخ‌ها (Update Prices Live)",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
            }
        }
    }
}

// ---------------- CARD 2: GALLERY UPLOADER (SHEEP & GOATS) ----------------
@Composable
fun GalleryUploaderCard(
    cardBg: Color,
    cardBorder: Color,
    onAddMediaClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GALLERY UPLOADER (SHEEP & GOATS)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Drag and drop upload area
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                        .clickable(onClick = onAddMediaClick)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = JadeGreenPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+", color = JadeGreenPrimary, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = GoldenWheatAccent)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "محل آپلود و کشیدن فایل‌ها",
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Drag-and-drop file upload area for photos & videos",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Uploaded Items list (Sheep & Boer Goat)
                Column(
                    modifier = Modifier.weight(1.3f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Item 1: Sheep
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sample_shal_sheep),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "گوسفند شال (Sheep)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("4.2 MB", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF334155))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("MP4", color = Color.White, fontSize = 8.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF334155))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("JPG", color = Color.White, fontSize = 8.sp)
                                }
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Item 2: Boer Goat
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sample_boer_goat),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "بز بوئر (Boer goat)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("2.8 MB", style = MaterialTheme.typography.labelSmall, fontSize = 9.sp)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF334155))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("MP4", color = Color.White, fontSize = 8.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF334155))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("JPG", color = Color.White, fontSize = 8.sp)
                                }
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// ---------------- CARD 3: VETERINARY CERTIFICATE STATUS ----------------
data class VetCertRow(
    val id: String,
    val livestock: String,
    val breed: String,
    val certNo: String,
    val expiryDate: String,
    val status: String // APPROVED, PENDING, NEEDS_REVIEW
)

@Composable
fun VeterinaryCertificateStatusCard(
    cardBg: Color,
    cardBorder: Color,
    onDownloadCertificate: (String) -> Unit
) {
    val certs = listOf(
        VetCertRow("01", "گوسفند", "شال قزوین", "000000051", "۱۴۰۴/۰۲/۰۱", "APPROVED"),
        VetCertRow("02", "بز", "بوئر گوشتی", "000000022", "۱۴۰۴/۰۲/۰۱", "PENDING"),
        VetCertRow("03", "گوسفند", "افشاری پشمی", "000000014", "۱۴۰۴/۰۲/۰۱", "NEEDS_REVIEW"),
        VetCertRow("04", "میش", "کردی بارور", "000000020", "۱۴۰۴/۰۲/۰۱", "NEEDS_REVIEW")
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VETERINARY CERTIFICATE STATUS",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "وضعیت اصالت و استعلام گواهی‌های سلامت سازمان دامپزشکی کشور",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("شناسه", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("دام", modifier = Modifier.weight(0.8f), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("نژاد", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("شماره گواهی", modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("وضعیت", modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("دریافت", modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Table Rows
            certs.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(row.id, modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.bodySmall)
                    Text(row.livestock, modifier = Modifier.weight(0.8f), style = MaterialTheme.typography.bodySmall)
                    Text(row.breed, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                    Text(row.certNo, modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.bodySmall, fontSize = 11.sp)

                    // Status Pill (Approved, Pending, Needs Review) matching attachment colors
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = when (row.status) {
                                    "APPROVED" -> StatusHealthy
                                    "PENDING" -> GoldenWheatAccent
                                    else -> Color(0xFFF97316)
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                when (row.status) {
                                    "APPROVED" -> StatusHealthy.copy(alpha = 0.12f)
                                    "PENDING" -> GoldenWheatAccent.copy(alpha = 0.12f)
                                    else -> Color(0xFFF97316).copy(alpha = 0.12f)
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (row.status) {
                                "APPROVED" -> "تأیید شده"
                                "PENDING" -> "در انتظار"
                                else -> "نیازمند بررسی"
                            },
                            color = when (row.status) {
                                "APPROVED" -> StatusHealthy
                                "PENDING" -> GoldenWheatAccent
                                else -> Color(0xFFF97316)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp
                        )
                    }

                    // Download Action
                    IconButton(
                        onClick = { onDownloadCertificate(row.certNo) },
                        modifier = Modifier
                            .weight(0.5f)
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "دانلود گواهی",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// ---------------- CARD 4: REAL-TIME SALES ANALYTICS (CUSTOM CANVAS CHART) ----------------
@Composable
fun SalesAnalyticsChartCard(
    cardBg: Color,
    cardBorder: Color,
    isDarkMode: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REAL-TIME SALES ANALYTICS",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3 KPI Metrics Row (Total Revenue: $148.5K / +12.4% Today / Animals Traded: 342)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "مجموع فروش (Total Revenue):",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "۱۴۸.۵ میلیون تومان",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "+۱۲.۴٪",
                        style = MaterialTheme.typography.titleMedium,
                        color = JadeGreenPrimary
                    )
                    Text(
                        text = "رشد امروز (Today)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "رأس معامله‌شده:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "۳۴۲ رأس",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom High-Fidelity Canvas Chart (Matching Neon Green & Purple Curves in Attachment)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val bottomPadding = 30f
                    val topPadding = 10f
                    val chartHeight = h - bottomPadding - topPadding

                    // Horizontal Grid Lines (400, 300, 200, 100, 0)
                    val gridColor = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0)
                    for (i in 0..4) {
                        val y = topPadding + (chartHeight / 4) * i
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1f
                        )
                    }

                    // Monthly Volume Data Points (11 months: Jan - Nov)
                    // Values between 0.1 and 0.95
                    val greenPoints = listOf(0.15f, 0.25f, 0.28f, 0.42f, 0.85f, 0.58f, 0.64f, 0.72f, 0.60f, 0.78f, 0.92f)
                    val purplePoints = listOf(0.12f, 0.18f, 0.35f, 0.28f, 0.45f, 0.52f, 0.68f, 0.82f, 0.70f, 0.85f, 0.75f)

                    val stepX = w / (greenPoints.size - 1)

                    // Draw Vertical Neon Bars
                    greenPoints.forEachIndexed { i, p ->
                        val barX = i * stepX
                        val barHeight = chartHeight * (p * 0.75f)
                        val barWidth = 14f

                        // Bar
                        drawRoundRect(
                            color = if (i % 2 == 0) Color(0xFF6366F1).copy(alpha = 0.55f) else Color(0xFF06B6D4).copy(alpha = 0.55f),
                            topLeft = Offset(barX - barWidth / 2, topPadding + chartHeight - barHeight),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(4f, 4f)
                        )
                    }

                    // 1. Draw Green Neon Curve Path
                    val greenPath = Path()
                    val greenFillPath = Path()

                    greenPoints.forEachIndexed { i, p ->
                        val x = i * stepX
                        val y = topPadding + chartHeight * (1f - p)
                        if (i == 0) {
                            greenPath.moveTo(x, y)
                            greenFillPath.moveTo(x, topPadding + chartHeight)
                            greenFillPath.lineTo(x, y)
                        } else {
                            val prevX = (i - 1) * stepX
                            val prevY = topPadding + chartHeight * (1f - greenPoints[i - 1])
                            val cx1 = prevX + (x - prevX) / 2
                            val cy1 = prevY
                            val cx2 = prevX + (x - prevX) / 2
                            val cy2 = y
                            greenPath.cubicTo(cx1, cy1, cx2, cy2, x, y)
                            greenFillPath.cubicTo(cx1, cy1, cx2, cy2, x, y)
                        }
                    }

                    greenFillPath.lineTo(w, topPadding + chartHeight)
                    greenFillPath.close()

                    // Gradient Under Green Curve
                    drawPath(
                        path = greenFillPath,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFF10B981).copy(alpha = 0.28f),
                                Color(0xFF10B981).copy(alpha = 0.02f)
                            )
                        )
                    )

                    // Stroke Green Curve
                    drawPath(
                        path = greenPath,
                        color = Color(0xFF10B981),
                        style = Stroke(width = 4f, cap = StrokeCap.Round)
                    )

                    // 2. Draw Purple / Cyan Neon Curve
                    val purplePath = Path()
                    purplePoints.forEachIndexed { i, p ->
                        val x = i * stepX
                        val y = topPadding + chartHeight * (1f - p)
                        if (i == 0) {
                            purplePath.moveTo(x, y)
                        } else {
                            val prevX = (i - 1) * stepX
                            val prevY = topPadding + chartHeight * (1f - purplePoints[i - 1])
                            val cx1 = prevX + (x - prevX) / 2
                            val cy1 = prevY
                            val cx2 = prevX + (x - prevX) / 2
                            val cy2 = y
                            purplePath.cubicTo(cx1, cy1, cx2, cy2, x, y)
                        }
                    }

                    drawPath(
                        path = purplePath,
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFF38BDF8), Color(0xFF818CF8), Color(0xFFA855F7))
                        ),
                        style = Stroke(width = 3f, cap = StrokeCap.Round)
                    )
                }
            }

            // Month Labels (Jan ... Nov / فروردین ... آبان)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val months = listOf("فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور", "مهر", "آبان", "آذر", "دی", "بهمن")
                months.forEachIndexed { i, m ->
                    if (i % 2 == 0) {
                        Text(
                            text = m,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
