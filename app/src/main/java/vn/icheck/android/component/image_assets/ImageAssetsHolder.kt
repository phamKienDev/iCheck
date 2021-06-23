package vn.icheck.android.component.image_assets

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.ICheckApplication
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.header_page.bottom_sheet_header_page.IListReportView
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.ichecklibs.view.TextBarlowSemiBold
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICImageAsset
import vn.icheck.android.screen.user.image_asset_page.ImageAssetPageActivity
import vn.icheck.android.util.kotlin.ActivityUtils

class ImageAssetsHolder(parent: ViewGroup, private val recyclerViewPool: RecyclerView.RecycledViewPool?, val view: IListReportView) : BaseViewHolder<ICImageAsset>(ViewHelper.createImageAssets(parent.context)) {

    @SuppressLint("SetTextI18n")
    override fun bind(obj: ICImageAsset) {
        (itemView as ViewGroup).run {
            (getChildAt(0) as LinearLayout).run {
                (getChildAt(1) as TextBarlowSemiBold).setOnClickListener {
                    ICheckApplication.currentActivity()?.let { activity ->
                        val intent = Intent(activity, ImageAssetPageActivity::class.java)
                        intent.putExtra(Constant.DATA_1, obj.pageName)
                        intent.putExtra(Constant.DATA_2, obj.pageID)
                        ActivityUtils.startActivity(activity, intent)
                    }
                }
            }

            (getChildAt(1) as RecyclerView).run {
                layoutManager = LinearLayoutManager(itemView.context.applicationContext, LinearLayoutManager.HORIZONTAL, false)
                adapter = ImageAssetsAdapter(obj.listImage!!, view)
                setRecycledViewPool(recyclerViewPool)
            }
        }
    }
}