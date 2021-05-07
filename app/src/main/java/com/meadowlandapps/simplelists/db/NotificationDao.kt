package com.meadowlandapps.simplelists.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Update

@Dao
interface NotificationDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertNotification(notification: Notification)

    @Update(onConflict = REPLACE)
    suspend fun updateNotification(notification: Notification)

    @Delete
    suspend fun deleteNotification(notification: Notification)
}