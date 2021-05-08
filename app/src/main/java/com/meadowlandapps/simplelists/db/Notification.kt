package com.meadowlandapps.simplelists.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "notification_table",
        foreignKeys = [
            ForeignKey(
                    entity = Task::class,
                    parentColumns = arrayOf("id"),
                    childColumns = arrayOf("taskId"),
                    onDelete = ForeignKey.CASCADE,
                    onUpdate = ForeignKey.CASCADE
            )
        ])
data class Notification(
        @PrimaryKey val notificationId: String,
        @ColumnInfo(name = "taskId") val taskId: String,
        @ColumnInfo(name = "time") var time: Long
)
