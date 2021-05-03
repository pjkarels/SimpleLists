package com.meadowlandapps.simplelists.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface TaskTypeDao {

    @get: Query("SELECT * FROM task_type_table")
    val taskTypes: LiveData<List<TaskType>>

    @Query("SELECT * FROM task_type_table WHERE :id LIKE id")
    suspend fun getCategory(id: Int): List<TaskType>

    @Insert(onConflict = REPLACE)
    suspend fun insertTaskTypes(vararg types: TaskType)

    @Query("DELETE FROM task_type_table WHERE :type LIKE name")
    fun deleteCategory(type: String)

    @Delete
    suspend fun deleteCategories(categories: List<TaskType>)
}