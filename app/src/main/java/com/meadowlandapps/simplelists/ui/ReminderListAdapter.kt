package com.meadowlandapps.simplelists.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.model.NotificationModel
import java.text.DateFormat

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
        private val date: TextView = itemView.findViewById(R.id.reminder_date)

        fun bind(reminder: NotificationModel) {
            itemView.tag = reminder
            date.text = DateFormat.getTimeInstance().format(reminder.time.time)
            removeReminderView.setOnClickListener(onItemClickListener)
        }
    }
}