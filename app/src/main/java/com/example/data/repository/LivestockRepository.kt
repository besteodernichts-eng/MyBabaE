package com.example.data.repository

import com.example.data.database.AppDatabase
import com.example.data.entities.DailyPriceRuleEntity
import com.example.data.entities.EscrowOrderEntity
import com.example.data.entities.LivestockEntity
import com.example.data.entities.MediaItemEntity
import com.example.data.entities.PlatformSettingsEntity
import com.example.engine.PricingEngine
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LivestockRepository(private val database: AppDatabase) {

    val allLivestock: Flow<List<LivestockEntity>> = database.livestockDao().getAllLivestock()
    val liveAuctions: Flow<List<LivestockEntity>> = database.livestockDao().getLiveAuctions()
    val allPriceRules: Flow<List<DailyPriceRuleEntity>> = database.priceRuleDao().getAllPriceRules()
    val allMedia: Flow<List<MediaItemEntity>> = database.mediaDao().getAllMedia()
    val allEscrowOrders: Flow<List<EscrowOrderEntity>> = database.escrowDao().getAllOrders()
    val unsyncedCount: Flow<Int> = database.livestockDao().getUnsyncedCount()
    val platformSettings: Flow<PlatformSettingsEntity?> = database.priceRuleDao().getPlatformSettings()

    suspend fun getLivestockById(id: Long): LivestockEntity? {
        return database.livestockDao().getLivestockById(id)
    }

    /**
     * Automatic Global Recalculation:
     * When Admin updates base price per kg for a breed, this updates the price rule AND
     * immediately updates all existing listings of that breed sold by weight!
     */
    suspend fun updateBasePriceGlobally(ruleId: Long, breed: String, newPricePerKg: Long) {
        val sdf = SimpleDateFormat("امروز - HH:mm", Locale.getDefault())
        val timestamp = sdf.format(Date())
        database.priceRuleDao().updateBasePrice(ruleId, newPricePerKg, timestamp)
        database.livestockDao().updateBasePriceForBreed(breed, newPricePerKg)
    }

    suspend fun addNewLivestock(item: LivestockEntity): Long {
        val insertedId = database.livestockDao().insertLivestock(item)
        // Auto-create initial media entry for admin review workbench
        database.mediaDao().insertMedia(
            MediaItemEntity(
                livestockId = insertedId,
                mediaType = "FULL",
                title = "تصویر اصلی ${item.title}",
                imageResName = item.imageRes,
                isWatermarked = true,
                status = if (item.isOfflineCreated) "PENDING" else "APPROVED"
            )
        )
        return insertedId
    }

    suspend fun placeAuctionBid(livestockId: Long, bidAmount: Long) {
        database.livestockDao().placeAuctionBid(livestockId, bidAmount)
    }

    suspend fun syncOfflineListings() {
        database.livestockDao().markAllSynced()
    }

    suspend fun reviewMedia(mediaId: Long, isApproved: Boolean, reason: String?) {
        val status = if (isApproved) "APPROVED" else "REJECTED"
        database.mediaDao().updateMediaStatus(mediaId, status, reason)
    }

    suspend fun createEscrowOrder(order: EscrowOrderEntity): Long {
        return database.escrowDao().insertOrder(order)
    }

    suspend fun settleWeighbridge(
        orderId: Long,
        destinationWeightKg: Double,
        order: EscrowOrderEntity
    ) {
        val settlement = PricingEngine.settleEscrowWeighbridge(
            initialWeightKg = order.initialWeightKg,
            destinationWeightKg = destinationWeightKg,
            basePricePerKg = if (order.initialWeightKg > 0) (order.totalAmountToman / order.initialWeightKg).toLong() else 350_000L,
            distanceKm = order.distanceKm
        )
        val finalStatus = if (settlement.isWithinTolerance) "COMPLETED" else "DISPUTED"
        database.escrowDao().settleWeighbridgeOrder(
            id = orderId,
            status = finalStatus,
            destWeight = destinationWeightKg,
            lossRate = settlement.actualLossRate,
            adjustedAmount = settlement.sellerPayoutToman
        )
    }
}
