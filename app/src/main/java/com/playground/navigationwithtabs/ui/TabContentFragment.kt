package com.playground.navigationwithtabs.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.playground.navigationwithtabs.R
import com.playground.navigationwithtabs.db.Task

class TabContentFragment : Fragment(), View.OnClickListener {

    companion object {
        fun newInstance(type: String): Fragment {
            val fragment = TabContentFragment()
            val bundle = Bundle()
            bundle.putString("type", type)
            fragment.arguments = bundle

            return fragment
        }
    }

    private lateinit var listAdapter: TaskListAdapter
    private var type = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        type = arguments?.getString("type", "") ?: ""
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
            val action = TabContentFragmentDirections.actionTabContentFragmentToAddTaskFragment(type, 0)
            v.findNavController().navigate(action)
        }

        vm.getTasks(type).observe(viewLifecycleOwner) { value ->
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
            val action = TabContentFragmentDirections.actionTabContentFragmentToEditTaskFragment(task.id, task.type)
            v.findNavController().navigate(action)
        }
    }
}