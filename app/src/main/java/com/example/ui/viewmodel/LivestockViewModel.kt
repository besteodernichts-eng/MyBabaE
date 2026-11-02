package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.entities.DailyPriceRuleEntity
import com.example.data.entities.EscrowOrderEntity
import com.example.data.entities.LivestockEntity
import com.example.data.entities.MediaItemEntity
import com.example.data.repository.LivestockRepository
import com.example.engine.PricingEngine
import com.example.model.ButcheryOption
import com.example.model.DeliveryType
import com.example.model.LivestockBreed
import com.example.model.LivestockType
import com.example.model.PurchasePurpose
import com.example.model.TeethStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Marketplace : ScreenDestination()
    data class Detail(val livestockId: Long) : ScreenDestination()
    object NewListing : ScreenDestination()
    object AdminPanel : ScreenDestination()
}

class LivestockViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LivestockRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = LivestockRepository(db)
        viewModelScope.launch {
            // Ensure sample data is populated
            AppDatabase.populateInitialData(db)
        }
    }

    // App Theme State
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // Navigation State
    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Marketplace)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    fun navigateTo(destination: ScreenDestination) {
        _currentScreen.value = destination
    }

    // Admin Tab State (0: Pricing, 1: Media Review, 2: Escrow, 3: Dashboard)
    private val _adminTab = MutableStateFlow(0)
    val adminTab: StateFlow<Int> = _adminTab.asStateFlow()

    fun setAdminTab(index: Int) {
        _adminTab.value = index
    }

    // Search and Filter State
    val searchQuery = MutableStateFlow("")
    val selectedTypeFilter = MutableStateFlow(LivestockType.ALL)
    val selectedBreedFilter = MutableStateFlow<LivestockBreed?>(null)
    val selectedPurposeFilter = MutableStateFlow(PurchasePurpose.ALL)
    val weightRange = MutableStateFlow(20f..120f)
    val selectedTeethFilter = MutableStateFlow<TeethStatus?>(null)
    val filterLiveAuctionOnly = MutableStateFlow(false)
    val isFilterSheetVisible = MutableStateFlow(false)

    // Data Flows from Repository
    val allLivestockList = repository.allLivestock.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val liveAuctions = repository.liveAuctions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val priceRules = repository.allPriceRules.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val mediaItems = repository.allMedia.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val escrowOrders = repository.allEscrowOrders.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val unsyncedCount = repository.unsyncedCount.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    // Filtered Livestock List
    val filteredLivestock: StateFlow<List<LivestockEntity>> = combine(
        allLivestockList,
        searchQuery,
        selectedTypeFilter,
        selectedBreedFilter,
        selectedPurposeFilter,
        weightRange,
        selectedTeethFilter,
        filterLiveAuctionOnly
    ) { params ->
        val list = params[0] as List<LivestockEntity>
        val query = params[1] as String
        val type = params[2] as LivestockType
        val breed = params[3] as LivestockBreed?
        val purpose = params[4] as PurchasePurpose
        val weight = params[5] as ClosedFloatingPointRange<Float>
        val teeth = params[6] as TeethStatus?
        val auctionOnly = params[7] as Boolean

        list.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.breed.contains(query, ignoreCase = true) ||
                    item.province.contains(query, ignoreCase = true) ||
                    item.city.contains(query, ignoreCase = true) ||
                    item.rfidCode.contains(query, ignoreCase = true)

            val matchesType = when (type) {
                LivestockType.ALL -> true
                else -> item.livestockType == type.name
            }

            val matchesBreed = breed == null || item.breed == breed.name

            val matchesPurpose = when (purpose) {
                PurchasePurpose.ALL -> true
                else -> item.purpose == purpose.name
            }

            val matchesWeight = item.weightKg in weight.start..weight.endInclusive

            val matchesTeeth = teeth == null || item.teethStatus == teeth.name

            val matchesAuction = !auctionOnly || item.isLiveAuction

            matchesQuery && matchesType && matchesBreed && matchesPurpose &&
                    matchesWeight && matchesTeeth && matchesAuction
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Detail Screen Interactive State
    private val _rotationAngle = MutableStateFlow(0f)
    val rotationAngle: StateFlow<Float> = _rotationAngle.asStateFlow()

    private val _isAutoRotating = MutableStateFlow(false)
    val isAutoRotating: StateFlow<Boolean> = _isAutoRotating.asStateFlow()

    private val _detailMediaTab = MutableStateFlow(0)
    val detailMediaTab: StateFlow<Int> = _detailMediaTab.asStateFlow()

    val predictorAge = MutableStateFlow(14)
    val predictorDiet = MutableStateFlow(2) // 1: معمولی, 2: صنعتی, 3: ممتاز

    val selectedDeliveryType = MutableStateFlow(DeliveryType.LIVE_TRANSPORT)
    val selectedButcheryOption = MutableStateFlow(ButcheryOption.FOUR_PIECES)
    val transportDistanceKm = MutableStateFlow(45)

    val userBidInput = MutableStateFlow("")
    val toastMessage = MutableStateFlow<String?>(null)

    fun setRotationAngle(angle: Float) {
        _rotationAngle.value = (angle % 360f + 360f) % 360f
    }

    fun toggleAutoRotation() {
        _isAutoRotating.value = !_isAutoRotating.value
        if (_isAutoRotating.value) {
            viewModelScope.launch {
                while (_isAutoRotating.value) {
                    delay(50)
                    _rotationAngle.value = (_rotationAngle.value + 4f) % 360f
                }
            }
        }
    }

    fun setDetailMediaTab(tab: Int) {
        _detailMediaTab.value = tab
    }

    // Place bid in auction
    fun placeBid(livestockId: Long, currentBid: Long) {
        val amount = userBidInput.value.toLongOrNull() ?: (currentBid + 500_000L)
        viewModelScope.launch {
            repository.placeAuctionBid(livestockId, amount)
            userBidInput.value = ""
            toastMessage.value = "پیشنهاد شما به مبلغ ${amount} با موفقیت ثبت شد"
        }
    }

    // Create Escrow Order for Buy Now
    fun createEscrowOrder(item: LivestockEntity) {
        viewModelScope.launch {
            val breakdown = PricingEngine.calculateOrderBreakdown(
                livestockPrice = PricingEngine.calculateEffectivePrice(item),
                deliveryType = selectedDeliveryType.value.name,
                butcheryOption = selectedButcheryOption.value.name,
                distanceKm = transportDistanceKm.value
            )
            val order = EscrowOrderEntity(
                orderNumber = "ESC-${System.currentTimeMillis() % 10000000}",
                livestockId = item.id,
                buyerName = "خریدار پلتفرم",
                sellerName = item.sellerName,
                deliveryType = selectedDeliveryType.value.name,
                butcheryOption = selectedButcheryOption.value.name,
                initialWeightKg = item.weightKg,
                originWeighbridgeSlip = "باسکول مبدا ${item.city}: ${item.weightKg} کیلوگرم",
                destinationWeightKg = 0.0,
                destinationWeighbridgeSlip = "در انتظار ورود به باسکول مقصد",
                distanceKm = transportDistanceKm.value,
                permittedLossRate = PricingEngine.calculatePermittedLossRate(transportDistanceKm.value),
                actualLossRate = 0.0,
                totalAmountToman = breakdown.totalGrandAmount,
                adjustedAmountToman = breakdown.totalGrandAmount,
                status = "LOCKED",
                dateJalali = "امروز"
            )
            repository.createEscrowOrder(order)
            toastMessage.value = "مبلغ در حساب امن امانی مسدود شد. کد پیگیری: ${order.orderNumber}"
        }
    }

    // Admin: Dynamic Pricing Engine - Update Base Price Globally
    fun updateBasePriceGlobally(rule: DailyPriceRuleEntity, newPrice: Long) {
        viewModelScope.launch {
            repository.updateBasePriceGlobally(rule.id, rule.breed, newPrice)
            toastMessage.value = "نرخ روزانه نژاد ${rule.breed} به ${newPrice} بروزرسانی و بر روی تمام آگهی‌ها اعمال شد"
        }
    }

    // Admin: Media moderation
    fun reviewMedia(mediaId: Long, approve: Boolean, reason: String? = null) {
        viewModelScope.launch {
            repository.reviewMedia(mediaId, approve, reason)
            val action = if (approve) "تأیید شد و واترمارک درج گردید" else "رد شد و پیامک برای دامدار ارسال گردید"
            toastMessage.value = "رسانه شناسه ${mediaId} $action"
        }
    }

    // Admin: Settle Weighbridge discrepancy
    fun settleWeighbridgeOrder(order: EscrowOrderEntity, destinationWeight: Double) {
        viewModelScope.launch {
            repository.settleWeighbridge(order.id, destinationWeight, order)
            toastMessage.value = "توزین باسکول نهایی ثبت و تسویه حساب امانی انجام شد"
        }
    }

    // Sync offline listings
    fun syncOfflineListings() {
        viewModelScope.launch {
            repository.syncOfflineListings()
            toastMessage.value = "همگام‌سازی اطلاعات با سرور با موفقیت انجام شد"
        }
    }

    // Create New Listing (with offline support)
    fun createListing(
        title: String,
        livestockType: LivestockType,
        breed: LivestockBreed,
        purpose: PurchasePurpose,
        pricingType: com.example.model.PricingType,
        weightKg: Double,
        ageMonths: Int,
        teethStatus: TeethStatus,
        pregnancyStatus: com.example.model.PregnancyStatus,
        rfidCode: String,
        province: String,
        city: String,
        fixedPrice: Long,
        isLiveAuction: Boolean,
        auctionMinBid: Long,
        sellerName: String,
        sellerPhone: String,
        isOffline: Boolean
    ) {
        viewModelScope.launch {
            val entity = LivestockEntity(
                title = title,
                livestockType = livestockType.name,
                breed = breed.name,
                purpose = purpose.name,
                pricingType = pricingType.name,
                weightKg = weightKg,
                ageMonths = ageMonths,
                teethStatus = teethStatus.name,
                pregnancyStatus = pregnancyStatus.name,
                rfidCode = rfidCode.ifBlank { "IR-98210${(1000000..9999999).random()}" },
                vetTrackingCode = "VET-${(100000..999999).random()}",
                province = province,
                city = city,
                distanceKm = (10..150).random(),
                basePricePerKg = 350_000L,
                fixedPrice = fixedPrice,
                imageRes = if (livestockType == LivestockType.GOAT || livestockType == LivestockType.KID) "sample_boer_goat" else "sample_dorper_sheep",
                isLiveAuction = isLiveAuction,
                auctionMinBid = auctionMinBid,
                auctionCurrentBid = auctionMinBid,
                auctionEndsInMinutes = if (isLiveAuction) 120 else 0,
                auctionBidsCount = 0,
                sellerName = sellerName,
                sellerPhone = sellerPhone,
                healthCertificateVerified = true,
                isOfflineCreated = isOffline,
                isSynced = !isOffline,
                createdAtJalali = "امروز"
            )
            repository.addNewLivestock(entity)
            toastMessage.value = if (isOffline) {
                "آگهی در حافظه محلی (آفلاین) ذخیره شد و پس از اتصال اینترنت همگام می‌شود"
            } else {
                "آگهی جدید با پلاک گوش و شناسنامه الکترونیکی با موفقیت ثبت شد"
            }
            _currentScreen.value = ScreenDestination.Marketplace
        }
    }

    fun dismissToast() {
        toastMessage.value = null
    }

    fun resetFilters() {
        searchQuery.value = ""
        selectedTypeFilter.value = LivestockType.ALL
        selectedBreedFilter.value = null
        selectedPurposeFilter.value = PurchasePurpose.ALL
        weightRange.value = 20f..120f
        selectedTeethFilter.value = null
        filterLiveAuctionOnly.value = false
    }
}
