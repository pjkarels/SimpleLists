package com.bitsandbogs.simplelists.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.bitsandbogs.simplelists.R
import com.bitsandbogs.simplelists.model.CategoryModel

/**
 * [RecyclerView.Adapter] that can display a [CategoryModel].
 */
class MoveCategoryViewAdapter(
        private var categories: List<CategoryModel>,
        private val clickListener: View.OnClickListener
) : RecyclerView.Adapter<MoveCategoryViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = categories[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = categories.size

    fun setCategories(categories: List<CategoryModel>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val radioButtonView: RadioButton = view.findViewById(R.id.category)
//        private val contentView: TextView = view.findViewById(R.id.content)

        fun bind(item: CategoryModel) {
            radioButtonView.tag = item
            radioButtonView.isChecked = item.selected
            radioButtonView.setOnClickListener(clickListener)
            radioButtonView.text = item.name
        }
    }
}