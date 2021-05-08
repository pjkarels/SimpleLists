package com.meadowlandapps.simplelists.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface NotificationDao {

    @Insert(onConflict = REPLACE)
    suspend fun upsertNotifications(notifications: List<Notification>)

    @Delete
    suspend fun deleteNotification(notifications: List<Notification>)
}