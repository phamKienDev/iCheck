package vn.icheck.android.screen.user.contribute_product.holder

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.balloon.*
import vn.icheck.android.R
import vn.icheck.android.constant.ATTRIBUTES_POSITION
import vn.icheck.android.constant.CONTRIBUTIONS_ACTION
import vn.icheck.android.constant.PUT_ATTRIBUTES
import vn.icheck.android.databinding.ItemMultiSelectBinding
import vn.icheck.android.screen.user.contribute_product.viewmodel.CategoryAttributesModel
import vn.icheck.android.util.ick.*

class MultiSelectHolder(val binding:ItemMultiSelectBinding):RecyclerView.ViewHolder(binding.root) {
    var balloon: Balloon? = null
    fun bind(categoryAttributesModel: CategoryAttributesModel){
        if (categoryAttributesModel.categoryItem.description.isNullOrEmpty()) {
            binding.imgHelp.beGone()
//            TooltipCompat.setTooltipText(binding.imgHelp,null)
        } else {
            binding.imgHelp.beVisible()
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
            binding.imgHelp.setOnClickListener {
                if (balloon?.isShowing == false) {
                    balloon?.showAlignBottom(binding.imgHelp)
                } else {
                    balloon?.dismiss()
                }
            }
//            TooltipCompat.setTooltipText(binding.imgHelp,categoryAttributesModel.categoryItem.description)
        }
        if (categoryAttributesModel.categoryItem.required == true) {
            binding.tvTitle simpleText categoryAttributesModel.categoryItem.name + " (*)"
        } else {
            binding.tvTitle simpleText categoryAttributesModel.categoryItem.name
        }
        if (!categoryAttributesModel.categoryItem.options.isNullOrEmpty()) {
            val arr = arrayListOf<Double>()
            if (categoryAttributesModel.categoryItem.value != null && categoryAttributesModel.categoryItem.value is ArrayList<*>) {
                val temp = categoryAttributesModel.categoryItem.value as ArrayList<Double>
                arr.addAll(temp)
            }else if (categoryAttributesModel.values != null && categoryAttributesModel.values is ArrayList<*>) {
                val temp = categoryAttributesModel.values as ArrayList<Double>
                arr.addAll(temp)
            }
            if (binding.root.childCount > 1) {
                binding.root.removeViewsInLayout(1, binding.root.childCount - 1)
            }
            for (item in categoryAttributesModel.categoryItem.options ?: arrayListOf()) {
                binding.root.addView(CheckBox(binding.root.context).apply {
                    viewTreeObserver.addOnGlobalLayoutListener {
                        val lp = layoutParams as LinearLayout.LayoutParams
                        lp.setMargins(10.dpToPx(), 0,10.dpToPx(), 0)
                        layoutParams = lp
                    }
                    text = item?.value
                    if (arr.firstOrNull { arrId -> arrId.toInt() == item?.id } != null) {
                        isChecked = true
                    }
                    if (!categoryAttributesModel.isEditable) {
                        isClickable = false
                        isFocusable = false
                    } else {
                        setCustomChecked(object : CompoundButton.OnCheckedChangeListener {
                            var id = item?.id
                            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                                buttonView?.context?.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                                    putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                                    putExtra(ATTRIBUTES_POSITION, absoluteAdapterPosition)
                                    if (isChecked) {
                                        putExtra(PUT_ATTRIBUTES, id)
                                    } else if (id != null) {
                                        putExtra(PUT_ATTRIBUTES, -id!!)
                                    }
                                })
                            }
                        })
                    }
                })
            }
        }

    }
    companion object{
        fun create(parent: ViewGroup):MultiSelectHolder {
            val binding = ItemMultiSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MultiSelectHolder(binding)
        }
    }
}