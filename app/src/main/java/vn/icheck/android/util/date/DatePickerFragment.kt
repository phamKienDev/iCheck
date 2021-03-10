package vn.icheck.android.util.date

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePickerFragment(val onDatePicked: OnDatePicked, val calendar: Calendar, val enableFuture:Boolean = false): DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Use the current date as the default date in the picker
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireActivity(), this, year, month, day).apply {
            if (!enableFuture) {
                datePicker.maxDate = System.currentTimeMillis()
            }
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
        onDatePicked.picked(year, month, day)
    }
}

interface OnDatePicked{
    fun picked(year: Int, month: Int, day: Int)
}