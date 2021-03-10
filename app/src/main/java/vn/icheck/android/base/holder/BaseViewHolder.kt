package vn.icheck.android.base.holder

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(obj: T)

    fun getString(idString: Int): String {
        return itemView.context.getString(idString)
    }

    fun getColor(idColor: Int): Int{
        return ContextCompat.getColor(itemView.context, idColor)
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