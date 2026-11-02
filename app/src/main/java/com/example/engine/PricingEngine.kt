package com.example.engine

import com.example.data.entities.LivestockEntity
import kotlin.math.max
import kotlin.math.min

object PricingEngine {

    /**
     * Calculates the effective price of a livestock item.
     * If pricingType == BY_WEIGHT: weightKg * basePricePerKg
     * If pricingType == FIXED_HEAD: fixedPrice
     * If pricingType == CARCASS: weightKg * 0.52 (carcass yield) * (basePricePerKg * 1.95)
     */
    fun calculateEffectivePrice(item: LivestockEntity): Long {
        return when (item.pricingType) {
            "BY_WEIGHT" -> {
                (item.weightKg * item.basePricePerKg).toLong()
            }
            "CARCASS" -> {
                // Carcass yield is typically ~50-54% in sheep/goats, valued at higher per kg
                val carcassWeightKg = item.weightKg * 0.52
                val carcassPricePerKg = item.basePricePerKg * 1.92
                (carcassWeightKg * carcassPricePerKg).toLong()
            }
            "FIXED_HEAD" -> {
                if (item.fixedPrice > 0) item.fixedPrice else (item.weightKg * item.basePricePerKg).toLong()
            }
            else -> (item.weightKg * item.basePricePerKg).toLong()
        }
    }

    /**
     * Calculates veterinary-standard permitted weight loss in transport.
     * Base loading stress loss = 1.5%
     * Road distance loss = +0.8% per 100 km
     * Maximum allowed threshold = 6.0%
     */
    fun calculatePermittedLossRate(distanceKm: Int): Double {
        val calculated = 1.5 + (distanceKm / 100.0) * 0.8
        return min(6.0, (calculated * 10).toInt() / 10.0)
    }

    /**
     * Settle Escrow against weighbridge slips.
     * If actual loss <= permitted loss, seller gets full payment.
     * If actual loss > permitted loss, excess loss is deducted from seller and refunded to buyer.
     */
    fun settleEscrowWeighbridge(
        initialWeightKg: Double,
        destinationWeightKg: Double,
        basePricePerKg: Long,
        distanceKm: Int
    ): EscrowSettlementResult {
        val permittedLossRate = calculatePermittedLossRate(distanceKm)
        val weightDifference = max(0.0, initialWeightKg - destinationWeightKg)
        val actualLossRate = if (initialWeightKg > 0) (weightDifference / initialWeightKg) * 100.0 else 0.0

        val permittedLossWeight = initialWeightKg * (permittedLossRate / 100.0)
        val excessWeightLost = max(0.0, weightDifference - permittedLossWeight)

        val baseTotalAmount = (initialWeightKg * basePricePerKg).toLong()
        val deductionForExcessLoss = (excessWeightLost * basePricePerKg).toLong()
        val finalSellerAmount = max(0L, baseTotalAmount - deductionForExcessLoss)
        val buyerRefund = deductionForExcessLoss

        return EscrowSettlementResult(
            initialWeightKg = initialWeightKg,
            destinationWeightKg = destinationWeightKg,
            weightDifferenceKg = (weightDifference * 10).toInt() / 10.0,
            permittedLossRate = permittedLossRate,
            actualLossRate = (actualLossRate * 10).toInt() / 10.0,
            excessLossKg = (excessWeightLost * 10).toInt() / 10.0,
            originalTotalToman = baseTotalAmount,
            sellerPayoutToman = finalSellerAmount,
            buyerRefundToman = buyerRefund,
            isWithinTolerance = excessWeightLost <= 0.05
        )
    }

    /**
     * Dynamic Weight Predictor & Growth Estimator based on age and diet quality.
     * Mirrors the interactive slider shown in the design attachment.
     */
    fun predictWeight(baseWeight: Double, ageMonths: Int, dietLevel: Int): Double {
        // dietLevel: 1 = معمولی (Standard forage), 2 = پرواری صنعتی (Standard feed), 3 = پرمیوم / ممتاز (Premium concentrate)
        val monthlyGrowthFactor = when (dietLevel) {
            1 -> 1.8
            2 -> 2.6
            3 -> 3.5
            else -> 2.5
        }
        val estimated = 25.0 + (ageMonths * monthlyGrowthFactor)
        return min(120.0, max(20.0, (estimated * 10).toInt() / 10.0))
    }

    /**
     * Compute logistics, butchery and total checkout bill
     */
    fun calculateOrderBreakdown(
        livestockPrice: Long,
        deliveryType: String,
        butcheryOption: String,
        distanceKm: Int,
        commissionPercent: Double = 2.5,
        costPerKm: Long = 25_000L
    ): OrderBreakdown {
        val transportCost = if (deliveryType == "LIVE_TRANSPORT" || deliveryType == "SLAUGHTER_BUTCHERY") {
            distanceKm * costPerKm
        } else 0L

        val butcheryCost = if (deliveryType == "SLAUGHTER_BUTCHERY") {
            when (butcheryOption) {
                "CARCASS_HALVES" -> 250_000L
                "FOUR_PIECES" -> 400_000L
                "CUSTOM_PACKAGED" -> 750_000L
                else -> 300_000L
            }
        } else 0L

        val platformCommission = (livestockPrice * (commissionPercent / 100.0)).toLong()
        val totalGrandAmount = livestockPrice + transportCost + butcheryCost

        return OrderBreakdown(
            livestockPrice = livestockPrice,
            transportCost = transportCost,
            butcheryCost = butcheryCost,
            platformCommission = platformCommission,
            totalGrandAmount = totalGrandAmount
        )
    }
}

data class EscrowSettlementResult(
    val initialWeightKg: Double,
    val destinationWeightKg: Double,
    val weightDifferenceKg: Double,
    val permittedLossRate: Double,
    val actualLossRate: Double,
    val excessLossKg: Double,
    val originalTotalToman: Long,
    val sellerPayoutToman: Long,
    val buyerRefundToman: Long,
    val isWithinTolerance: Boolean
)

data class OrderBreakdown(
    val livestockPrice: Long,
    val transportCost: Long,
    val butcheryCost: Long,
    val platformCommission: Long,
    val totalGrandAmount: Long
)
