package com.playground.navigationwithtabs.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task (
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "name") var name: String,
        @ColumnInfo(name = "type") val type: String,
        @ColumnInfo(name = "completed") var completed: Boolean
)