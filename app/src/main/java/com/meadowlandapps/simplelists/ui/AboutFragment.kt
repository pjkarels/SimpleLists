package com.meadowlandapps.simplelists.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.meadowlandapps.simplelists.BuildConfig
import com.meadowlandapps.simplelists.R

class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val versionValueView: TextView = view.findViewById(R.id.about_version_value)

        versionValueView.text = BuildConfig.VERSION_CODE.toString()
    }
}