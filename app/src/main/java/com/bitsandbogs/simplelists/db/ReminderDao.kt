package com.bitsandbogs.simplelists.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ReminderDao {

    @Query("SELECT * FROM notification_table")
    suspend fun getNotificationsWithTasks(): List<NotificationWithTasks>
}