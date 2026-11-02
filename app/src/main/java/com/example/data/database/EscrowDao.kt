package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entities.EscrowOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EscrowDao {
    @Query("SELECT * FROM escrow_orders ORDER BY id DESC")
    fun getAllOrders(): Flow<List<EscrowOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: EscrowOrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<EscrowOrderEntity>)

    @Update
    suspend fun updateOrder(order: EscrowOrderEntity)

    @Query("UPDATE escrow_orders SET status = :status, destinationWeightKg = :destWeight, actualLossRate = :lossRate, adjustedAmountToman = :adjustedAmount WHERE id = :id")
    suspend fun settleWeighbridgeOrder(
        id: Long,
        status: String,
        destWeight: Double,
        lossRate: Double,
        adjustedAmount: Long
    )
}
