package com.meadowlandapps.simplelists.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM task_table WHERE type LIKE :type AND removed == 0")
    fun tasksForType(type: String): LiveData<List<Task>>

    @Query("SELECT * FROM task_table WHERE type LIKE :listName AND removed == 0")
    suspend fun getItemsForList(listName: String): List<Task>

    @Query("SELECT * FROM task_table WHERE removed == 1")
    fun removedTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task_table WHERE id LIKE :taskId LIMIT 1")
    suspend fun getTask(taskId: Int): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTasks(items: List<Task>)

    @Delete
    suspend fun deleteItems(tasks: List<Task>)
}