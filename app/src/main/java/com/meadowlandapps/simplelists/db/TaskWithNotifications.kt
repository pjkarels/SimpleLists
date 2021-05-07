package com.meadowlandapps.simplelists.db

import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithNotifications(
        @Embedded val task: Task,
        @Relation(
                parentColumn = "id",
                entityColumn = "id",
        ) val notifications: List<Notification>
)
