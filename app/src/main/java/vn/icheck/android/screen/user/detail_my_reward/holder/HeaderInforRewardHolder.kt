package vn.icheck.android.screen.user.detail_my_reward.holder

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.header_infor_reward.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.network.models.ICItemReward
import vn.icheck.android.util.kotlin.WidgetUtils

class HeaderInforRewardHolder(parent: ViewGroup) : BaseViewHolder<ICItemReward>(LayoutInflater.from(parent.context).inflate(R.layout.header_infor_reward, parent, false)) {
    @SuppressLint("SetTextI18n")
    override fun bind(obj: ICItemReward) {
        WidgetUtils.loadImageUrl(itemView.imgBanner, obj.image)
        WidgetUtils.loadImageUrlRounded4(itemView.imgLogo, obj.shopImage)
        itemView.tvNameProduct.text = obj.name
        itemView.tvCongty.text = obj.shopName

        when (obj.type) {
            1 -> {
                stateProductShip(obj)
            }
            2 -> {
                stateCardMobile(obj)
            }
            3 -> {
                stateProductShop(obj)
            }
        }
    }

    private fun stateCardMobile(obj: ICItemReward) {
        itemView.tvState.visibility = View.GONE
        itemView.tvRefuse.visibility = View.VISIBLE
        itemView.tvTime.visibility = View.VISIBLE
        itemView.tvRefuseDes.visibility = View.VISIBLE
        itemView.tvTimeDes.visibility = View.VISIBLE

        itemView.tvRefuse.text = "Mã thẻ cào"
        itemView.tvTime.text = "Hạn sử dụng"
        itemView.tvTimeDes.text = TimeHelper.convertDateSvToDateVn(obj.remainTime)

        when (obj.state) {
            //chưa dùng
            1 -> {
                itemView.tvRefuseDes.setTextColor(ColorManager.getAccentGreenColor(itemView.context))
                itemView.tvRefuseDes.text = obj.refuse

                itemView.imgUsed.visibility = View.GONE
            }
            //đã dùng
            2 -> {
                itemView.tvRefuseDes.setTextColor(ColorManager.getNormalTextColor(itemView.context))
                itemView.tvRefuseDes.text = obj.refuse

                itemView.imgUsed.visibility = View.VISIBLE
            }
        }
    }

    fun stateProductShop(obj: ICItemReward) {
        itemView.imgUsed.visibility = View.GONE
        itemView.tvState.visibility = View.GONE
        itemView.tvRefuse.visibility = View.VISIBLE
        itemView.tvTime.visibility = View.VISIBLE
        itemView.tvRefuseDes.visibility = View.VISIBLE
        itemView.tvTimeDes.visibility = View.VISIBLE

        itemView.tvRefuse.text = "Hạn lấy quà"
        itemView.tvRefuseDes.setTextColor(ColorManager.getNormalTextColor(itemView.context))
        itemView.tvRefuseDes.text = obj.refuse

        itemView.tvTime.text = "Loại quà"
        itemView.tvTimeDes.setTextColor(ColorManager.getNormalTextColor(itemView.context))
        itemView.tvRefuseDes.text = "Qùa lấy tại cửa hàng"

    }

    fun stateProductShip(obj: ICItemReward) {
        itemView.imgUsed.visibility = View.GONE
        itemView.tvRefuseDes.setTextColor(ColorManager.getNormalTextColor(itemView.context))
        itemView.tvTimeDes.setTextColor(ColorManager.getNormalTextColor(itemView.context))
        when (obj.state) {
            //chưa nhận
            1 -> {
                itemView.tvState.visibility = View.GONE

                itemView.tvRefuse.visibility = View.VISIBLE
                itemView.tvRefuse.text = "Hạn nhận quà"
                itemView.tvRefuseDes.visibility = View.VISIBLE
                itemView.tvRefuseDes.text = TimeHelper.convertDateSvToDateVn(obj.remainTime)

                itemView.tvTime.visibility = View.VISIBLE
                itemView.tvTime.text = "Loại quà"
                itemView.tvTimeDes.visibility = View.VISIBLE
                itemView.tvTimeDes.text = TimeHelper.convertDateSvToDateVn(obj.refuse)
            }
            //đã ship
            2 -> {
                itemView.tvState.visibility = View.VISIBLE
                itemView.tvState.setTextColor(vn.icheck.android.ichecklibs.ColorManager.getPrimaryColor(itemView.context))
                itemView.tvState.text = "Đã xác nhận giao quà"

                itemView.tvRefuse.visibility = View.VISIBLE
                itemView.tvRefuse.text = "Thời gian xác nhận"
                itemView.tvRefuse.text = TimeHelper.convertDateSvToDateVn(obj.remainTime)

                itemView.tvTime.visibility = View.GONE
                itemView.tvTimeDes.visibility = View.GONE
            }
            //từ chối
            3 -> {
                itemView.tvState.visibility = View.VISIBLE
                itemView.tvRefuse.visibility = View.VISIBLE
                itemView.tvTime.visibility = View.VISIBLE

                itemView.tvState.setTextColor(ColorManager.getAccentRedColor(itemView.context))
                itemView.tvState.text = "Bạn đã từ chối nhận quà"

                itemView.tvRefuse.text = "Lý do từ chối"
                itemView.tvRefuseDes.text = "Phí ship cao quá (1 dòng)"

                itemView.tvTime.text = "Thời gian từ chối"
                itemView.tvTimeDes.text = TimeHelper.convertDateSvToDateVn(obj.remainTime)
            }
            //giao thành công
            4 -> {
                itemView.tvState.visibility = View.VISIBLE
                itemView.tvState.setTextColor(ColorManager.getAccentGreenColor(itemView.context))
                itemView.tvState.text = "Giao quà thành công"

                itemView.tvRefuse.visibility = View.VISIBLE
                itemView.tvRefuse.text = "Thời gian giao"
                itemView.tvRefuse.text = TimeHelper.convertDateSvToDateVn(obj.remainTime)

                itemView.tvTime.visibility = View.GONE
                itemView.tvTimeDes.visibility = View.GONE
            }
        }
    }
}