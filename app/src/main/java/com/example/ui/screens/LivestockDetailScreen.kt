package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.engine.PricingEngine
import com.example.model.ButcheryOption
import com.example.model.DeliveryType
import com.example.ui.components.DynamicWeightPredictor
import com.example.ui.components.PersianNumberHelper
import com.example.ui.components.ThreeSixtyViewer
import com.example.ui.theme.DarkCharcoalBg
import com.example.ui.theme.GoldenWheatAccent
import com.example.ui.theme.JadeGreenPrimary
import com.example.ui.theme.StatusHealthy
import com.example.ui.theme.StatusRfid
import com.example.ui.viewmodel.LivestockViewModel
import com.example.ui.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LivestockDetailScreen(
    livestockId: Long,
    viewModel: LivestockViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(ScreenDestination.Marketplace)
    }

    val allItems by viewModel.allLivestockList.collectAsState()
    val item = allItems.find { it.id == livestockId } ?: allItems.firstOrNull()

    if (item == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("اطلاعات دام یافت نشد")
        }
        return
    }

    val rotationAngle by viewModel.rotationAngle.collectAsState()
    val isAutoRotating by viewModel.isAutoRotating.collectAsState()
    val detailMediaTab by viewModel.detailMediaTab.collectAsState()
    val predictorAge by viewModel.predictorAge.collectAsState()
    val predictorDiet by viewModel.predictorDiet.collectAsState()
    val selectedDelivery by viewModel.selectedDeliveryType.collectAsState()
    val selectedButchery by viewModel.selectedButcheryOption.collectAsState()
    val transportDistance by viewModel.transportDistanceKm.collectAsState()
    val userBidInput by viewModel.userBidInput.collectAsState()

    var showBidModal by remember { mutableStateOf(false) }
    var showCheckoutModal by remember { mutableStateOf(false) }
    var showVetDialog by remember { mutableStateOf(false) }

    val effectivePrice = PricingEngine.calculateEffectivePrice(item)
    val permittedLossRate = PricingEngine.calculatePermittedLossRate(transportDistance)
    val estDeliveredWeight = item.weightKg * (1.0 - (permittedLossRate / 100.0))

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "شناسنامه دیجیتال دام سبک",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenDestination.Marketplace) },
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Favorite */ }) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "علاقه‌مندی"
                        )
                    }
                    IconButton(onClick = { /* Share */ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "اشتراک‌گذاری شناسنامه"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Dual Action Bottom Bar (Place Bid / Buy Now with Escrow) matching attachment screenshot
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (item.isLiveAuction) {
                        OutlinedButton(
                            onClick = { showBidModal = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("place_bid_button"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "ثبت پیشنهاد • ${PersianNumberHelper.formatToman(item.auctionCurrentBid)}",
                                style = MaterialTheme.typography.labelLarge,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }

                    Button(
                        onClick = { showCheckoutModal = true },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(52.dp)
                            .testTag("buy_now_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = JadeGreenPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "خرید امانی • ${PersianNumberHelper.formatToman(effectivePrice)}",
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Media View Tabs: 360 View, Full Profile, Teeth Verification, RFID Tag
            TabRow(
                selectedTabIndex = detailMediaTab,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
            ) {
                val mediaTabs = listOf("چرخش ۳۶۰°", "نمای کامل", "وضعیت دندان", "پلاک گوش")
                mediaTabs.forEachIndexed { index, tabTitle ->
                    Tab(
                        selected = detailMediaTab == index,
                        onClick = { viewModel.setDetailMediaTab(index) },
                        text = {
                            Text(
                                text = tabTitle,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active Media Area
            when (detailMediaTab) {
                0 -> {
                    // Interactive 360 Degree Walking View
                    ThreeSixtyViewer(
                        imageResName = item.imageRes,
                        rotationAngle = rotationAngle,
                        isAutoRotating = isAutoRotating,
                        onAngleChange = { viewModel.setRotationAngle(it) },
                        onToggleAutoRotate = { viewModel.toggleAutoRotation() }
                    )
                }
                else -> {
                    // Static Media (Full Profile, Teeth check, or RFID close-up)
                    val drawableRes = when (item.imageRes) {
                        "sample_shal_sheep" -> R.drawable.sample_shal_sheep
                        "sample_boer_goat" -> R.drawable.sample_boer_goat
                        else -> R.drawable.sample_dorper_sheep
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(RoundedCornerShape(24.dp))
                    ) {
                        Image(
                            painter = painterResource(id = drawableRes),
                            contentDescription = "رسانه تایید شده دام",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Watermark Overlay on Approved Media
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "تأیید اصالت • ببعی من",
                                color = GoldenWheatAccent,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Livestock Title & RFID Heading (matching attachment)
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "کد ۱۵ رقمی پلاک ملی گوش: ${PersianNumberHelper.toPersianDigits(item.rfidCode)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // The 3 Prominent Stat Badges in a Row (as in design attachment screenshot):
            // 1. Digital RFID Badge, 2. Veterinary Health Certificate, 3. Current Weight
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Card 1: RFID Badge
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StatusRfid.copy(alpha = 0.10f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Nfc,
                            contentDescription = null,
                            tint = StatusRfid,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "پلاک RFID",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusRfid,
                            fontSize = 10.sp
                        )
                        Text(
                            text = PersianNumberHelper.toPersianDigits(item.rfidCode.takeLast(7)),
                            style = MaterialTheme.typography.labelMedium,
                            color = StatusRfid,
                            maxLines = 1
                        )
                    }
                }

                // Card 2: Veterinary Certificate
                Card(
                    modifier = Modifier
                        .weight(1.1f)
                        .clickable { showVetDialog = true },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StatusHealthy.copy(alpha = 0.12f))
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = StatusHealthy,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "گواهی سلامت",
                            style = MaterialTheme.typography.labelSmall,
                            color = StatusHealthy,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "تأیید دامپزشک",
                            style = MaterialTheme.typography.labelMedium,
                            color = StatusHealthy
                        )
                    }
                }

                // Card 3: Current Weight
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Scale,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "وزن باسکول",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                        Text(
                            text = PersianNumberHelper.formatWeightKg(item.weightKg),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dynamic Weight Calculator & Predictor (from mockup)
            DynamicWeightPredictor(
                currentAgeMonths = predictorAge,
                currentDietLevel = predictorDiet,
                baseWeightKg = item.weightKg,
                basePricePerKg = item.basePricePerKg,
                onAgeChanged = { viewModel.predictorAge.value = it },
                onDietChanged = { viewModel.predictorDiet.value = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Value-Added Services & Delivery Method Selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "نحوه تحویل و خدمات ارزش افزوده قصابی",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Option A: Live Delivery
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.selectedDeliveryType.value = DeliveryType.LIVE_TRANSPORT }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedDelivery == DeliveryType.LIVE_TRANSPORT,
                            onClick = { viewModel.selectedDeliveryType.value = DeliveryType.LIVE_TRANSPORT },
                            colors = RadioButtonDefaults.colors(selectedColor = JadeGreenPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, tint = JadeGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "ارسال دام زنده",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "حمل با خودروی مخصوص انتقال دام و باسکول مقصد",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Option B: Slaughter & Butchery
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.selectedDeliveryType.value = DeliveryType.SLAUGHTER_BUTCHERY }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedDelivery == DeliveryType.SLAUGHTER_BUTCHERY,
                            onClick = { viewModel.selectedDeliveryType.value = DeliveryType.SLAUGHTER_BUTCHERY },
                            colors = RadioButtonDefaults.colors(selectedColor = JadeGreenPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(imageVector = Icons.Default.Restaurant, contentDescription = null, tint = GoldenWheatAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "خدمات کشتارگاهی و قصابی شرعی",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = "ذبح شرعی، خرد کردن سفارشی، پاک‌کردن کله‌پاچه و بسته‌بندی وکیوم یخچال‌دار",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Sub-options if Butchery selected
                    if (selectedDelivery == DeliveryType.SLAUGHTER_BUTCHERY) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "نوع قطعه‌بندی گوشت:",
                            style = MaterialTheme.typography.labelMedium,
                            color = GoldenWheatAccent
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        ButcheryOption.values().forEach { option ->
                            FilterChip(
                                selected = selectedButchery == option,
                                onClick = { viewModel.selectedButcheryOption.value = option },
                                label = {
                                    Text("${option.titleFa} (+${PersianNumberHelper.formatToman(option.additionalCostToman)})")
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldenWheatAccent.copy(alpha = 0.2f),
                                    selectedLabelColor = GoldenWheatAccent
                                ),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Transport Distance & Smart Weight Loss Calculator
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "محاسبه‌گر هوشمند افت وزن در مسافت حمل و نقل",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "محاسبه درصد مجاز افت وزن طبیعی از باسکول مبدا تا مقصد",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "فاصله تا مقصد:",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "${PersianNumberHelper.toPersianDigits(transportDistance)} کیلومتر",
                            style = MaterialTheme.typography.labelMedium,
                            color = JadeGreenPrimary
                        )
                    }

                    Slider(
                        value = transportDistance.toFloat(),
                        onValueChange = { viewModel.transportDistanceKm.value = it.toInt() },
                        valueRange = 10f..400f,
                        steps = 38,
                        colors = SliderDefaults.colors(
                            thumbColor = JadeGreenPrimary,
                            activeTrackColor = JadeGreenPrimary
                        )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "افت وزن مجاز:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${PersianNumberHelper.toPersianDigits(permittedLossRate)} درصد",
                                style = MaterialTheme.typography.titleSmall,
                                color = GoldenWheatAccent
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "وزن تخمینی تحویل:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = PersianNumberHelper.formatWeightKg(estDeliveredWeight),
                                style = MaterialTheme.typography.titleSmall,
                                color = JadeGreenPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }

        // Live Auction Bid Dialog
        if (showBidModal) {
            AlertDialog(
                onDismissRequest = { showBidModal = false },
                title = { Text("ثبت پیشنهاد در مزایده زنده") },
                text = {
                    Column {
                        Text(
                            text = "آخرین قیمت پیشنهادی: ${PersianNumberHelper.formatToman(item.auctionCurrentBid)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = userBidInput,
                            onValueChange = { viewModel.userBidInput.value = it },
                            label = { Text("مبلغ پیشنهادی جدید (تومان)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.placeBid(item.id, item.auctionCurrentBid)
                            showBidModal = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldenWheatAccent)
                    ) {
                        Text("ثبت نهایی پیشنهاد")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showBidModal = false }) {
                        Text("انصراف")
                    }
                }
            )
        }

        // Buy Now Escrow Checkout Dialog
        if (showCheckoutModal) {
            val breakdown = PricingEngine.calculateOrderBreakdown(
                livestockPrice = effectivePrice,
                deliveryType = selectedDelivery.name,
                butcheryOption = selectedButchery.name,
                distanceKm = transportDistance
            )

            AlertDialog(
                onDismissRequest = { showCheckoutModal = false },
                title = { Text("پرداخت امن با حساب امانی (Escrow)") },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "وجه پرداختی تا زمان رسیدن دام به باسکول مقصد، سنجش افت وزن مجاز و تأیید سلامت در حساب امانی مسدود می‌ماند.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("قیمت پایه دام:", style = MaterialTheme.typography.bodySmall)
                            Text(PersianNumberHelper.formatToman(breakdown.livestockPrice), style = MaterialTheme.typography.bodySmall)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("کرایه خودروی حمل (${PersianNumberHelper.formatDistance(transportDistance)}):", style = MaterialTheme.typography.bodySmall)
                            Text(PersianNumberHelper.formatToman(breakdown.transportCost), style = MaterialTheme.typography.bodySmall)
                        }
                        if (breakdown.butcheryCost > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("خدمات قصابی و بسته‌بندی:", style = MaterialTheme.typography.bodySmall)
                                Text(PersianNumberHelper.formatToman(breakdown.butcheryCost), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("مبلغ کل جهت بلوکه در حساب امانی:", style = MaterialTheme.typography.titleSmall)
                            Text(PersianNumberHelper.formatToman(breakdown.totalGrandAmount), style = MaterialTheme.typography.titleSmall, color = JadeGreenPrimary)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.createEscrowOrder(item)
                            showCheckoutModal = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = JadeGreenPrimary)
                    ) {
                        Text("تأیید و پرداخت امن امانی")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { showCheckoutModal = false }) {
                        Text("انصراف")
                    }
                }
            )
        }

        // Health & Vaccination History Dialog
        if (showVetDialog) {
            AlertDialog(
                onDismissRequest = { showVetDialog = false },
                title = { Text("شناسنامه سلامت و واکسیناسیون دامپزشکی") },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "کد رهگیری دامپزشکی: ${item.vetTrackingCode}",
                            style = MaterialTheme.typography.labelMedium,
                            color = JadeGreenPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        val vaccines = listOf(
                            "واکسن تب برفکی (Foot & Mouth)" to item.vaccineFootAndMouth,
                            "واکسن آبله گوسفندی (Sheep Pox)" to item.vaccinePox,
                            "واکسن بروسلوز / تب مالت (Brucellosis)" to item.vaccineBrucellosis,
                            "واکسن شاربن / سیاه‌زخم (Anthrax)" to item.vaccineAnthrax
                        )
                        vaccines.forEach { (name, date) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = StatusHealthy,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = name, style = MaterialTheme.typography.bodySmall)
                                }
                                Text(
                                    text = PersianNumberHelper.toPersianDigits(date),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showVetDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = JadeGreenPrimary)
                    ) {
                        Text("متوجه شدم")
                    }
                }
            )
        }
    }
}
