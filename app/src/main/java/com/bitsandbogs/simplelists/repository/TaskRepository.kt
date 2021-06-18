package com.bitsandbogs.simplelists.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.bitsandbogs.simplelists.db.Notification
import com.bitsandbogs.simplelists.db.Task
import com.bitsandbogs.simplelists.db.TaskDao
import com.bitsandbogs.simplelists.db.TaskType
import com.bitsandbogs.simplelists.db.TaskTypeDao
import com.bitsandbogs.simplelists.db.TaskWithNotifications
import com.bitsandbogs.simplelists.db.TaskWithType
import com.bitsandbogs.simplelists.model.CategoryModel
import com.bitsandbogs.simplelists.model.ItemModel
import com.bitsandbogs.simplelists.model.NotificationModel
import java.util.*

class TaskRepository(
        private val taskDao: TaskDao,
        private val taskTypeDao: TaskTypeDao
) {

    val taskTypes: LiveData<List<CategoryModel>> =
            taskTypeDao.taskTypes.map { taskTypes ->
                taskTypes.map { taskType ->
                    mapTaskTypeToCategory(taskType)
                }
            }

    val removedItems: LiveData<List<ItemModel>>
        get() {
            return taskDao.removedTasks().map { tasks ->
                tasks.map { task ->
                    mapTaskToItem(task)
                }
            }
        }

    suspend fun getCategories() = taskTypeDao.getCategories().map { taskType ->
        mapTaskTypeToCategory(taskType)
    }

    suspend fun insertTaskType(category: TaskType) {
        taskTypeDao.insertTaskTypes(category)
    }

    suspend fun updateTaskType(category: TaskType) {
        taskTypeDao.updateTaskTypes(category)
    }

    suspend fun getCategory(id: Long) = taskTypeDao.getCategory(id)

    fun tasksForType(id: Long) = taskDao.tasksForType(id).map { tasks ->
        tasks.map { tasksWithNotifications ->
            mapTaskWithNotificationToItem(tasksWithNotifications)
        }
    }

    suspend fun getItemNamesForListId(id: Long) = taskDao.getItemNamesForListId(id)

    suspend fun getTask(taskId: String) = mapTaskWithNotificationToItem(taskDao.getTask(taskId))

    suspend fun insertTask(item: ItemModel) {
        val task = mapItemToTask(item)
        val notifications = mapItemToNotifications(item)

        taskDao.insertTaskAndNotifications(task, notifications)
    }

    suspend fun updateTask(item: ItemModel) {
        val task = mapItemToTask(item)
        val notifications = mapItemToNotifications(item)

        taskDao.updateTaskAndNotifications(task, notifications)
    }

    /**
     * Function called to update to restore deleted items
     * No need to update related Reminders
     */
    suspend fun updateItems(items: List<ItemModel>) {
        val tasks = items.map { itemModel ->
            mapItemToTask(itemModel)
        }
        taskDao.updateTasks(tasks)
    }

    fun deleteLists(listNames: List<String>) {
        for (name in listNames) {
            taskTypeDao.deleteCategory(name)
        }
    }

    suspend fun deleteItemsByIds(itemIds: List<String>) {
        val items = mutableListOf<Task>()

        itemIds.forEach { item ->
            items.add(Task(item))
        }
        taskDao.deleteItemsByIds(items)
    }

    private fun mapTaskTypeToCategory(type: TaskType) = CategoryModel(type.id, type.name, false)

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

    private fun mapTaskWithNotificationToItem(taskWithNotifications: TaskWithNotifications) =
            if (taskWithNotifications == null) {
                ItemModel()
            } else {
                ItemModel(
                        id = taskWithNotifications.task.id,
                        name = taskWithNotifications.task.name,
                        categoryId = taskWithNotifications.task.typeId,
                        completed = taskWithNotifications.task.completed,
                        removed = taskWithNotifications.task.removed,
                        notifications = taskWithNotifications.notifications.map { notification ->
                            mapNotificationToNotificationModel(notification)
                        } as MutableList<NotificationModel>,
                        isNew = false
                )
            }

    private fun mapNotificationToNotificationModel(notification: Notification): NotificationModel {
        val calendar = Calendar.getInstance().apply {
            this.timeInMillis = notification.time
        }

        return NotificationModel(
                id = notification.notificationId,
                time = calendar
        )
    }

    private fun mapItemToTask(item: ItemModel): Task {
        return Task(
                id = item.id,
                name = item.name,
                typeId = item.categoryId,
                completed = item.completed,
                removed = item.removed
        )
    }

    private fun mapItemToNotifications(item: ItemModel): List<Notification> {
        return item.notifications.map { reminder ->
            Notification(
                    notificationId = reminder.id,
                    taskId = item.id,
                    time = reminder.time.timeInMillis
            )
        }
    }
}