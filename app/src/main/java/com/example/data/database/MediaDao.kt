package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entities.MediaItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    @Query("SELECT * FROM media_items ORDER BY id DESC")
    fun getAllMedia(): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_items WHERE livestockId = :livestockId")
    fun getMediaForLivestock(livestockId: Long): Flow<List<MediaItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: MediaItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mediaList: List<MediaItemEntity>)

    @Update
    suspend fun updateMedia(media: MediaItemEntity)

    @Query("UPDATE media_items SET status = :status, rejectionReason = :reason WHERE id = :id")
    suspend fun updateMediaStatus(id: Long, status: String, reason: String?)
}
