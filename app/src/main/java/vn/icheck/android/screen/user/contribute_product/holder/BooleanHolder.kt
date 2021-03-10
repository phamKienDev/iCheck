package vn.icheck.android.screen.user.contribute_product.holder

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.balloon.*
import vn.icheck.android.R
import vn.icheck.android.constant.ATTRIBUTES_POSITION
import vn.icheck.android.constant.CONTRIBUTIONS_ACTION
import vn.icheck.android.constant.PUT_ATTRIBUTES
import vn.icheck.android.databinding.ItemBooleanBinding
import vn.icheck.android.screen.user.contribute_product.viewmodel.CategoryAttributesModel
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.simpleText

class BooleanHolder(private val itemBooleanBinding: ItemBooleanBinding):RecyclerView.ViewHolder(itemBooleanBinding.root) {
    var balloon: Balloon? = null
    fun bind(categoryAttributesModel: CategoryAttributesModel) {
        if (categoryAttributesModel.categoryItem.description.isNullOrEmpty()) {
            itemBooleanBinding.imgHelp.beGone()
//            TooltipCompat.setTooltipText(itemBooleanBinding.imgHelp,null)
        } else {
            itemBooleanBinding.imgHelp.beVisible()
            balloon = createBalloon(itemView.context){
                setLayout(R.layout.item_popup)
                setHeight(BalloonSizeSpec.WRAP)
                setWidth(BalloonSizeSpec.WRAP)
                setAutoDismissDuration(5000L)
                setArrowConstraints(ArrowConstraints.ALIGN_ANCHOR)
                setArrowOrientation(ArrowOrientation.TOP)
                setBalloonAnimation(BalloonAnimation.OVERSHOOT)
                setBackgroundColor(Color.parseColor("#2D9CDB"))
            }
            balloon?.getContentView()?.findViewById<TextView>(R.id.tv_popup)?.text = categoryAttributesModel.categoryItem.description.toString()
            itemBooleanBinding.imgHelp.setOnClickListener {
                if (balloon?.isShowing == false) {
                    balloon?.showAlignBottom(itemBooleanBinding.imgHelp)
                } else {
                    balloon?.dismiss()
                }
            }
//            TooltipCompat.setTooltipText(itemBooleanBinding.imgHelp,categoryAttributesModel.categoryItem.description)
        }
        if (categoryAttributesModel.categoryItem.required == true) {
            itemBooleanBinding.tvTitle simpleText categoryAttributesModel.categoryItem.name + " (*)"
        } else {
            itemBooleanBinding.tvTitle simpleText categoryAttributesModel.categoryItem.name
        }
        if (categoryAttributesModel.values != null && categoryAttributesModel.values as Boolean? != null) {
            itemBooleanBinding.btnSwitch.isChecked = true
        }
        if (categoryAttributesModel.isEditable) {
            itemBooleanBinding.btnSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                itemBooleanBinding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                    putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                    putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                    putExtra(PUT_ATTRIBUTES, isChecked)
                })
            }
        } else {
            itemBooleanBinding.btnSwitch.isClickable = false
            itemBooleanBinding.btnSwitch.isFocusable = false
        }

    }

    companion object{
        fun create(parent:ViewGroup):BooleanHolder{
            val itemBooleanBinding = ItemBooleanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return BooleanHolder(itemBooleanBinding)
        }
    }
}