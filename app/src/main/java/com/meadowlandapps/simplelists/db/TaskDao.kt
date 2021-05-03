package com.meadowlandapps.simplelists.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskDao {

    @Query("SELECT * FROM task_table WHERE typeId LIKE :id AND removed == 0")
    fun tasksForType(id: Int): LiveData<List<Task>>

    @Query("SELECT * FROM task_table WHERE typeId LIKE :id AND removed == 0")
    suspend fun getItemsForList(id: Int): List<Task>

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