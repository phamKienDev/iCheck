package vn.icheck.android.base.holder

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.ViewHelper

abstract class BaseViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(obj: T)

    fun getString(idString: Int): String {
        return itemView.context.getString(idString)
    }

    fun getColor(idColor: Int): Int{
        return if (idColor!= R.color.colorPrimary) {
            ContextCompat.getColor(itemView.context, idColor)
        } else {
            Constant.getPrimaryColor(itemView.context)
        }
    }

//    fun click(view: View?, call: () -> Unit) {
//        click(view, 1000, call)
//    }
//
//    fun click(view: View?, millisecond: Long, call: () -> Unit) {
//        call.invoke()
//
//        view?.isClickable = false
//        Handler(Looper.getMainLooper()).postDelayed({
//            view?.isClickable = true
//        }, millisecond)
//    }
}