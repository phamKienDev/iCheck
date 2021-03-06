package vn.icheck.android.screen.user.contribute_product.holder

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.skydoves.balloon.*
import vn.icheck.android.R
import vn.icheck.android.constant.ATTRIBUTES_POSITION
import vn.icheck.android.constant.CONTRIBUTIONS_ACTION
import vn.icheck.android.constant.PUT_ATTRIBUTES
import vn.icheck.android.databinding.ItemSelectBinding
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.network.model.category.OptionsItem
import vn.icheck.android.screen.user.contribute_product.viewmodel.CategoryAttributesModel
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.setCustomChecked
import vn.icheck.android.util.ick.simpleText

class SelectHolder(private val itemSelectBinding: ItemSelectBinding) : RecyclerView.ViewHolder(itemSelectBinding.root) {
    var balloon: Balloon? = null
    fun bind(categoryAttributesModel: CategoryAttributesModel) {
        if (categoryAttributesModel.categoryItem.description.isNullOrEmpty()) {
            itemSelectBinding.imgHelp.beGone()
//            TooltipCompat.setTooltipText(itemSelectBinding.imgHelp,null)
        } else {
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
            itemSelectBinding.imgHelp.setOnClickListener {
                if (balloon?.isShowing == false) {
                    balloon?.showAlignBottom(itemSelectBinding.imgHelp)
                } else {
                    balloon?.dismiss()
                }
            }
            itemSelectBinding.imgHelp.beVisible()
        }
        if (categoryAttributesModel.categoryItem.required == true) {
            itemSelectBinding.tvTitle.setText(R.string.s_bat_buoc, categoryAttributesModel.categoryItem.name?:"")
        } else {
            itemSelectBinding.tvTitle simpleText categoryAttributesModel.categoryItem.name
        }
        if (categoryAttributesModel.categoryItem.options?.size ?: 0 <= 6) {
            itemSelectBinding.groupSpinner.beGone()
            itemSelectBinding.groupSelect.removeAllViews()
            if (!categoryAttributesModel.categoryItem.options.isNullOrEmpty()) {
                for (item in categoryAttributesModel.categoryItem.options ?: arrayListOf()) {
                    itemSelectBinding.groupSelect.addView(RadioButton(itemSelectBinding.root.context).apply {
                        text = item?.value
                        setTextColor(ViewHelper.createColorStateList(ColorManager.getSecondTextColor(itemView.context),ColorManager.getNormalTextColor(itemView.context)))
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            buttonTintList=ViewHelper.createColorStateList(ContextCompat.getColor(itemView.context,R.color.grayB4),ColorManager.getPrimaryColor(itemView.context))
                        }
                        if (item?.id == categoryAttributesModel.values) {
                            isChecked = true
                        }
                        setCustomChecked(object : CompoundButton.OnCheckedChangeListener {
                            val pos = categoryAttributesModel.categoryItem.options?.indexOf(item) ?: 0
                            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                                if (isChecked) {
                                    itemSelectBinding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                                        putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                                        putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                                        putExtra(PUT_ATTRIBUTES, categoryAttributesModel.categoryItem.options?.get(pos)?.id)
                                    })
                                }
                            }
                        })
                    })

                }
            }
            if (categoryAttributesModel.values != null && categoryAttributesModel.values is Double?) {
                val filter = categoryAttributesModel.categoryItem.options?.firstOrNull {
                    it?.id == (categoryAttributesModel.values as Double).toInt()
                }
                if (filter != null) {
                    itemSelectBinding.groupSelect.check(itemSelectBinding.groupSelect.getChildAt(categoryAttributesModel.categoryItem.options?.indexOf(filter) ?: 0).id )
                }

            } else if (categoryAttributesModel.values != null && categoryAttributesModel.values is String?) {
                itemSelectBinding.groupSelect.addView(RadioButton(itemSelectBinding.root.context).apply {
                    text = categoryAttributesModel.values.toString()
                    this.isChecked = true
                })
            }
        } else {
            itemSelectBinding.groupSpinner.beVisible()

            val arrAdapter = if (!categoryAttributesModel.categoryItem.options.isNullOrEmpty()) {
                if (categoryAttributesModel.categoryItem.options?.firstOrNull()?.id != 0) {
                    categoryAttributesModel.categoryItem.options?.add(0, OptionsItem(0, itemView.context.getString(R.string.tuy_chon)))
                }
                object : ArrayAdapter<OptionsItem>(itemSelectBinding.root.context, android.R.layout.simple_spinner_item, categoryAttributesModel.categoryItem.options ?: arrayListOf()) {
                    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val checkedTextView= super.getDropDownView(position, convertView, parent) as CheckedTextView
                        checkedTextView.typeface= Typeface.createFromAsset(context.assets,"font/barlow_medium.ttf")
                        checkedTextView.setTextColor(if (checkedTextView.text==itemView.context.getString(R.string.tuy_chon)) {
                            ColorManager.getDisableTextColor(context)
                        }else{
                            ColorManager.getNormalTextColor(context)
                        })
                        return checkedTextView
                    }

                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val textView=super.getView(position, convertView, parent) as TextView
                        textView.typeface= Typeface.createFromAsset(context.assets,"font/barlow_medium.ttf")
                        textView.setTextColor(if (textView.text==itemView.context.getString(R.string.tuy_chon)) {
                            ColorManager.getDisableTextColor(context)
                        }else{
                            ColorManager.getNormalTextColor(context)
                        })
                        return textView
                    }
                }
            } else {
                object : ArrayAdapter<OptionsItem>(itemSelectBinding.root.context, android.R.layout.simple_spinner_item, listOf(OptionsItem(0, itemView.context.getString(R.string.tuy_chon)))) {
                    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val checkedTextView= super.getDropDownView(position, convertView, parent) as CheckedTextView
                        checkedTextView.typeface= Typeface.createFromAsset(context.assets,"font/barlow_medium.ttf")
                        checkedTextView.setTextColor(ColorManager.getDisableTextColor(context))
                        return checkedTextView
                    }

                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val textView=super.getView(position, convertView, parent) as TextView
                        textView.typeface= Typeface.createFromAsset(context.assets,"font/barlow_medium.ttf")
                        textView.setTextColor(ColorManager.getDisableTextColor(context))
                        return textView
                    }
                }
            }

            arrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            itemSelectBinding.spinner.adapter = arrAdapter
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                itemSelectBinding.spinner.backgroundTintList=ViewHelper.createColorStateList(ColorManager.getPrimaryColor(itemView.context),ColorManager.getPrimaryColor(itemView.context))
            }
            for (item in categoryAttributesModel.categoryItem.options ?: arrayListOf()) {
                if (categoryAttributesModel.values is Double) {
                    if (item?.id == (categoryAttributesModel.values as Double).toInt()) {
                        itemSelectBinding.spinner.setSelection(categoryAttributesModel.categoryItem.options?.indexOf(item) ?: 0 + 1)
                    }
                }else if (categoryAttributesModel.values is Int) {
                    if (item?.id == categoryAttributesModel.values as Int) {
                        itemSelectBinding.spinner.setSelection(categoryAttributesModel.categoryItem.options?.indexOf(item) ?: 0 + 1)
                    }
                }
            }
            itemSelectBinding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    itemSelectBinding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                        putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                        putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                        putExtra(PUT_ATTRIBUTES, (itemSelectBinding.spinner.selectedItem as OptionsItem?)?.id ?: 0)
                    })
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    itemSelectBinding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                        putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                        putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                        putExtra(PUT_ATTRIBUTES, 0)
                    })
                }
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): SelectHolder {
            val itemSelectBinding = ItemSelectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SelectHolder(itemSelectBinding)
        }
    }
}