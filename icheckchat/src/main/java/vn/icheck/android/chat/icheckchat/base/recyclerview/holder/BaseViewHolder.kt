package vn.icheck.android.chat.icheckchat.base.recyclerview.holder

import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.base.view.setGone
import vn.icheck.android.chat.icheckchat.base.view.setVisible

abstract class BaseViewHolder<T>(viewBinding: ViewBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    abstract fun bind(obj: T)

    fun getColor(color: Int): Int{
        return ContextCompat.getColor(itemView.context, color)
    }

    val default: String
        get() {
            return itemView.context.getString(R.string.dang_cap_nhat)
        }

    fun checkNullOrEmpty(textView: AppCompatTextView, dataCheck: String?) {
        textView.text = if (!dataCheck.isNullOrEmpty() && dataCheck != "null") {
            textView.setVisible()
            dataCheck
        } else {
            textView.setGone()
            ""
        }
    }
}