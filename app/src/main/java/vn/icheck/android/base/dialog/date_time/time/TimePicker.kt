package vn.icheck.android.base.dialog.date_time.time

import android.app.TimePickerDialog
import android.content.Context
import vn.icheck.android.helper.TimeHelper
import java.util.*

abstract class TimePicker(val context: Context, private val theme: Int, private val calendar: Calendar) {

    fun show() {
        TimePickerDialog(context, theme, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)

            val mHour = TimeHelper.getNumberWithTwoCharacter(calendar.get(Calendar.HOUR_OF_DAY))
            val mMinute = TimeHelper.getNumberWithTwoCharacter(calendar.get(Calendar.MINUTE))

            if (view.isShown)
                onSelected("$mHour:$mMinute", calendar.timeInMillis)

        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    protected abstract fun onSelected(time: String, milliseconds: Long)
}