package vn.icheck.android.util.ick

import android.app.Activity
import android.content.Context
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment

fun TextView.rText(rString: Int, vararg formatArgs: Any?) {
    apply {
        text = context.getString(rString, *formatArgs)
    }
}

infix fun TextView.rText(rString: Int) {
    apply {
        text = context.getString(rString)
    }
}

fun TextView.rHintText(rString: Int, vararg formatArgs: Any?) {
    apply {
        hint = context.getString(rString, *formatArgs)
    }
}

infix fun TextView.rHintText(rString: Int) {
    apply {
        hint = context.getString(rString)
    }
}

fun AppCompatTextView.rText(rString: Int, vararg formatArgs: Any?) {
    apply {
        text = context.getString(rString, *formatArgs)
    }
}

infix fun AppCompatTextView.rText(rString: Int) {
    apply {
        text = context.getString(rString)
    }
}

fun Context.rText(rString: Int, vararg formatArgs: Any?): String {
    return getString(rString, *formatArgs)
}

infix fun Context.rText(rString: Int): String {
    return getString(rString)
}

fun Activity.rText(rString: Int, vararg formatArgs: Any?): String {
    return getString(rString, *formatArgs)
}

infix fun Activity.rText(rString: Int): String {
    return getString(rString)
}

fun Fragment.rText(rString: Int, vararg formatArgs: Any?): String {
    return getString(rString, *formatArgs)
}

infix fun Fragment.rText(rString: Int): String {
    return getString(rString)
}

object StringConstant{
    const val dang_cap_nhat = "Đang cập nhật"
    const val vua_xong = "Vừa xong"
    const val phut_truoc = "phút trước"
    const val gio_truoc = "giờ trước"
    const val giao_tan_noi = "Giao tận nơi"
    const val nhan_tai_cua_hang = "Nhận tại cửa hàng"
    const val qua_the_cao = "Quà thẻ cào"
    const val qua_tinh_than = "Quà tinh thần"
    const val qua_hien_vat = "Quà hiện vật"
}