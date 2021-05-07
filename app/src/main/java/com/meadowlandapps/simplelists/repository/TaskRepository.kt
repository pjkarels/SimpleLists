package com.meadowlandapps.simplelists.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.meadowlandapps.simplelists.db.Notification
import com.meadowlandapps.simplelists.db.Task
import com.meadowlandapps.simplelists.db.TaskDao
import com.meadowlandapps.simplelists.db.TaskType
import com.meadowlandapps.simplelists.db.TaskTypeDao
import com.meadowlandapps.simplelists.db.TaskWithNotifications
import com.meadowlandapps.simplelists.db.TaskWithType
import com.meadowlandapps.simplelists.model.CategoryModel
import com.meadowlandapps.simplelists.model.ItemModel
import com.meadowlandapps.simplelists.model.NotificationModel
import java.util.*

class TaskRepository(private val taskDao: TaskDao, private val taskTypeDao: TaskTypeDao) {

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

    suspend fun getTask(taskId: Long) = mapTaskWithNotificationToItem(taskDao.getTask(taskId))

    suspend fun insertTask(item: ItemModel) {
        taskDao.insertTask(mapItemToTask(item))
    }

    suspend fun updateTask(item: ItemModel) {
        taskDao.updateTask(mapItemToTask(item))
    }


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

    suspend fun deleteItemsByIds(itemIds: List<Long>) {
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
                typeId = task.typeId,
                category = task.typeString,
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
                        typeId = taskWithNotifications.task.typeId,
                        completed = taskWithNotifications.task.completed,
                        removed = taskWithNotifications.task.removed,
                        notification = taskWithNotifications.notifications.map { notification ->
                            mapNotificationToNotificationModel(notification)
                        }
                )
            }

    private fun mapNotificationToNotificationModel(notification: Notification): NotificationModel {
        val calendar = Calendar.getInstance().apply {
            this.timeInMillis = notification.time
        }

        return NotificationModel(
                id = notification.id,
                time = calendar
        )
    }

    private fun mapItemToTask(item: ItemModel): Task {
        return Task(
                id = item.id,
                name = item.name,
                typeId = item.typeId,
                completed = item.completed,
                removed = item.removed
        )
    }
}