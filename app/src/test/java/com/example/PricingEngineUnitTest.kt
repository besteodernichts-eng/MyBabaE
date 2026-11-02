package com.example

import com.example.data.entities.LivestockEntity
import com.example.engine.PricingEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PricingEngineUnitTest {

    @Test
    fun testWeightPricingCalculation() {
        val sheep = LivestockEntity(
            id = 1,
            title = "قوچ شال",
            livestockType = "RAM",
            breed = "SHAL",
            purpose = "FATTENING",
            pricingType = "BY_WEIGHT",
            weightKg = 80.0,
            ageMonths = 12,
            teethStatus = "TWO_TEETH",
            pregnancyStatus = "NOT_APPLICABLE",
            rfidCode = "IR-12345",
            vetTrackingCode = "VET-123",
            province = "قزوین",
            city = "بوئین‌زهرا",
            distanceKm = 20,
            basePricePerKg = 350_000L,
            fixedPrice = 0L,
            imageRes = "sample_dorper_sheep",
            sellerName = "علی",
            sellerPhone = "0912"
        )
        val calculatedPrice = PricingEngine.calculateEffectivePrice(sheep)
        // 80.0 * 350,000 = 28,000,000
        assertEquals(28_000_000L, calculatedPrice)
    }

    @Test
    fun testPermittedLossRateCalculation() {
        // 100 km -> 1.5 + (100/100)*0.8 = 2.3%
        val rate100 = PricingEngine.calculatePermittedLossRate(100)
        assertEquals(2.3, rate100, 0.05)

        // 300 km -> 1.5 + (300/100)*0.8 = 3.9%
        val rate300 = PricingEngine.calculatePermittedLossRate(300)
        assertEquals(3.9, rate300, 0.05)
    }

    @Test
    fun testWeighbridgeSettlementWithinTolerance() {
        val settlement = PricingEngine.settleEscrowWeighbridge(
            initialWeightKg = 80.0,
            destinationWeightKg = 78.5, // 1.5 kg lost (~1.875% loss, permitted is 2.3%)
            basePricePerKg = 350_000L,
            distanceKm = 100
        )
        assertTrue(settlement.isWithinTolerance)
        assertEquals(28_000_000L, settlement.sellerPayoutToman)
        assertEquals(0L, settlement.buyerRefundToman)
    }
}
