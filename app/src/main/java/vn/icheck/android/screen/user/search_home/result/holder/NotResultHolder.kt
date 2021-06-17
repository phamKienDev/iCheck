package vn.icheck.android.screen.user.search_home.result.holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_not_result_holder.view.*
import vn.icheck.android.R

class NotResultHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.layout_not_result_holder, parent, false)) {

    fun setListener(listener:View.OnClickListener?) {
        itemView.tvCreate.apply {
            background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(context)
            setOnClickListener {
//            listener?.onClick(itemView.tvCreate)
            }
        }
    }
}