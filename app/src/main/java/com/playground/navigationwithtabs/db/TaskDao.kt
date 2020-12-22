package com.playground.navigationwithtabs.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TaskDao {

    @Query("SELECT * FROM task_table")
    suspend fun getTasks(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(vararg tasks: Task)
}