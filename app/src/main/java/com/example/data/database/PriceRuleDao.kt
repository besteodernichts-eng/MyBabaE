package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entities.DailyPriceRuleEntity
import com.example.data.entities.PlatformSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceRuleDao {
    @Query("SELECT * FROM daily_price_rules ORDER BY livestockType ASC, breed ASC")
    fun getAllPriceRules(): Flow<List<DailyPriceRuleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: DailyPriceRuleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRules(rules: List<DailyPriceRuleEntity>)

    @Update
    suspend fun updateRule(rule: DailyPriceRuleEntity)

    @Query("UPDATE daily_price_rules SET basePricePerKg = :newPrice, lastUpdatedJalali = :timestamp WHERE id = :id")
    suspend fun updateBasePrice(id: Long, newPrice: Long, timestamp: String)

    @Query("SELECT * FROM platform_settings WHERE id = 1")
    fun getPlatformSettings(): Flow<PlatformSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlatformSettings(settings: PlatformSettingsEntity)
}
