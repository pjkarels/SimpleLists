package com.meadowlandapps.simplelists.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "task_table",
        foreignKeys = [
            ForeignKey(
                    entity = TaskType::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("typeId"),
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE
            )
        ]
)
data class Task(
        @PrimaryKey(autoGenerate = true) val id: Long,
        @ColumnInfo(name = "name") var name: String = "",
        @ColumnInfo(name = "typeId") var typeId: Long = 0,
        @ColumnInfo(name = "notificationId") var notificationId: Long = 0,
        @ColumnInfo(name = "completed") var completed: Boolean = false,
        @ColumnInfo(name = "removed") var removed: Boolean = false
)