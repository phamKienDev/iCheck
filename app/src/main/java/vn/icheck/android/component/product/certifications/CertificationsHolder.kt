package vn.icheck.android.component.product.certifications

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper

class CertificationsHolder(parent: ViewGroup, viewPool: RecyclerView.RecycledViewPool?) : RecyclerView.ViewHolder(createView(parent.context, viewPool)) {

    fun bind(obj: CertificationsModel) {
        (itemView as ViewGroup).run {
            // Text title
            (getChildAt(0) as AppCompatTextView).run {
                setText(R.string.chung_chi_va_chung_nhan)
            }

            // List
            (getChildAt(1) as RecyclerView).run {
                adapter = CertificationsAdapter(obj.certificates)
            }
        }
    }

    companion object {

        fun createView(context: Context, recycledViewPool: RecyclerView.RecycledViewPool?): LinearLayout {
            return LinearLayout(context).also { layoutParent ->
                layoutParent.layoutParams = ViewHelper.createLayoutParams(0,SizeHelper.size10,0,0)
                layoutParent.orientation = LinearLayout.VERTICAL
                layoutParent.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

                // Text title
                layoutParent.addView(ViewHelper.createTitle(context))

                // List
                layoutParent.addView(RecyclerView(context).also {
                    it.setRecycledViewPool(recycledViewPool)
                    it.layoutParams = ViewHelper.createLayoutParams()
                    it.setPadding(SizeHelper.size7, 0, SizeHelper.size7, SizeHelper.size10)
                    it.clipToPadding = false
                    it.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false).apply {
                        initialPrefetchItemCount = 4
                    }
                })
            }
        }
    }
}