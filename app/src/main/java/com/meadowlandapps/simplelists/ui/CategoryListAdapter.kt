package com.meadowlandapps.simplelists.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.model.CategoryModel

class CategoryListAdapter internal constructor (
    context: Context,
    private val checkedChangeListener: CompoundButton.OnCheckedChangeListener
) : RecyclerView.Adapter<CategoryListAdapter.CategoryViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var categories = emptyList<CategoryModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = inflater.inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.bind(category)
    }

    override fun getItemCount() = categories.size

    internal fun setCategories(categories: List<CategoryModel>) {
        this.categories = categories
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameView: TextView = itemView.findViewById(R.id.category_name)
        private val checkBox: CheckBox = itemView.findViewById(R.id.category_select)

        fun bind(category: CategoryModel) {
            checkBox.isChecked = false // initialize to unchecked
            checkBox.tag = category
            nameView.text = category.name
            checkBox.setOnCheckedChangeListener(checkedChangeListener)
        }
    }
}