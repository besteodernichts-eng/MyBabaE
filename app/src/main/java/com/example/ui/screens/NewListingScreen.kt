package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LivestockBreed
import com.example.model.LivestockType
import com.example.model.PricingType
import com.example.model.PregnancyStatus
import com.example.model.PurchasePurpose
import com.example.model.TeethStatus
import com.example.ui.components.PersianNumberHelper
import com.example.ui.theme.GoldenWheatAccent
import com.example.ui.theme.JadeGreenPrimary
import com.example.ui.theme.StatusWarning
import com.example.ui.viewmodel.LivestockViewModel
import com.example.ui.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewListingScreen(
    viewModel: LivestockViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(ScreenDestination.Marketplace)
    }

    var title by remember { mutableStateOf("قوچ نژاد افشاری اصیل") }
    var selectedType by remember { mutableStateOf(LivestockType.RAM) }
    var selectedBreed by remember { mutableStateOf(LivestockBreed.AFSHARI) }
    var selectedPurpose by remember { mutableStateOf(PurchasePurpose.BREEDING) }
    var selectedPricingType by remember { mutableStateOf(PricingType.BY_WEIGHT) }
    var weightText by remember { mutableStateOf("75") }
    var ageMonthsText by remember { mutableStateOf("15") }
    var selectedTeeth by remember { mutableStateOf(TeethStatus.TWO_TEETH) }
    var rfidCode by remember { mutableStateOf("IR-982109847123") }
    var province by remember { mutableStateOf("زنجان") }
    var city by remember { mutableStateOf("ابهر") }
    var fixedPriceText by remember { mutableStateOf("26000000") }
    var isLiveAuction by remember { mutableStateOf(false) }
    var minBidText by remember { mutableStateOf("25000000") }
    var sellerName by remember { mutableStateOf("حاج علی مرادی") }
    var sellerPhone by remember { mutableStateOf("09121234567") }
    var isOfflineMode by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ثبت آگهی جدید دام سبک",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateTo(ScreenDestination.Marketplace) },
                        modifier = Modifier.testTag("new_listing_back")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            viewModel.createListing(
                                title = title,
                                livestockType = selectedType,
                                breed = selectedBreed,
                                purpose = selectedPurpose,
                                pricingType = selectedPricingType,
                                weightKg = weightText.toDoubleOrNull() ?: 60.0,
                                ageMonths = ageMonthsText.toIntOrNull() ?: 12,
                                teethStatus = selectedTeeth,
                                pregnancyStatus = PregnancyStatus.NOT_APPLICABLE,
                                rfidCode = rfidCode,
                                province = province,
                                city = city,
                                fixedPrice = fixedPriceText.toLongOrNull() ?: 0L,
                                isLiveAuction = isLiveAuction,
                                auctionMinBid = minBidText.toLongOrNull() ?: 0L,
                                sellerName = sellerName,
                                sellerPhone = sellerPhone,
                                isOffline = isOfflineMode
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("submit_listing_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOfflineMode) StatusWarning else JadeGreenPrimary
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isOfflineMode) "ذخیره در حالت آفلاین (روستایی)" else "ثبت آگهی و صدور شناسنامه",
                            style = MaterialTheme.typography.titleSmall
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Offline Mode Toggle Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isOfflineMode) StatusWarning.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = if (isOfflineMode) StatusWarning else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ثبت در مناطق روستایی (حالت آفلاین)",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "بدون نیاز به اینترنت، با اتصال بعدی همگام می‌شود",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Switch(
                        checked = isOfflineMode,
                        onCheckedChange = { isOfflineMode = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = StatusWarning,
                            checkedTrackColor = StatusWarning.copy(alpha = 0.4f)
                        )
                    )
                }
            }

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("عنوان آگهی") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Livestock Type Chips
            Text(
                text = "نوع دام:",
                style = MaterialTheme.typography.titleSmall
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(LivestockType.RAM, LivestockType.SHEEP, LivestockType.LAMB, LivestockType.GOAT).forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type.titleFa) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JadeGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Breed Selector Chips
            Text(
                text = "نژاد دام:",
                style = MaterialTheme.typography.titleSmall
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(LivestockBreed.AFSHARI, LivestockBreed.SHAL, LivestockBreed.GHEZEL, LivestockBreed.BOER).forEach { breed ->
                    FilterChip(
                        selected = selectedBreed == breed,
                        onClick = { selectedBreed = breed },
                        label = { Text(breed.titleFa) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JadeGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // 15-Digit RFID Code & Weight
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = rfidCode,
                    onValueChange = { rfidCode = it },
                    label = { Text("پلاک گوش ملی (RFID)") },
                    modifier = Modifier.weight(1.5f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("وزن زنده (کیلو)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // Age & Teeth Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = ageMonthsText,
                    onValueChange = { ageMonthsText = it },
                    label = { Text("سن دام (ماه)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = province,
                    onValueChange = { province = it },
                    label = { Text("استان") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("شهر") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            // Pricing Mode Selector
            Text(text = "نحوه قیمت‌گذاری:", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PricingType.values().forEach { pt ->
                    FilterChip(
                        selected = selectedPricingType == pt,
                        onClick = { selectedPricingType = pt },
                        label = { Text(pt.titleFa) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldenWheatAccent,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            if (selectedPricingType == PricingType.FIXED_HEAD) {
                OutlinedTextField(
                    value = fixedPriceText,
                    onValueChange = { fixedPriceText = it },
                    label = { Text("قیمت مقطوع راسی (تومان)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // Live Auction Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("فروش به صورت مزایده زنده آنلاین", style = MaterialTheme.typography.titleSmall)
                    Text("تعیین کف قیمت و آغاز رقابت خریداران", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = isLiveAuction, onCheckedChange = { isLiveAuction = it })
            }

            if (isLiveAuction) {
                OutlinedTextField(
                    value = minBidText,
                    onValueChange = { minBidText = it },
                    label = { Text("کف قیمت مزایده (تومان)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // Seller Contact Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = sellerName,
                    onValueChange = { sellerName = it },
                    label = { Text("نام دامدار") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = sellerPhone,
                    onValueChange = { sellerPhone = it },
                    label = { Text("شماره تماس") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }

            // Media Uploader Preview Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null, tint = JadeGreenPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "تصویر کامل، عکس دندان‌ها و فیلم ۳۶۰ درجه بارگذاری شده است",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
