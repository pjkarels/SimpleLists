package com.meadowlandapps.simplelists.ui

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import java.util.*
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE

class TimeDialogFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    private val args: TimeDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val time = args.time
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time

        val hour = calendar.get(HOUR_OF_DAY)
        val minute = calendar.get(MINUTE)

        return TimePickerDialog(activity, this, hour, minute, false)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {

    }
}