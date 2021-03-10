package vn.icheck.android.component.box

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.button_suggestion_box_component.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.component.shopvariant.history.IShopVariantListener
import vn.icheck.android.component.view.BaseButton
import vn.icheck.android.network.models.ICButtonSuggestionModel
import vn.icheck.android.screen.firebase.FirebaseDynamicLinksActivity
import vn.icheck.android.util.kotlin.WidgetUtils

class ButtonSuggestionBoxComponent(view: View, val listener: IShopVariantListener) : BaseViewHolder<ICButtonSuggestionModel?>(view) {
    override fun bind(obj: ICButtonSuggestionModel?) {
        if (!obj?.message.isNullOrEmpty()) {
            itemView.tvMessage.text = obj?.message
        }

        if (!obj?.image.isNullOrEmpty()) {
            WidgetUtils.loadImageUrl(itemView.imgBackground, obj?.image)
        }

        if (obj?.allowClose != null) {
            if (obj.allowClose == true) {
                itemView.imgDelete.visibility = View.VISIBLE
            } else {
                itemView.imgDelete.visibility = View.GONE
            }
        } else {
            itemView.imgDelete.visibility = View.GONE
        }

        obj?.button?.let {
            itemView.btnAction.setTheme(it)
        }

        itemView.btnAction.setOnClickListener(object : BaseButton.OnClickListener {
            override fun onClicked(schema: String?) {
                ICheckApplication.currentActivity()?.let { act ->
                    FirebaseDynamicLinksActivity.startDestinationUrl(act, schema)
                }
            }
        })

        itemView.imgDelete.setOnClickListener {
            listener.onDeleteItemSuccess(adapterPosition)
        }
    }

    companion object {
        fun create(parent: ViewGroup, listener: IShopVariantListener): ButtonSuggestionBoxComponent {
            return ButtonSuggestionBoxComponent(LayoutInflater.from(parent.context).inflate(R.layout.button_suggestion_box_component, parent, false), listener)
        }
    }
}