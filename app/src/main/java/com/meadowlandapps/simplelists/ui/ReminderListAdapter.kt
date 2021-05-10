package com.meadowlandapps.simplelists.ui

import DATE_FORMAT
import TIME_FORMAT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.model.NotificationModel
import java.text.SimpleDateFormat

class ReminderListAdapter(
        private val onItemClickListener: View.OnClickListener
) : RecyclerView.Adapter<ReminderListAdapter.ReminderViewHolder>() {

    private var reminders = emptyList<NotificationModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]

        holder.bind(reminder)
    }

    override fun getItemCount(): Int {
        return reminders.size
    }

    internal fun setReminders(reminders: List<NotificationModel>) {
        this.reminders = reminders
        notifyDataSetChanged()
    }

    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val removeReminderView: ImageButton = itemView.findViewById(R.id.reminder_button_remove)
        private val dateView: TextView = itemView.findViewById(R.id.reminder_date)
        private val timeView: TextView = itemView.findViewById(R.id.reminder_time)

        fun bind(reminder: NotificationModel) {
            val dateFormat = SimpleDateFormat(DATE_FORMAT)
            val timeFormat = SimpleDateFormat(TIME_FORMAT)
            dateView.text = dateFormat.format(reminder.time.time)
            timeView.text = timeFormat.format(reminder.time.time)

            removeReminderView.tag = reminder
            dateView.tag = reminder
            timeView.tag = reminder

            removeReminderView.setOnClickListener(onItemClickListener)
            dateView.setOnClickListener(onItemClickListener)
            timeView.setOnClickListener(onItemClickListener)
        }
    }
}