package vn.icheck.android.base.dialog.date_time.date

import android.app.DatePickerDialog
import android.content.Context
import java.util.*

abstract class DatePicker(val context: Context, private val theme: Int, private val calendar: Calendar,
                          private val minDate: Long? = null, private val maxDate: Long? = null) {

    fun show() {
        val dialog = DatePickerDialog(context, theme, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            var time = if (dayOfMonth > 9) dayOfMonth.toString() else "0$dayOfMonth"
            time += "/" + if (month > 8) (month + 1).toString() else "0" + (month + 1)
            time += "/$year"

            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.YEAR, year)

            if (view.isShown)
                onSelected(time, calendar.timeInMillis)

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        minDate?.let {
            dialog.datePicker.minDate = it
        }

        maxDate?.let {
            dialog.datePicker.maxDate = it
        }


        dialog.show()
    }

    protected abstract fun onSelected(date: String, milliseconds: Long)
}