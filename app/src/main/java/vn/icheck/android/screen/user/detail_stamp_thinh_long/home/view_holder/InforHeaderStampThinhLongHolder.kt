package vn.icheck.android.screen.user.detail_stamp_thinh_long.home.view_holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.infor_header_stamp_thinh_long.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.call_back.SlideHeaderStampHoaPhatListener
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.kotlin.WidgetUtils
import vn.icheck.android.util.text.ReviewPointText

class InforHeaderStampThinhLongHolder(parent: ViewGroup, val headerImagelistener: SlideHeaderStampHoaPhatListener) : BaseViewHolder<ICBarcodeProductV1>(LayoutInflater.from(parent.context).inflate(R.layout.infor_header_stamp_thinh_long, parent, false)) {

    var nation: String? = "http://ucontent.icheck.vn/ensign/VN.png"
        set(value) {
            field = "http://ucontent.icheck.vn/ensign/" + value?.toUpperCase() + ".png"
        }

    override fun bind(obj: ICBarcodeProductV1) {
        itemView.tv_product_name.text = obj.name

        if (obj.price > 0L && obj.price != null) {
            itemView.tv_price.setText(R.string.x_d, obj.price)
            itemView.tvGiaNiemYet.visibility = View.VISIBLE
        } else {
            itemView.tv_price.text = itemView.context.getString(R.string.dang_cap_nhat_gia)
            itemView.tvGiaNiemYet.visibility = View.INVISIBLE
        }

        itemView.tvBarcodeProduct.text = obj.barcode

        nation = obj.owner?.vendorPage?.country?.code

        val nationName = obj.owner?.vendorPage?.country?.name ?: itemView.tv_sx.context.getString(R.string.viet_nam)

        if (!nationName.isNullOrEmpty()) {
            itemView.tv_sx.text = nationName
            WidgetUtils.loadImageUrl(itemView.nation_img,nation)
            itemView.tv_sx.visibility = View.VISIBLE
            itemView.nation_img.visibility = View.VISIBLE
        } else {
            itemView.tv_sx.visibility = View.GONE
            itemView.nation_img.visibility = View.GONE
        }

        if (obj.productRate > 0f) {
            itemView.product_rate.visibility = View.VISIBLE
            itemView.tv_product_avg.visibility = View.VISIBLE
            itemView.product_rate.rating = obj.productRate
            itemView.tv_product_avg.setText(R.string.your_score, obj.productRate * 2,
                    ReviewPointText.getText(obj.productRate))
        } else {
            itemView.product_rate.visibility = View.GONE
            itemView.tv_product_avg.visibility = View.GONE
        }

        if (obj.countReviews == 0) {
            itemView.tv_xrv.visibility = View.GONE
        } else {
            itemView.tv_xrv.setText(R.string.xem_d_danh_gia, obj.countReviews)
            itemView.tv_xrv.setOnClickListener {
                headerImagelistener.showAllBottomReviews()
            }
            itemView.tv_xrv.visibility = View.VISIBLE
        }
    }
}