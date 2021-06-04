package vn.icheck.android.component.product.certifications

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.screen.user.detail_media.DetailMediaActivity
import vn.icheck.android.util.kotlin.WidgetUtils

class CertificationsAdapter(val listData: List<String>) : RecyclerView.Adapter<CertificationsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent)

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])

        holder.itemView.setOnClickListener {
            ICheckApplication.currentActivity()?.let { activity ->
                val listImg = arrayListOf<String?>()
                for (item in listData) {
                    listImg.add(item)
                }
                DetailMediaActivity.start(activity,listImg)
            }
        }
    }

    class ViewHolder(parent: ViewGroup) : BaseViewHolder<String>(createView(parent.context)) {

        override fun bind(obj: String) {
            WidgetUtils.loadImageUrlNotCrop(itemView as AppCompatImageView, obj, R.drawable.update_product_holder)
        }

        companion object {

            fun createView(context: Context): AppCompatImageView {
                return AppCompatImageView(context).also {
                    it.layoutParams = ViewHelper.createLayoutParams(SizeHelper.size100, SizeHelper.size140).also {params ->
                        params.setMargins(SizeHelper.size10,0,0,0)
                    }
                    it.setBackgroundColor(Constant.getAppBackgroundGrayColor(context))
                }
            }
        }
    }
}