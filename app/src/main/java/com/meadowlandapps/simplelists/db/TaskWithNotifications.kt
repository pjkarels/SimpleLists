package com.meadowlandapps.simplelists.db

import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithNotifications(
        @Embedded val task: Task,
        @Relation(
                parentColumn = "id",
                entityColumn = "taskId",
        ) val notifications: MutableList<Notification>
)
