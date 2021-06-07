package com.meadowlandapps.simplelists.db

import androidx.room.Embedded
import androidx.room.Relation

data class NotificationWithTasks(
    @Embedded
    val notification: Notification,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "id",
    ) val tasks: MutableList<TaskWithType>
)