package com.playground.navigationwithtabs.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.playground.navigationwithtabs.R

class TabContentFragment : Fragment() {

    companion object {
        fun newInstance() = TabContentFragment()
    }

    private lateinit var viewModel: TabContentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(this).get(TabContentViewModel::class.java)

        return inflater.inflate(R.layout.fragment_tab_content, container, false)
    }
}