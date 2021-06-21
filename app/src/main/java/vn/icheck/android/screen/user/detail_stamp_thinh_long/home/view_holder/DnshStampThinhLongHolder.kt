package vn.icheck.android.screen.user.detail_stamp_thinh_long.home.view_holder

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dnsh_stamp_thinh_long.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.network.models.v1.ICBarcodeProductV1
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.call_back.SlideHeaderStampHoaPhatListener
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText

class DnshStampThinhLongHolder(parent: ViewGroup, val headerImagelistener: SlideHeaderStampHoaPhatListener) : BaseViewHolder<ICBarcodeProductV1.Vendor>(LayoutInflater.from(parent.context).inflate(R.layout.dnsh_stamp_thinh_long, parent, false)) {

    override fun bind(obj: ICBarcodeProductV1.Vendor) {
        itemView.tv_owner_name.text = obj.vendorPage.name
        itemView.img_not_verified.visibility = View.VISIBLE
        if (obj.vendorPage.tax != null) {
            itemView.tv_owner_mst.setText(R.string.ma_so_thue_s, obj.vendorPage.tax)
        } else {
            itemView.tv_owner_mst.visibility = View.GONE
        }
        if (obj.vendorPage.address != null) {
            itemView.tv_owner_address.text = obj.vendorPage.address
        } else {
            itemView.tv_owner_address.visibility = View.GONE
        }
        if (obj.vendorPage.phone != null) {
            itemView.tv_owner_phone.text = obj.vendorPage.phone
        } else {
            itemView.tv_owner_phone.visibility = View.GONE
        }
        if (obj.vendorPage.website != null) {
            itemView.tv_owner_website.text = obj.vendorPage.website
        } else {
            itemView.tv_owner_website.visibility = View.GONE
        }
        if (obj.vendorPage.email != null) {
            itemView.tv_owner_mail.text = obj.vendorPage.email
        } else {
            itemView.tv_owner_mail.visibility = View.GONE
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemView.owner_name_vg.background = itemView.context.resources.getDrawable(R.drawable.bg_corner_top_dnsh_thinh_long_10, null)
        }
        itemView.img_not_verified.setImageResource(R.drawable.ic_evs_verified)

        itemView.tv_owner_phone.setOnClickListener {
            headerImagelistener.dial(obj.vendorPage.phone)
        }

        itemView.tv_owner_website.setOnClickListener {
            headerImagelistener.web(obj.vendorPage.website)
        }

        itemView.tv_owner_mail.setOnClickListener {
            headerImagelistener.email(obj.vendorPage.email)
        }

        itemView.setOnClickListener {
            headerImagelistener.showPage(obj.vendorPage.id)
        }
    }
}