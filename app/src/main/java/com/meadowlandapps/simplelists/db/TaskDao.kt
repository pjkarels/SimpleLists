package com.meadowlandapps.simplelists.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface TaskDao {

    @Transaction
    @Query("SELECT * FROM task_table WHERE typeId LIKE :id AND removed == 0")
    fun tasksForType(id: Long): LiveData<List<TaskWithNotifications>>

    @Query("SELECT name FROM task_table WHERE typeId LIKE :id AND removed == 0")
    suspend fun getItemNamesForListId(id: Long): List<String>

    @Query("SELECT * FROM taskwithtype WHERE removed == 1")
    fun removedTasks(): LiveData<List<TaskWithType>>

    @Transaction
    @Query("SELECT * FROM task_table WHERE id LIKE :taskId LIMIT 1")
    suspend fun getTask(taskId: String): TaskWithNotifications

    @Insert(onConflict = REPLACE)
    suspend fun insertTask(task: Task)

    @Insert(onConflict = REPLACE)
    suspend fun insertTasks(items: List<Task>)

    @Insert(onConflict = REPLACE)
    suspend fun updateTask(task: Task)

    @Update(onConflict = REPLACE)
    suspend fun updateTasks(items: List<Task>)

    @Delete
    suspend fun deleteItems(tasks: List<Task>)

    @Delete
    suspend fun deleteItemsByIds(itemIds: List<Task>)
}