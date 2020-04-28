package com.skichrome.oc.easyvgp.view.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener
{
    // =================================
    //              Fields
    // =================================

    private val args: DatePickerFragmentArgs by navArgs()

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_MONTH]
        val month = calendar[Calendar.MONTH]
        val year = calendar[Calendar.YEAR]

        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(datePicker: DatePicker?, year: Int, month: Int, day: Int)
    {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        navigateToSetupFragment(calendar)
    }

    // =================================
    //              Methods
    // =================================

    private fun navigateToSetupFragment(date: Calendar)
    {
        val opt = DatePickerFragmentDirections.actionDateSelectorFragmentToNewVgpSetupFragment(
            machineId = args.machineId,
            machineTypeId = args.machineTypeId,
            customerId = args.customerId,
            reportDateToEdit = args.reportDateToEdit,
            reportDateFromDatePicker = date.timeInMillis
        )
        findNavController().navigate(opt)
    }
}