package com.bitsandbogs.simplelists.receiver

import BUNDLE_KEY_CATEGORY_NAME
import BUNDLE_KEY_ITEM_ID
import BUNDLE_KEY_NOTIFICATION_ID
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.bitsandbogs.simplelists.db.AppDatabase
import com.bitsandbogs.simplelists.repository.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class DeviceBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            Log.d(DeviceBootReceiver::class.java.simpleName, "Receiving Boot Broadcast")
            // Set the alarms here.
            val db = AppDatabase.getDatabase(context)
            val reminderRepository = ReminderRepository(db.notificationDao())

            MainScope().launch(Dispatchers.IO) {
                val notifications = reminderRepository.getNotifications()
                notifications.forEach { notificationWithTasks ->
                    val task = notificationWithTasks.tasks.first()
                    val reminderId = notificationWithTasks.notification.notificationId
                    val itemId = task.id
                    val categoryName = task.typeString
                    Log.d(
                        DeviceBootReceiver::class.java.simpleName,
                        "ReminderId: $reminderId, Task Name: ${task.name}, Category Name: ${task.typeString}"
                    )
                    Log.d(
                        DeviceBootReceiver::class.java.simpleName,
                        "Is Completed: ${task.completed}"
                    )
                    Log.d(DeviceBootReceiver::class.java.simpleName, "Is Removed: ${task.removed}")
                    MainScope().launch(Dispatchers.Main) {
                        val alarmMgr =
                            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val alarmIntent =
                            Intent(context, AlarmReceiver::class.java).let { sendIntent ->
                                sendIntent.putExtra(BUNDLE_KEY_ITEM_ID, itemId)
                                sendIntent.putExtra(BUNDLE_KEY_CATEGORY_NAME, categoryName)
                                sendIntent.putExtra(BUNDLE_KEY_NOTIFICATION_ID, reminderId)
                                sendIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
                                PendingIntent.getBroadcast(
                                    context,
                                    reminderId.hashCode(),
                                    sendIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                                )
                            }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            alarmMgr.setExactAndAllowWhileIdle(
                                AlarmManager.RTC_WAKEUP,
                                notificationWithTasks.notification.time,
                                alarmIntent
                            )
                        } else {
                            alarmMgr.setExact(
                                AlarmManager.RTC_WAKEUP,
                                notificationWithTasks.notification.time,
                                alarmIntent
                            )
                        }

                        Log.d(
                            DeviceBootReceiver::class.java.simpleName,
                            "Adding alarm with id: $reminderId"
                        )
                    }
                }
            }
        }
    }
}