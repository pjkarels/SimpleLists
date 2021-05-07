package com.meadowlandapps.simplelists.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.model.ItemModel

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
        val restoreButton: Button = view.findViewById(R.id.button_restore_items)
        val deleteButton: Button = view.findViewById(R.id.button_delete_items)

        val listAdapter = ItemRecyclerViewAdapter(this)
        val recyclerView: RecyclerView = view.findViewById(R.id.items_list)
        recyclerView.adapter = listAdapter

        vm.removedItems.observe(viewLifecycleOwner) { items ->
            items?.let {
                listAdapter.setListItems(items)
                vm.selectedItems.clear()
                vm.onCheckedChanged()
            }
            val visibility =
                    if (items.isNullOrEmpty()) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
            restoreButton.visibility = visibility
            deleteButton.visibility = visibility
        }

        vm.enableButtons.observe(viewLifecycleOwner) { enabled ->
            restoreButton.isEnabled = enabled
            deleteButton.isEnabled = enabled
        }

        restoreButton.setOnClickListener {
            vm.restoreSelectedItems()
        }

        deleteButton.setOnClickListener {
            val itemIds = vm.selectedItems.map { item ->
                item.id
            }
            val action =
                DeletedItemsFragmentDirections.actionDeletedItemsFragmentToDeleteItemsConfirmDialogFragment(
                        itemIds.toLongArray()
                )
            requireView().findNavController().navigate(action)
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        val item = buttonView?.tag as ItemModel
        vm.checkedChanged(isChecked, item)
    }
}