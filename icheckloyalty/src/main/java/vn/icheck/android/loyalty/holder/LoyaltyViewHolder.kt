package vn.icheck.android.loyalty.holder

import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_loyalty_holder.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.CampaignLoyaltyHelper
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKLoyalty
import vn.icheck.android.loyalty.screen.url_gift_detail.UrlGiftDetailActivity

class LoyaltyViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_loyalty_holder, parent, false)) {
    fun bind(obj: ICKLoyalty, barcode: String, actvity: FragmentActivity, listener: CampaignLoyaltyHelper.IRemoveHolderInputLoyaltyListener, callback: CampaignLoyaltyHelper.ILoginListener) {
        WidgetHelper.loadImageUrl(itemView.banner_game, obj.image?.original)

        itemView.loyalty_description.text = if (!obj.name.isNullOrEmpty()) {
            obj.name
        } else {
            itemView.context.getString(R.string.dang_cap_nhat)
        }

        itemView.banner_game.setOnClickListener {
            itemView.context.startActivity(Intent(itemView.context, UrlGiftDetailActivity::class.java).apply {
                putExtra(ConstantsLoyalty.DATA_1, obj.id)
                putExtra(ConstantsLoyalty.DATA_2, barcode)
                putExtra(ConstantsLoyalty.DATA_3, obj)
            })
        }

        itemView.btnCheckCode.setOnClickListener {
            itemView.btnCheckCode.isEnabled = false
            CampaignLoyaltyHelper.checkCodeLoyalty(actvity, obj, itemView.edittext.text.toString().trim(), barcode, listener, callback)
            itemView.edittext.setText("")
            Handler().postDelayed({
                itemView.btnCheckCode.isEnabled = true
            }, 3000)
        }
    }
}