package vn.icheck.android.component.`null`

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class NullHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        View(parent.context).also {
            it.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
)