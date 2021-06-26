package vn.icheck.android.screen.dialog

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.dialog_setting_point.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.date_time.callback.DateTimePickerListener
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.ViewHelper

abstract class CointSettingDialog(context: Context, var type: Int, val begin: String, val end: String) : BaseBottomSheetDialog(context, R.layout.dialog_setting_point, true) {

    fun show() {
        if (begin.isNotEmpty() || end.isNotEmpty() || type != 0) {
            setButton(true)
        }

        setupView()

        dialog.txtBegin.text = begin
        dialog.txtEnd.text = end



        when (type) {
            1 -> {
                dialog.radioXuVao.isChecked = true
            }
            2 -> {
                dialog.radioXuRa.isChecked = true
            }
            else -> {
                dialog.radioAll.isChecked = true
            }
        }

        dialog.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioXuRa -> {
                    setButton(true)
                    type = 2
                }
                R.id.radioXuVao -> {
                    setButton(true)
                    type = 1
                }
                else -> {
                    type = 0
                }
            }
        }

        dialog.imgClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.txtSettingAgain.setOnClickListener { settingAgain() }

        dialog.txtBegin.setOnClickListener {
            if (dialog.txtEnd.text.toString().isNotEmpty()) {
                TimeHelper.datePicker(dialog.context, dialog.txtBegin.text.toString(), "", dialog.txtEnd.text.toString(), object : DateTimePickerListener {
                    override fun onSelected(dateTime: String, milliseconds: Long) {
                        dialog.txtBegin.text = dateTime
                        setButton(true)
                    }
                })
            } else {
                TimeHelper.datePicker(dialog.context, dialog.txtBegin.text.toString(), "", TimeHelper.getDateTime(System.currentTimeMillis()), object : DateTimePickerListener {
                    override fun onSelected(dateTime: String, milliseconds: Long) {
                        dialog.txtBegin.text = dateTime
                        setButton(true)
                    }
                })
            }
        }

        dialog.txtEnd.setOnClickListener {
            TimeHelper.datePicker(dialog.context, dialog.txtEnd.text.toString(), dialog.txtBegin.text.toString(), TimeHelper.getDateTime(System.currentTimeMillis()), object : DateTimePickerListener {
                override fun onSelected(dateTime: String, milliseconds: Long) {
                    dialog.txtEnd.text = dateTime
                    setButton(true)
                }
            })

        }

        dialog.txtFinish.setOnClickListener {
            dialog.dismiss()
            onDone(type, dialog.txtBegin.text.toString(), dialog.txtEnd.text.toString())
        }

        dialog.show()
    }

    private fun setupView() {
        dialog.txtSettingAgain.background = ViewHelper.bgTransparentStrokeLineColor1Corners4(dialog.context)
        dialog.txtBegin.background = ViewHelper.bgTransparentStrokeLineColor1Corners4(dialog.context)
        dialog.txtEnd.background = ViewHelper.bgTransparentStrokeLineColor1Corners4(dialog.context)

        dialog.txtBegin.setTextColor(ColorManager.getNormalTextColor(dialog.context))
        dialog.txtEnd.setTextColor(ColorManager.getNormalTextColor(dialog.context))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewHelper.createColorStateList(ContextCompat.getColor(dialog.context,R.color.grayB4),ColorManager.getPrimaryColor(dialog.context)).apply {
                dialog.radioAll.buttonTintList=this
                dialog.radioXuRa.buttonTintList=this
                dialog.radioXuVao.buttonTintList=this
            }
        }
    }

    private fun settingAgain() {
        type = 0
        dialog.radioAll.isChecked = true
        dialog.txtBegin.text = ""
        dialog.txtEnd.text = ""
        setButton(false)
    }

    private fun setButton(type: Boolean) {
        if (type) {
            dialog.txtSettingAgain.setTextColor( ColorManager.getPrimaryColor(dialog.context))
            dialog.txtSettingAgain.background = ViewHelper.bgOutlinePrimary1Corners4(dialog.context)
            dialog.txtSettingAgain.isEnabled = true
        } else {
            dialog.txtSettingAgain.setTextColor(ColorManager.getDisableTextColor(dialog.context))
            dialog.txtSettingAgain.background=ViewHelper.bgTransparentStrokeLineColor1Corners4(dialog.context)
            dialog.txtSettingAgain.isEnabled = false
        }
    }

    protected abstract fun onDone(type: Int, begin: String, end: String)
}