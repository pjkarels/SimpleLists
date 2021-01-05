package com.playground.navigationwithtabs.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.playground.navigationwithtabs.R
import com.playground.navigationwithtabs.db.Task

class TaskListAdapter internal constructor (
        context: Context,
        private val onItemClickListener: View.OnClickListener
) :
    RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var tasks = emptyList<Task>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView: View = inflater.inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val current = tasks[position]

        holder.nameView.text = current.name
        holder.bind(current, onItemClickListener)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    internal fun setTasks(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(itemView: View) : ViewHolder(itemView) {
        val nameView: TextView = itemView.findViewById(R.id.item_name)
        val deleteView: ImageView = itemView.findViewById(R.id.item_delete)

        fun bind(task: Task, onItemClickListener: View.OnClickListener) {
            itemView.tag = task
            itemView.setOnClickListener(onItemClickListener)
            deleteView.tag = task
            deleteView.setOnClickListener(onItemClickListener)
        }
    }
}