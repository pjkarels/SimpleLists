package com.playground.navigationwithtabs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.playground.navigationwithtabs.R

/**
 * A simple [Fragment] subclass.
 */
class IntroFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_intro, container, false)
        val nextButton: View = rootView.findViewById(R.id.intro_button_next)

        nextButton.setOnClickListener { v ->
            val action = IntroFragmentDirections.actionIntroFragmentToViewPagerFragment()
            v.findNavController().navigate(action)
        }

        return rootView
    }
}