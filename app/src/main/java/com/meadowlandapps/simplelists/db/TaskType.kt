package com.meadowlandapps.simplelists.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_type_table")
data class TaskType(
        @PrimaryKey(autoGenerate = true) val id: Int,
        @ColumnInfo(name = "name") var name: String)