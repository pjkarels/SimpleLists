package com.bitsandbogs.simplelists.repository

import com.bitsandbogs.simplelists.db.Notification
import com.bitsandbogs.simplelists.db.ReminderDao
import com.bitsandbogs.simplelists.db.TaskWithType
import com.bitsandbogs.simplelists.model.ItemModel
import com.bitsandbogs.simplelists.model.NotificationModel
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