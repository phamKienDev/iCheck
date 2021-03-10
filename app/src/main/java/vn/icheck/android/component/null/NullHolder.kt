package vn.icheck.android.component.`null`

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R

class NullHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ctsp_null_holder, parent, false)) {

    companion object {
        fun create(parent: ViewGroup) = NullHolder(parent)
    }
}