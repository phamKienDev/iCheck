package vn.icheck.android.screen.user.detail_my_reward.holder

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.header_infor_reward.*
import kotlinx.android.synthetic.main.header_infor_reward.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.ICDetail_Campaign
import vn.icheck.android.network.models.ICItemReward
import vn.icheck.android.util.ick.rText
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

        itemView.tvRefuse rText R.string.ma_cao_the
        itemView.tvTime rText R.string.han_su_dung
        itemView.tvTimeDes.text = TimeHelper.convertDateSvToDateVn(obj.remainTime)

        when (obj.state) {
            //chưa dùng
            1 -> {
                itemView.tvRefuseDes.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAccentGreen))
                itemView.tvRefuseDes.text = obj.refuse

                itemView.imgUsed.visibility = View.GONE
            }
            //đã dùng
            2 -> {
                itemView.tvRefuseDes.setTextColor(Color.parseColor("#212121"))
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

        itemView.tvRefuse rText R.string.han_lay_qua
        itemView.tvRefuseDes.setTextColor(Color.parseColor("#212121"))
        itemView.tvRefuseDes.text = obj.refuse

        itemView.tvTime rText R.string.loai_qua
        itemView.tvTimeDes.setTextColor(Color.parseColor("#212121"))
        itemView.tvRefuseDes rText R.string.qua_lay_tai_cua_hang

    }

    fun stateProductShip(obj: ICItemReward) {
        itemView.imgUsed.visibility = View.GONE
        itemView.tvRefuseDes.setTextColor(Color.parseColor("#212121"))
        itemView.tvTimeDes.setTextColor(Color.parseColor("#212121"))
        when (obj.state) {
            //chưa nhận
            1 -> {
                itemView.tvState.visibility = View.GONE

                itemView.tvRefuse.visibility = View.VISIBLE
                itemView.tvRefuse rText R.string.han_nhan_qua
                itemView.tvRefuseDes.visibility = View.VISIBLE
                itemView.tvRefuseDes.text = TimeHelper.convertDateSvToDateVn(obj.remainTime)

                itemView.tvTime.visibility = View.VISIBLE
                itemView.tvTime rText R.string.loai_qua
                itemView.tvTimeDes.visibility = View.VISIBLE
                itemView.tvTimeDes.text = TimeHelper.convertDateSvToDateVn(obj.refuse)
            }
            //đã ship
            2 -> {
                itemView.tvState.visibility = View.VISIBLE
                itemView.tvState.setTextColor(Color.parseColor("#057DDA"))
                itemView.tvState rText R.string.da_xac_nhan_giao_qua

                itemView.tvRefuse.visibility = View.VISIBLE
                itemView.tvRefuse rText R.string.thoi_gian_xac_nhan
                itemView.tvRefuse.text = TimeHelper.convertDateSvToDateVn(obj.remainTime)

                itemView.tvTime.visibility = View.GONE
                itemView.tvTimeDes.visibility = View.GONE
            }
            //từ chối
            3 -> {
                itemView.tvState.visibility = View.VISIBLE
                itemView.tvRefuse.visibility = View.VISIBLE
                itemView.tvTime.visibility = View.VISIBLE

                itemView.tvState.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAccentRed))
                itemView.tvState rText R.string.ban_da_tu_choi_nhan_qua

                itemView.tvRefuse rText R.string.ly_do_tu_choi
                itemView.tvRefuseDes rText R.string.phi_ship_qua_cao_1_dong

                itemView.tvTime rText R.string.thoi_gian_tu_choi
                itemView.tvTimeDes.text = TimeHelper.convertDateSvToDateVn(obj.remainTime)
            }
            //giao thành công
            4 -> {
                itemView.tvState.visibility = View.VISIBLE
                itemView.tvState.setTextColor(ContextCompat.getColor(itemView.context, R.color.colorAccentGreen))
                itemView.tvState rText R.string.giao_qua_thanh_cong

                itemView.tvRefuse.visibility = View.VISIBLE
                itemView.tvRefuse rText R.string.thoi_gian_giao
                itemView.tvRefuse.text = TimeHelper.convertDateSvToDateVn(obj.remainTime)

                itemView.tvTime.visibility = View.GONE
                itemView.tvTimeDes.visibility = View.GONE
            }
        }
    }
}