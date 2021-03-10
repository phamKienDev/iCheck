package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_holder

import android.os.Build
import android.text.Html
import android.text.Spannable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_step_build_product_stamp_parent.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.callback.ItemClickSmallAnimationListener
import vn.icheck.android.network.models.detail_stamp_v6_1.ICObjectInfo
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.adapter.step_build_product.StepBuildProductStampAdapter
import vn.icheck.android.util.kotlin.GlideImageGetter

class StepBuildProductStampHolder(parent: ViewGroup) : BaseViewHolder<MutableList<ICObjectInfo>>(LayoutInflater.from(parent.context).inflate(R.layout.item_step_build_product_stamp_parent, parent, false)), ItemClickListener<ICObjectInfo>, ItemClickSmallAnimationListener<ICObjectInfo> {
    private val adapter = StepBuildProductStampAdapter(this,this)

    override fun bind(obj: MutableList<ICObjectInfo>) {
        itemView.recyclerView.adapter = adapter
        if (adapter.isEmpty) {
            adapter.setData(obj)
        }

        setData(adapter.getSelectedPosition, adapter.getSelectedObj)
    }

    private fun setData(position: Int, obj: ICObjectInfo) {
        itemView.tvCount.text = (position + 1).toString()

        itemView.tvName.text = obj.title
        val imageGetter = GlideImageGetter(itemView.tvContent)
        val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(obj.short_content, Html.FROM_HTML_MODE_LEGACY, imageGetter, null) as Spannable
        } else {
            Html.fromHtml(obj.short_content, imageGetter, null)
        }
        itemView.tvContent.text = html
    }

    override fun onItemClick(position: Int, item: ICObjectInfo?) {
        item?.let {
            setData(position, it)
        }
    }

    override fun onItemClickSmall(position: Int, item: ICObjectInfo?) {
        itemView.recyclerView.findViewHolderForAdapterPosition(position)?.let {
            if (it is StepBuildProductStampAdapter.ViewHolder) {
                it.setSelect(false)
            }
        }
    }
}