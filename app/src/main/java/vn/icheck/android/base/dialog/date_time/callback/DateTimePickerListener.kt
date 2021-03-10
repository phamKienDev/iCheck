package vn.icheck.android.base.dialog.date_time.callback

/**
 * Created by lecon on 11/26/2017
 */
interface DateTimePickerListener {

    fun onSelected(dateTime : String, milliseconds : Long)
}