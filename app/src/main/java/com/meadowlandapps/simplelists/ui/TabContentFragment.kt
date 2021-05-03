package com.meadowlandapps.simplelists.ui

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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.meadowlandapps.simplelists.R
import com.meadowlandapps.simplelists.db.Task

class TabContentFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance(type: Int): Fragment {
            val fragment = TabContentFragment()
            val bundle = Bundle()
            bundle.putInt("category", type)
            fragment.arguments = bundle

            return fragment
        }
    }

    private lateinit var listAdapter: TaskListAdapter
    private var category = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        category = arguments?.getInt("category", 0) ?: 0
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
            val action = TabContentFragmentDirections.actionTabContentFragmentToAddTaskFragment(category, 0)
            v.findNavController().navigate(action)
        }

        vm.getTasks(category).observe(viewLifecycleOwner) { value ->
            value.let {
                listAdapter.setTasks(value)
            }
        }
    }

    override fun onClick(v: View) {
        val task = v.tag as Task
        if (v is ImageView) {
            val vm = ViewModelProvider(this).get(TabContentViewModel::class.java)
            when (v.id) {
                R.id.item_delete -> vm.deleteTask(task)
                R.id.item_complete -> vm.updateTaskCompleteness(task)
            }
        } else {
            val action = TabContentFragmentDirections.actionTabContentFragmentToEditTaskFragment(task.id, task.typeId)
            v.findNavController().navigate(action)
        }
    }
}