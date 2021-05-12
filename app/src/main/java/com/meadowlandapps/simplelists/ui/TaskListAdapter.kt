package com.meadowlandapps.simplelists.ui

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.model.ItemModel

class TaskListAdapter internal constructor (
        private val context: Context,
        private val onItemClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var tasks = emptyList<ItemModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView: View = inflater.inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = tasks[position]
        holder.bind(current, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    internal fun setTasks(tasks: List<ItemModel>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(itemView: View) : ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.item_name)
        private val reminderView: ImageView = itemView.findViewById(R.id.item_reminder_set)
        private val completeView: ImageView = itemView.findViewById(R.id.item_complete)
        private val deleteView: ImageView = itemView.findViewById(R.id.item_delete)

        fun bind(item: ItemModel, onItemClickListener: View.OnClickListener) {
            nameView.text = item.name
            nameView.setTextAppearance(if (item.completed) R.style.TextAppearance_MaterialComponents_Subtitle1_ListItem_Completed else R.style.TextAppearance_MaterialComponents_Subtitle1_ListItem)
            nameView.paintFlags = if (item.completed) Paint.STRIKE_THRU_TEXT_FLAG else 0
            itemView.tag = item
            itemView.setOnClickListener(onItemClickListener)

            reminderView.visibility = if (item.notifications.size > 0) View.VISIBLE else View.GONE

            completeView.tag = item
            val icon = if (item.completed) {
                ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_uncomplete_24,
                        context.theme
                )
            } else {
                ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_check_24,
                        context.theme
                )
            }
            icon?.setTint(context.getColor(R.color.grey_light))
            completeView.setImageDrawable(icon)
            completeView.setOnClickListener(onItemClickListener)
            deleteView.tag = item
            deleteView.setOnClickListener(onItemClickListener)
        }
    }
}