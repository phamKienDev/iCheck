package vn.icheck.android.loyalty.holder

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_loyalty_holder.view.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.helper.WidgetHelper
import vn.icheck.android.loyalty.model.ICKLoyalty
import vn.icheck.android.loyalty.screen.url_gift_detail.UrlGiftDetailActivity
import java.util.*

class LoyaltyViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_loyalty_holder, parent, false)) {

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            itemView.edittext.removeTextChangedListener(this)

            itemView.edittext.setText(itemView.edittext.text.toString().toUpperCase(Locale.getDefault()))

            itemView.edittext.setSelection(itemView.edittext.text.toString().length)

            itemView.edittext.addTextChangedListener(this)
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    }

    fun bind(obj: ICKLoyalty, barcode: String) {
        WidgetHelper.loadImageUrl(itemView.banner_game, obj.image?.original)

        itemView.loyalty_description.text = if (!obj.name.isNullOrEmpty()) {
            obj.name
        } else {
            itemView.context.getString(R.string.dang_cap_nhat)
        }

        itemView.edittext.removeTextChangedListener(textWatcher)

        itemView.edittext.addTextChangedListener(textWatcher)

        itemView.banner_game.setOnClickListener {
            itemView.context.startActivity(Intent(itemView.context, UrlGiftDetailActivity::class.java).apply {
                putExtra(ConstantsLoyalty.DATA_1, obj.id)
                putExtra(ConstantsLoyalty.DATA_2, barcode)
                putExtra(ConstantsLoyalty.DATA_3, obj)
            })
        }
    }
}