package vn.icheck.android.screen.user.contribute_product.holder

import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.skydoves.balloon.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.R
import vn.icheck.android.base.holder.CoroutineViewHolder
import vn.icheck.android.constant.ATTRIBUTES_POSITION
import vn.icheck.android.constant.CONTRIBUTIONS_ACTION
import vn.icheck.android.constant.PUT_ATTRIBUTES
import vn.icheck.android.databinding.ItemNumberBinding
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.screen.user.contribute_product.viewmodel.CategoryAttributesModel
import vn.icheck.android.util.AfterTextWatcher
import vn.icheck.android.util.ick.*

class NumberTextHolder(private val itemNumberBinding: ItemNumberBinding) : CoroutineViewHolder(itemNumberBinding.root) {
    var balloon: Balloon? = null
    fun bind(categoryAttributesModel: CategoryAttributesModel) {
        if (categoryAttributesModel.categoryItem.description.isNullOrEmpty()) {
            itemNumberBinding.imgHelp.beGone()
//            TooltipCompat.setTooltipText(itemNumberBinding.imgHelp,null)
        } else {
            itemNumberBinding.imgHelp.beVisible()
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
//            TooltipCompat.setTooltipText(itemSelectBinding.imgHelp,categoryAttributesModel.categoryItem.description )
            itemNumberBinding.imgHelp.setOnClickListener {
                if (balloon?.isShowing == false) {
                    balloon?.showAlignBottom(itemNumberBinding.imgHelp)
                } else {
                    balloon?.dismiss()
                }
            }
//            TooltipCompat.setTooltipText(itemNumberBinding.imgHelp,categoryAttributesModel.categoryItem.description)
        }
        if (categoryAttributesModel.categoryItem.required == true) {
            itemNumberBinding.tvTitle.setText(R.string.s_bat_buoc, categoryAttributesModel.categoryItem.name?:"")
        } else {
            itemNumberBinding.tvTitle simpleText categoryAttributesModel.categoryItem.name
        }

        itemNumberBinding.edtPrice.apply {
            clearFocus()
            hint = context.getString(R.string.nhap_s, categoryAttributesModel.categoryItem.name)
            setText(categoryAttributesModel.values?.toStringNotNull())
        }

        if (categoryAttributesModel.values != null && categoryAttributesModel.values.toString().isNotEmpty()) {
            when {
                categoryAttributesModel.values.toString().toDoubleOrNull() != null -> {
                    itemNumberBinding.edtPrice.setText(categoryAttributesModel.values?.toStringNotNull()?.toLongOrNull()?.toString()
                            ?: "")
                }
                categoryAttributesModel.values.toString().toIntOrNull() != null -> {
                    itemNumberBinding.edtPrice.setText((categoryAttributesModel.values.toString().toLongOrNull()).toString())
                }
                else -> {
                    itemNumberBinding.edtPrice.setText((categoryAttributesModel.values.toString()).toString())
                }
            }
        } else {
            itemNumberBinding.edtPrice.setText("")
        }
//        itemNumberBinding.edtPrice.setImeActionDone({
//            itemNumberBinding.edtPrice.clearFocus()
//            action?.invoke()
//        })
//        itemNumberBinding.edtPrice.setOnFocusChangeListener { v, hasFocus ->
//            IckContributeProductActivity.onNumber = hasFocus
//        }
        itemNumberBinding.edtPrice.addTextChangedListener(object : AfterTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    job = if (job?.isActive == true) {
                        job?.cancel()
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(400)
                            itemNumberBinding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                                putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                                putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                                putExtra(PUT_ATTRIBUTES, s.toString())
                            })
                        }
                    } else {
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(400)
                            itemNumberBinding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                                putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                                putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                                putExtra(PUT_ATTRIBUTES, s.toString())
                            })
                        }
                    }

                }


            }
        })
    }


    companion object {
        fun create(parent: ViewGroup): NumberTextHolder {
            val itemNumberBinding = ItemNumberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return NumberTextHolder(itemNumberBinding)
        }
    }
}