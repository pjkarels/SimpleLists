package com.playground.navigationwithtabs.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.playground.navigationwithtabs.R

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
        val rootView = inflater.inflate(R.layout.fragment_tab_content, container, false)

        val recyclerView: RecyclerView = rootView.findViewById(R.id.task_list_view)
        listAdapter = TaskListAdapter(requireContext(), this)
        recyclerView.adapter = listAdapter

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm = ViewModelProvider(this).get(TabContentViewModel::class.java)

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { v ->
            val action = TabContentFragmentDirections.actionTabContentFragmentToAddTaskFragment(type)
            v.findNavController().navigate(action)
        }

        vm.getTasks(type).observe(viewLifecycleOwner) { value ->
            value.let {
                listAdapter.setTasks(value)
            }
        }
    }

    override fun onClick(v: View?) {

    }
}