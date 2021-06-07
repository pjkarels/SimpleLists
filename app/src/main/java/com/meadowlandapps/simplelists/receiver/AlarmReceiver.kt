package com.meadowlandapps.simplelists.receiver

import BUNDLE_KEY_CATEGORY_ID
import BUNDLE_KEY_CATEGORY_NAME
import BUNDLE_KEY_ITEM_ID
import BUNDLE_KEY_NOTIFICATION_ID
import CHANNEL_ID
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.db.AppDatabase
import com.meadowlandapps.simplelists.repository.TaskRepository
import com.meadowlandapps.simplelists.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(AlarmReceiver::class.java.simpleName, "Received broadcast")

        intent?.let {
            val categoryName = intent.getStringExtra(BUNDLE_KEY_CATEGORY_NAME)
            val itemId = intent.getStringExtra(BUNDLE_KEY_ITEM_ID)
            val reminderId = intent.getStringExtra(BUNDLE_KEY_NOTIFICATION_ID)

            Log.d(
                AlarmReceiver::class.java.simpleName,
                "Beginning broadcast with itemId: $itemId, and reminderId: $reminderId"
            )

            itemId?.let {
                val db = AppDatabase.getDatabase(context)
                val repo = TaskRepository(db.taskDao(), db.taskTypeDao())
                MainScope().launch(Dispatchers.IO) {
                    val item = repo.getTask(itemId)
                    Log.d(AlarmReceiver::class.java.simpleName, "Item Name: ${item.name}")
                    Log.d(AlarmReceiver::class.java.simpleName, "Is Completed: ${item.completed}")
                    Log.d(AlarmReceiver::class.java.simpleName, "Is Removed: ${item.removed}")
                    if (item.notifications.map { notificationModel ->
                            notificationModel.id
                        }.contains(reminderId) &&
                        (!item.completed || !item.removed)
                    ) {
                        Log.d(AlarmReceiver::class.java.simpleName, "Notification found in list")
                        val args = Bundle().apply {
                            putLong(BUNDLE_KEY_CATEGORY_ID, item.categoryId)
                            putString(BUNDLE_KEY_ITEM_ID, itemId)
                        }

                        launch(Dispatchers.Main) {
                            Log.d(AlarmReceiver::class.java.simpleName, "Creating Notification")
                            val pendingIntent = NavDeepLinkBuilder(context)
                                .setGraph(R.navigation.app_navigation_graph)
                                .setDestination(R.id.addTaskFragment)
                                .setArguments(args)
                                .setComponentName(MainActivity::class.java)
                                .createPendingIntent()

                            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_check_list_48)
                                .setContentTitle(item.name)
                                .setContentText(categoryName)
                                .setContentIntent(pendingIntent)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true)

                            with(NotificationManagerCompat.from(context)) {
                                Log.d(AlarmReceiver::class.java.simpleName, "Posting Notification")
                                // notificationId is a unique int for each notification that you must define
                                notify(reminderId.hashCode(), builder.build())
                            }
                        }
                    }
                }
            }
        }
    }
}