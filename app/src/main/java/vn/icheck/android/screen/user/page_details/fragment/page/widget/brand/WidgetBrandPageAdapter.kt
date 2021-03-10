package vn.icheck.android.screen.user.page_details.fragment.page.widget.brand

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.network.models.ICPageTrend
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class WidgetBrandPageAdapter(val listData: MutableList<ICPageTrend>) : RecyclerView.Adapter<WidgetBrandPageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICPageTrend>(ViewHelper.createItemBrandPage(parent.context)) {

        override fun bind(obj: ICPageTrend) {
            val params = (itemView as CardView).getChildAt(0) as LinearLayout

            WidgetUtils.loadImageUrl((params.getChildAt(0) as CircleImageView), obj.avatar)

            (params.getChildAt(1) as AppCompatTextView).run {
                if (!obj.name.isNullOrEmpty()) {
                    text = obj.name
                }
            }

            (params.getChildAt(2) as AppCompatTextView).run {
                if (obj.followCount != null) {
                    if (obj.followCount!! < 1000) {
                        text = "${obj.followCount} người theo dõi"
                    } else if (obj.followCount!! < 10000) {
                        text = "${TextHelper.formatMoney(obj.followCount)} người theo dõi"
                    } else {
                        text = "${((obj.followCount)!! / 1000f).toString().replace(".0", "")}k người theo dõi"
                    }
                }
            }

            itemView.setOnClickListener {
                ToastUtils.showLongWarning(itemView.context, "onClickItemBrand")
            }
        }
    }
}