package vn.icheck.android.screen.dialog

import android.content.Context
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.dialog_shop_sort.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog

abstract class SortShopSettingDiaLog(context: Context, private var type: Int) : BaseBottomSheetDialog(context, R.layout.dialog_shop_sort, true) {

    fun show() {
        setColor()

        when (type) {
            0 -> {
                dialog.tvDefault.setTextColor(ContextCompat.getColor(dialog.context, R.color.errorColor))
            }
            1 -> {
                dialog.tvLowtoHigh.setTextColor(ContextCompat.getColor(dialog.context, R.color.errorColor))
            }
            2 -> {
                dialog.tvHightoLow.setTextColor(ContextCompat.getColor(dialog.context, R.color.errorColor))
            }
            3 -> {
                dialog.tvNew.setTextColor(ContextCompat.getColor(dialog.context, R.color.errorColor))
            }
            4 -> {
                dialog.tvTheBestSeller.setTextColor(ContextCompat.getColor(dialog.context, R.color.errorColor))
            }
        }

        initListener()

        dialog.show()
    }

    private fun initListener() {
        dialog.tvClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.tvDefault.setOnClickListener {
            dialog.dismiss()
            onClickDefault(0)
        }

        dialog.tvLowtoHigh.setOnClickListener {
            dialog.dismiss()
            onClickLowToHight(1)
        }

        dialog.tvHightoLow.setOnClickListener {
            dialog.dismiss()
            onClickHightToLow(2)
        }

        dialog.tvNew.setOnClickListener {
            dialog.dismiss()
            onClickNew(3)
        }

        dialog.tvTheBestSeller.setOnClickListener {
            dialog.dismiss()
            onClickTheBestSeller(4)
        }
    }

    private fun setColor() {
        dialog.tvDefault.setTextColor(ContextCompat.getColor(dialog.context, R.color.light_blue))
        dialog.tvLowtoHigh.setTextColor(ContextCompat.getColor(dialog.context, R.color.light_blue))
        dialog.tvHightoLow.setTextColor(ContextCompat.getColor(dialog.context, R.color.light_blue))
        dialog.tvNew.setTextColor(ContextCompat.getColor(dialog.context, R.color.light_blue))
        dialog.tvTheBestSeller.setTextColor(ContextCompat.getColor(dialog.context, R.color.light_blue))
    }

    protected abstract fun onClickDefault(type: Int)
    protected abstract fun onClickLowToHight(type: Int)
    protected abstract fun onClickHightToLow(type: Int)
    protected abstract fun onClickNew(type: Int)
    protected abstract fun onClickTheBestSeller(type: Int)
}