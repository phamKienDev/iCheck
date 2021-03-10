package vn.icheck.android.screen.user.mall.holder

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICBusiness
import vn.icheck.android.screen.user.page_details.PageDetailActivity
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class MallBusinessAdapter(private val listData: List<ICBusiness>) : RecyclerView.Adapter<MallBusinessAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ViewHelper.createBusinessItemHolder(parent.context))
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(obj: ICBusiness) {
            WidgetUtils.loadImageUrlRounded(itemView as AppCompatImageButton, obj.avatar_thumbnails?.small, SizeHelper.size10)

            itemView.setOnClickListener {
                ICheckApplication.currentActivity()?.let { activity ->
                    ActivityUtils.startActivity<PageDetailActivity, Long>(activity, Constant.DATA_1, obj.id!!)
                }
            }
        }
    }
}