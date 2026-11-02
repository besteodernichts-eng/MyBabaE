package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entities.LivestockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LivestockDao {
    @Query("SELECT * FROM livestock ORDER BY id DESC")
    fun getAllLivestock(): Flow<List<LivestockEntity>>

    @Query("SELECT * FROM livestock WHERE id = :id")
    suspend fun getLivestockById(id: Long): LivestockEntity?

    @Query("SELECT * FROM livestock WHERE isLiveAuction = 1 ORDER BY auctionEndsInMinutes ASC")
    fun getLiveAuctions(): Flow<List<LivestockEntity>>

    @Query("SELECT * FROM livestock WHERE breed = :breed")
    suspend fun getLivestockByBreed(breed: String): List<LivestockEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLivestock(item: LivestockEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<LivestockEntity>)

    @Update
    suspend fun updateLivestock(item: LivestockEntity)

    @Query("UPDATE livestock SET basePricePerKg = :newPricePerKg WHERE breed = :breed AND pricingType = 'BY_WEIGHT'")
    suspend fun updateBasePriceForBreed(breed: String, newPricePerKg: Long): Int

    @Query("UPDATE livestock SET auctionCurrentBid = :newBid, auctionBidsCount = auctionBidsCount + 1 WHERE id = :id")
    suspend fun placeAuctionBid(id: Long, newBid: Long)

    @Query("UPDATE livestock SET isSynced = 1 WHERE isSynced = 0")
    suspend fun markAllSynced()

    @Query("SELECT COUNT(*) FROM livestock WHERE isSynced = 0")
    fun getUnsyncedCount(): Flow<Int>
}
