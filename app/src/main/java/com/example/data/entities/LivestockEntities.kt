package com.example.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "livestock")
data class LivestockEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val livestockType: String,
    val breed: String,
    val purpose: String,
    val pricingType: String,
    val weightKg: Double,
    val ageMonths: Int,
    val teethStatus: String,
    val pregnancyStatus: String,
    val rfidCode: String,
    val vetTrackingCode: String,
    val province: String,
    val city: String,
    val distanceKm: Int,
    val basePricePerKg: Long,
    val fixedPrice: Long,
    val imageRes: String,
    val isLiveAuction: Boolean = false,
    val auctionMinBid: Long = 0L,
    val auctionCurrentBid: Long = 0L,
    val auctionEndsInMinutes: Int = 0,
    val auctionBidsCount: Int = 0,
    val sellerName: String,
    val sellerPhone: String,
    val healthCertificateVerified: Boolean = true,
    val vaccineFootAndMouth: String = "۱۴۰۳/۰۷/۱۵",
    val vaccinePox: String = "۱۴۰۳/۰۶/۲۰",
    val vaccineBrucellosis: String = "۱۴۰۳/۰۵/۱۰",
    val vaccineAnthrax: String = "۱۴۰۳/۰۴/۱۸",
    val isOfflineCreated: Boolean = false,
    val isSynced: Boolean = true,
    val createdAtJalali: String = "۱۴۰۳/۰۸/۱۰"
)

@Entity(tableName = "daily_price_rules")
data class DailyPriceRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val livestockType: String,
    val breed: String,
    val province: String,
    val basePricePerKg: Long,
    val lastUpdatedJalali: String = "امروز - ۱۰:۳۰"
)

@Entity(tableName = "media_items")
data class MediaItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val livestockId: Long,
    val mediaType: String, // 360, FULL, TEETH, RFID
    val title: String,
    val imageResName: String,
    val isWatermarked: Boolean = true,
    val status: String = "APPROVED", // PENDING, APPROVED, REJECTED
    val rejectionReason: String? = null
)

@Entity(tableName = "escrow_orders")
data class EscrowOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String,
    val livestockId: Long,
    val buyerName: String,
    val sellerName: String,
    val deliveryType: String,
    val butcheryOption: String,
    val initialWeightKg: Double,
    val originWeighbridgeSlip: String,
    val destinationWeightKg: Double,
    val destinationWeighbridgeSlip: String,
    val distanceKm: Int,
    val permittedLossRate: Double,
    val actualLossRate: Double,
    val totalAmountToman: Long,
    val adjustedAmountToman: Long,
    val status: String,
    val dateJalali: String
)

@Entity(tableName = "platform_settings")
data class PlatformSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val commissionPercent: Double = 2.5,
    val transportCostPerKm: Long = 25_000L,
    val baseButcheryCost: Long = 350_000L
)
