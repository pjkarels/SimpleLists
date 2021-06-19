package com.bitsandbogs.simplelists.ui

import BUNDLE_KEY_CATEGORY_ID
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bitsandbogs.simplelists.R
import com.bitsandbogs.simplelists.model.ItemModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TabContentFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance(type: Long): Fragment {
            val fragment = TabContentFragment()
            val bundle = Bundle()
            bundle.putLong(BUNDLE_KEY_CATEGORY_ID, type)
            fragment.arguments = bundle

            return fragment
        }
    }

    private lateinit var listAdapter: TaskListAdapter
    private var categoryId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        categoryId = arguments?.getLong(BUNDLE_KEY_CATEGORY_ID, 0) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tab_content, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm = ViewModelProvider(this).get(TabContentViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.task_list_view)
        listAdapter = TaskListAdapter(requireContext(), this)
        recyclerView.adapter = listAdapter

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { v ->
            val action = TabContentFragmentDirections.actionTabContentFragmentToAddTaskFragment(categoryId, "")
            v.findNavController().navigate(action)
        }

        vm.getTasks(categoryId).observe(viewLifecycleOwner) { value ->
            value.let {
                listAdapter.setTasks(value)
            }
        }
    }

    override fun onClick(v: View) {
        val item = v.tag as ItemModel
        if (v is ImageView) {
            val vm = ViewModelProvider(this).get(TabContentViewModel::class.java)
            when (v.id) {
                R.id.item_delete -> vm.deleteTask(item)
                R.id.item_complete -> vm.updateTaskCompleteness(item)
            }
        } else {
            val action = TabContentFragmentDirections.actionTabContentFragmentToEditTaskFragment(item.id, item.categoryId)
            v.findNavController().navigate(action)
        }
    }
}