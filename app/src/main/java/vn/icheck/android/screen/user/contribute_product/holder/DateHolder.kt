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
import vn.icheck.android.constant.CHOOSE_DATE
import vn.icheck.android.constant.CONTRIBUTIONS_ACTION
import vn.icheck.android.constant.PUT_ATTRIBUTES
import vn.icheck.android.databinding.ItemDateBinding
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.user.contribute_product.viewmodel.CategoryAttributesModel
import vn.icheck.android.util.date.DatePickerFragment
import vn.icheck.android.util.date.OnDatePicked
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.ick.getDayTime
import vn.icheck.android.util.ick.simpleText
import vn.icheck.android.util.kotlin.ToastUtils
import java.text.SimpleDateFormat
import java.util.*

class DateHolder(val binding: ItemDateBinding) : RecyclerView.ViewHolder(binding.root) {
    var balloon: Balloon? = null
    fun bind(categoryAttributesModel: CategoryAttributesModel) {
        binding.edtText.background=ViewHelper.bgWhiteStrokeLineColor1Corners4(itemView.context)
        if (categoryAttributesModel.categoryItem.description.isNullOrEmpty()) {
            binding.imgHelp.beGone()
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
            binding.titleText simpleText categoryAttributesModel.categoryItem.name + " (*)"
        } else {
            binding.titleText simpleText categoryAttributesModel.categoryItem.name
        }
        if (categoryAttributesModel.values != null) {
            binding.edtText.setText(categoryAttributesModel.values.toString().getDayTime())
        } else {
            binding.edtText.setText("")
        }

        binding.edtText.setOnClickListener {
            if (categoryAttributesModel.fragmentManager != null && categoryAttributesModel.calendar != null) {
                val pickDate = DatePickerFragment(object : OnDatePicked {
                    override fun picked(year: Int, month: Int, day: Int) {
                        val days = if (day < 10) {
                            "0$day"
                        } else {
                            "$day"
                        }
                        val months = if (month + 1 < 10) {
                            "0${month + 1}"
                        } else {
                            "${month + 1}"
                        }
                        val timeInMills = Calendar.getInstance().timeInMillis
//                        ickLoginViewModel.calendar.set(year, month, day)
//                        if (timeInMills < ickLoginViewModel.calendar.timeInMillis) {
//                            ToastUtils.showShortError(requireContext(), "Không cho phép chọn ngày sinh là ngày tương lai!")
//                        } else {
                        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
//                            ickLoginViewModel.setBirthDay(df.format(ickLoginViewModel.calendar.time))
                        binding.edtText.setText("$days/$months/$year")
                        val c = Calendar.getInstance()
                        c.set(year, month, day)
                        binding.root.context.sendBroadcast(Intent(CONTRIBUTIONS_ACTION).apply {
                            putExtra(CONTRIBUTIONS_ACTION, PUT_ATTRIBUTES)
                            putExtra(ATTRIBUTES_POSITION, bindingAdapterPosition)
                            putExtra(PUT_ATTRIBUTES, df.format(c.time))
                        })
//                        }
                    }
                }, categoryAttributesModel.calendar!!, true)

                pickDate.show(categoryAttributesModel.fragmentManager!!, null)
            }

        }
    }

    companion object {
        fun create(parent: ViewGroup): DateHolder {
            val binding = ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return DateHolder(binding)
        }
    }
}