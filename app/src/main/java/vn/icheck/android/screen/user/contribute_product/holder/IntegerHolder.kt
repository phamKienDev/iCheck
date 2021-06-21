package vn.icheck.android.screen.user.contribute_product.holder

import android.content.Intent
import android.graphics.Color
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
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
import vn.icheck.android.databinding.ItemIntegerBinding
import vn.icheck.android.screen.user.contribute_product.viewmodel.CategoryAttributesModel
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText

class IntegerHolder(val binding:ItemIntegerBinding):CoroutineViewHolder(binding.root) {
    var value = 0L
    var balloon: Balloon? = null
    fun bind(categoryAttributesModel: CategoryAttributesModel){
        if (categoryAttributesModel.categoryItem.description.isNullOrEmpty()) {
//            TooltipCompat.setTooltipText(binding.imgHelp,null)
            binding.imgHelp.beGone()
        } else {
            binding.imgHelp.beVisible()
//            TooltipCompat.setTooltipText(binding.imgHelp,categoryAttributesModel.categoryItem.description)
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
        }
        if (categoryAttributesModel.categoryItem.required == true) {
            binding.tvTitle.setText(R.string.s_bat_buoc, categoryAttributesModel.categoryItem.name)
        } else {
            binding.tvTitle.text = categoryAttributesModel.categoryItem.name
        }
        if (categoryAttributesModel.values != null && categoryAttributesModel.values.toString().isNotEmpty()) {
            if (categoryAttributesModel.values is Double) {
                binding.tvValue.setText((categoryAttributesModel.values as Double).toInt().toString())
                value = (categoryAttributesModel.values as Double).toLong()
            } else if (categoryAttributesModel.values is Int) {
                binding.tvValue.setText(categoryAttributesModel.values.toString())
                value = categoryAttributesModel.values as Long
            }

        } else {
            binding.tvValue.setText("")
            value = 0L
        }
        binding.tvValue.addTextChangedListener {
            if (it.toString().toLongOrNull() != null) {
                value = it.toString().toLong()
                job = if (job?.isActive == true) {
                    job?.cancel()
                    CoroutineScope(Dispatchers.Main).launch{
                        delay(400)
                        binding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                            if (categoryAttributesModel.values == null) {
                                putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                                putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                                putExtra(PUT_ATTRIBUTES, value)
                            } else {
                                putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                                putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                                putExtra(PUT_ATTRIBUTES, value)
                            }
                        })
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch{
                        delay(400)
                        binding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                            if (categoryAttributesModel.values == null) {
                                putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                                putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                                putExtra(PUT_ATTRIBUTES, value)
                            } else {
                                putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                                putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                                putExtra(PUT_ATTRIBUTES, value)
                            }
                        })
                    }
                }
            }
        }
//        if (categoryAttributesModel.values is Double) {
//            value = (categoryAttributesModel.values as Double).toInt()
//        }
        binding.btnAdd.setOnClickListener {
            value++
            val select = binding.tvValue.selectionEnd
            binding.tvValue.setText(value.toString())
            binding.tvValue.setSelection(select)
            job = if (job?.isActive == true) {
                job?.cancel()
                CoroutineScope(Dispatchers.Main).launch{
                    delay(400)
                    binding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                        if (categoryAttributesModel.values == null) {
                            putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                            putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                            putExtra(PUT_ATTRIBUTES, value)
                        } else {
                            putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                            putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
//                        if (categoryAttributesModel.values is Int) {
//                            categoryAttributesModel.values = categoryAttributesModel.values as Int + 1
//                        }
                            putExtra(PUT_ATTRIBUTES, value)
                        }
                    })
                }
            } else {
                CoroutineScope(Dispatchers.Main).launch{
                    delay(400)
                    binding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                        if (categoryAttributesModel.values == null) {
                            putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                            putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                            putExtra(PUT_ATTRIBUTES, value)
                        } else {
                            putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                            putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
//                        if (categoryAttributesModel.values is Int) {
//                            categoryAttributesModel.values = categoryAttributesModel.values as Int + 1
//                        }
                            putExtra(PUT_ATTRIBUTES, value)
                        }
                    })
                }
            }
        }
        binding.btnSubtract.setOnClickListener {
            if (value > 0) {
                value--
                val select = binding.tvValue.selectionEnd
                binding.tvValue.setText(value.toString())
                binding.tvValue.setSelection(select)
                job = if (job?.isActive == true) {
                    job?.cancel()
                    CoroutineScope(Dispatchers.Main).launch{
                        delay(400)
                        binding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                            putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                            putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                            //                    if (categoryAttributesModel.values is Int) {
                            //                        categoryAttributesModel.values = categoryAttributesModel.values as Int - 1
                            //                    }
                            putExtra(PUT_ATTRIBUTES, value)
                        })
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch{
                        delay(400)
                        binding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                            putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                            putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                            //                    if (categoryAttributesModel.values is Int) {
                            //                        categoryAttributesModel.values = categoryAttributesModel.values as Int - 1
                            //                    }
                            putExtra(PUT_ATTRIBUTES, value)
                        })
                    }
                }
            }

        }
    }
}