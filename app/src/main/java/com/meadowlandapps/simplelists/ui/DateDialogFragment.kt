package com.meadowlandapps.simplelists.ui

import BUNDLE_KEY_DATE
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import java.util.*

class DateDialogFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private val args: DateDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val vm = ViewModelProvider(this).get(DateDialogViewModel::class.java)
        val date = args.date
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        vm.date = calendar

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireActivity(), this, year, month, dayOfMonth)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val vm = ViewModelProvider(this).get(DateDialogViewModel::class.java)
        val calendar = vm.date

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        findNavController().previousBackStackEntry?.savedStateHandle?.set(BUNDLE_KEY_DATE, calendar.timeInMillis)
    }
}