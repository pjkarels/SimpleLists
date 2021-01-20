package com.meadowlandapps.simplelists.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.db.Task

/**
 * A fragment representing a list of Items.
 */
class DeletedItemsFragment : Fragment(), CompoundButton.OnCheckedChangeListener {

    lateinit var vm: DeletedItemsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProvider(this).get(DeletedItemsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_deleted_items_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val listAdapter = ItemRecyclerViewAdapter(this)
        val recyclerView: RecyclerView = view.findViewById(R.id.items_list)
        recyclerView.adapter = listAdapter

        vm.removedItems.observe(viewLifecycleOwner) { items ->
            items?.let {
                listAdapter.setItems(items)
            }
        }

        val restoreButton: Button = view.findViewById(R.id.button_restore_items)
        val deleteButton: Button = view.findViewById(R.id.button_delete_items)

        restoreButton.setOnClickListener {
            vm.restoreSelectedItems()
        }

        deleteButton.setOnClickListener {
            vm.deleteSelectedItems()
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        val item = buttonView?.tag as Task

        if (!vm.selectedItems.contains(item)) {
            vm.selectedItems.add(item)
        }
        else {
            vm.selectedItems.remove(item)
        }
    }
}