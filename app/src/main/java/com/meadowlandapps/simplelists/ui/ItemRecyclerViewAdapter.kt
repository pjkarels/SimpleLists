package com.meadowlandapps.simplelists.ui

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.db.Task

/**
 * [RecyclerView.Adapter] that can display a [Task].
 */
class ItemRecyclerViewAdapter(private val checkedChangeListener: CompoundButton.OnCheckedChangeListener)
    : RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder>() {

    private var items: List<Task> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    internal fun setItems(items: List<Task>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameView: TextView = itemView.findViewById(R.id.category_name)
        private val checkBox: CheckBox = itemView.findViewById(R.id.category_select)

        fun bind(item: Task) {
            checkBox.tag = item
            nameView.text = "${item.name} (${item.type})"
            checkBox.setOnCheckedChangeListener(checkedChangeListener)
        }
    }
}