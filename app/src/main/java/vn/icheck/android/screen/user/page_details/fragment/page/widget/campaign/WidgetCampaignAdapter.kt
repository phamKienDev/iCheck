package vn.icheck.android.screen.user.page_details.fragment.page.widget.campaign

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.ICCampaign
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class WidgetCampaignAdapter(val listData: MutableList<ICCampaign>) : RecyclerView.Adapter<WidgetCampaignAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WidgetCampaignAdapter.ViewHolder {
        return ViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: WidgetCampaignAdapter.ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(parent: ViewGroup) : BaseViewHolder<ICCampaign>(ViewHelper.createItemCampaign(parent.context)) {
        override fun bind(obj: ICCampaign) {
            val params = (itemView as CardView).getChildAt(0) as LinearLayout

//            if (obj.media?.get(0)?.type == "image") {
//                WidgetUtils.loadImageUrlRounded4((params.getChildAt(0) as AppCompatImageView), obj.media?.get(0)?.content)
//            }
            WidgetUtils.loadImageUrlRounded4((params.getChildAt(0) as AppCompatImageView), obj.image)

            itemView.setOnClickListener {
//                val intent = Intent(itemView.context, DetailCampaignV2Activity::class.java)
//                intent.putExtra(Constant.DATA_1, obj)
//                itemView.context.startActivity(intent)
            }

            (params.getChildAt(1) as AppCompatTextView).run {
                if (obj.state is String) {
                    when (obj.state) {
                        "running" -> {
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAccentRed))
                            text = context.rText(R.string.dang_dien_ra)
                        }
                        "finished" -> {
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.black_21_v2))
                            text = context.rText(R.string.da_ket_thuc)
                            (params.getChildAt(4) as LinearLayout).visibility = View.INVISIBLE
                        }
                        else -> {
                            setTextColor(ContextCompat.getColor(itemView.context, R.color.colorSecondText))
                            text = TimeHelper.convertDateSvToDateVn(obj.startTime)
                        }
                    }
                }
            }

            (params.getChildAt(2) as AppCompatTextView).run {
                text = if (!obj.title.isNullOrEmpty()) {
                    obj.title
                } else {
                    itemView.context.getString(R.string.dang_cap_nhat)
                }
            }

            (params.getChildAt(3) as AppCompatTextView).run {
                text = if (obj.daysLeft != null && obj.daysLeft != 0) {
                    context.rText(R.string.con_d_ngay, obj.daysLeft)
                } else {
                    itemView.context.getString(R.string.dang_cap_nhat)
                }
            }

            (params.getChildAt(4) as LinearLayout).run {
                setOnClickListener {
                    ToastUtils.showLongWarning(itemView.context, it.context.rText(R.string.on_click_join))
                }
            }
        }
    }
}