package vn.icheck.android.loyalty.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.helper.TimeHelper

abstract class BaseViewHolder<T>(getLayoutID: Int, parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(getLayoutID, parent, false)) {

    abstract fun bind(obj: T)

    fun getString(string: Int): String {
        return itemView.context.getString(string)
    }

    fun getColor(color: Int): Int{
        return ContextCompat.getColor(itemView.context, color)
    }

    val default: String
        get() {
            return itemView.context.getString(R.string.dang_cap_nhat)
        }

    fun checkNullOrEmpty(textView: AppCompatTextView, dataCheck: String?) {
        textView.text = if (!dataCheck.isNullOrEmpty()) {
            dataCheck
        } else {
            default
        }
    }
    
    fun checkNullOrEmptyConvertDateTimeSvToTimeDateVn(textView: AppCompatTextView, dataCheck: String?){
        textView.text = if (!dataCheck.isNullOrEmpty()) {
            TimeHelper.convertDateTimeSvToTimeDateVn(dataCheck)
        } else {
            default
        }
    }
}