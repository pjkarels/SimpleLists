package com.meadowlandapps.simplelists.repository

import com.meadowlandapps.simplelists.db.Notification
import com.meadowlandapps.simplelists.db.ReminderDao
import com.meadowlandapps.simplelists.db.TaskWithType
import com.meadowlandapps.simplelists.model.ItemModel
import com.meadowlandapps.simplelists.model.NotificationModel
import java.util.*

class ReminderRepository(private val dao: ReminderDao) {

    suspend fun getNotifications() = dao.getNotificationsWithTasks()

    private fun mapNotificationToNotificationModel(notification: Notification): NotificationModel {
        val calendar = Calendar.getInstance().apply {
            this.timeInMillis = notification.time
        }

        return NotificationModel(
            id = notification.notificationId,
            time = calendar
        )
    }

    private fun mapTaskToItem(task: TaskWithType): ItemModel {
        return ItemModel(
            id = task.id,
            name = task.name,
            categoryId = task.typeId,
            categoryName = task.typeString,
            completed = task.completed,
            removed = task.removed,
        )
    }

}