package com.meadowlandapps.simplelists.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.meadowlandapps.simplelists.db.Task
import com.meadowlandapps.simplelists.db.TaskDao
import com.meadowlandapps.simplelists.db.TaskDetail
import com.meadowlandapps.simplelists.db.TaskType
import com.meadowlandapps.simplelists.db.TaskTypeDao
import com.meadowlandapps.simplelists.model.CategoryModel
import com.meadowlandapps.simplelists.model.ItemModel

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

    suspend fun getCategory(id: Int) = taskTypeDao.getCategory(id).firstOrNull()

    fun tasksForType(id: Int) = taskDao.tasksForType(id)

    suspend fun getItemsForList(id: Int) = taskDao.getItemsForList(id)

    suspend fun getTask(taskId: Int) = taskDao.getTask(taskId).firstOrNull()

    suspend fun insertTask(task: Task) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
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

    suspend fun deleteItemsByIds(itemIds: List<Int>) {
        val items = mutableListOf<Task>()

        itemIds.forEach { item ->
            items.add(Task(item))
        }
        taskDao.deleteItemsByIds(items)
    }

    private fun mapTaskTypeToCategory(type: TaskType) = CategoryModel(type.id, type.name, false)

    private fun mapTaskToItem(task: TaskDetail): ItemModel {
        return ItemModel(
                id = task.id,
                name = task.name,
                typeId = task.typeId,
                category = task.typeString,
                completed = task.completed,
                removed = task.removed
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