package com.playground.navigationwithtabs.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskDao {

    @Query("SELECT * FROM task_table WHERE type LIKE :type")
    fun tasksForType(type: String): LiveData<List<Task>>

    @Query("SELECT * FROM task_table WHERE type LIKE :type")
    suspend fun getTasks(type: String): List<Task>

    @Query("SELECT * FROM task_table WHERE id LIKE :taskId LIMIT 1")
    suspend fun getTask(taskId: Int): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTasks(task: Task)
}