package vn.icheck.android.screen.user.checkoutcart.holder

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.item_checkout_payment.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICPayment
import vn.icheck.android.screen.user.checkoutcart.entity.Checkout
import vn.icheck.android.screen.user.checkoutcart.view.ICheckoutCartView

class CheckoutPaymentHolder(view: View, val listener: ICheckoutCartView) : BaseViewHolder<Checkout>(view) {

    override fun bind(obj: Checkout) {
        itemView.radioGroup.removeAllViews()

        obj.payments?.let { payments ->
            for (it in payments) {
                itemView.radioGroup.addView(createRadioButton(itemView.context, obj.payment_method_id, it))
            }
        }

        itemView.radioGroup.setOnCheckedChangeListener { radioGroup, id ->
            obj.payments?.let { payments ->
                val radioButton = itemView.radioGroup.findViewById<RadioButton>(id)
                val position = itemView.radioGroup.indexOfChild(radioButton)

                if (position >= 0 && position < payments.size) {
                    listener.onChangePayment(payments[position])
                }
            }
        }
    }

    private fun createRadioButton(context: Context, paymentID: Int?, obj: ICPayment): RadioButton {
        val radioButton = AppCompatRadioButton(context)

        val layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT)
        layoutParams.setMargins(0, SizeHelper.size12, 0, SizeHelper.size12)
        radioButton.layoutParams = layoutParams

        radioButton.id = View.generateViewId()
        radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        radioButton.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        radioButton.setTextColor(ViewHelper.createColorStateList(Constant.getDisableTextColor(context),Color.parseColor("#333333")))
        radioButton.includeFontPadding = false
        radioButton.gravity = Gravity.CENTER_VERTICAL
        radioButton.text = obj.name
        radioButton.isChecked = obj.id == paymentID
//        radioButton.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
        radioButton.gravity = Gravity.CENTER_VERTICAL
        return radioButton
    }
}