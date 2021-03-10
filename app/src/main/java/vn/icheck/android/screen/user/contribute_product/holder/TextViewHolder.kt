package vn.icheck.android.screen.user.contribute_product.holder

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vn.icheck.android.base.holder.CoroutineViewHolder
import vn.icheck.android.constant.ATTRIBUTES_POSITION
import vn.icheck.android.constant.CONTRIBUTIONS_ACTION
import vn.icheck.android.constant.PUT_ATTRIBUTES
import vn.icheck.android.databinding.ItemTextBinding
import vn.icheck.android.screen.user.contribute_product.viewmodel.CategoryAttributesModel
import vn.icheck.android.util.ick.simpleText

class TextViewHolder(private val itemTextBinding: ItemTextBinding) : CoroutineViewHolder(itemTextBinding.root) {
    fun bind(categoryAttributesModel: CategoryAttributesModel) {
        itemTextBinding.titleText simpleText categoryAttributesModel.categoryItem.name
        itemTextBinding.edtText.hint = "Nhập ${categoryAttributesModel.categoryItem.name}"
        if (categoryAttributesModel.values != null && categoryAttributesModel.values.toString().isNotEmpty()) {
            itemTextBinding.edtText.setText(categoryAttributesModel.values.toString())
        }
        if (categoryAttributesModel.isEditable) {
            itemTextBinding.edtText.addTextChangedListener {
                job = if (job?.isActive == true) {
                    job?.cancel()
                    CoroutineScope(Dispatchers.Main).launch{
                        delay(400)
                        if (it.toString().isNotEmpty()) {
                            itemTextBinding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                                putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                                putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                                putExtra(PUT_ATTRIBUTES, it.toString())
                            })
                        }
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch{
                        delay(400)
                        if (it.toString().isNotEmpty()) {
                            itemTextBinding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                                putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                                putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                                putExtra(PUT_ATTRIBUTES, it.toString())
                            })
                        }
                    }
                }

            }
        } else {
            itemTextBinding.edtText.isFocusable = false
            itemTextBinding.edtText.isClickable = false
        }
    }

    companion object {
        fun create(parent: ViewGroup): TextViewHolder {
            val itemTextBinding = ItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TextViewHolder(itemTextBinding)
        }
    }
}