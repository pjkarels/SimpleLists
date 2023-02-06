package com.bitsandbogs.simplelists.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskTypeDao {

    @get: Query("SELECT * FROM task_type_table")
    val taskTypes: LiveData<List<TaskType>>

    @Query("SELECT * FROM task_type_table")
    suspend fun getCategories(): List<TaskType>

    @Query("SELECT * FROM task_type_table WHERE :id LIKE id LIMIT 1")
    suspend fun getCategory(id: Long): TaskType

    @Insert(onConflict = REPLACE)
    suspend fun insertTaskTypes(vararg types: TaskType)

    @Query("DELETE FROM task_type_table WHERE :type LIKE name")
    fun deleteCategory(type: String)

    @Delete
    suspend fun deleteCategories(categories: List<TaskType>)

    @Update(onConflict = REPLACE)
    suspend fun updateTaskTypes(vararg types: TaskType)
}