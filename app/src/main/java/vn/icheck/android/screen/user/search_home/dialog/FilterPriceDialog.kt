package vn.icheck.android.screen.user.search_home.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_filter_price.*
import kotlinx.android.synthetic.main.layout_title_filter_search.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.ichecklibs.ViewHelper

class FilterPriceDialog(price: String?, val callback: FilterPriceCallback) : BaseBottomSheetDialogFragment() {

    private val arrItem = arrayListOf<TextView>()
    var selectedPrice = price

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_filter_price, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tvTitle.setText(R.string.gia_tu)

        arrItem.add(tv_all)
        arrItem.add(tv_friend)
        arrItem.add(tv_friend_2)

        if (selectedPrice != null) {
            for (item in arrItem) {
                if (item.text.toString() == selectedPrice) {
                    selectedItem(item)
                }
            }
        } else {
            selectedItem(tv_all)
        }


        for (item in arrItem) {
            item.setOnClickListener {
                selectedItem(item)
            }
        }

        imgClose.setOnClickListener {
            dismiss()
        }

        tvDone.setOnClickListener {
            callback.selectFilter(selectedPrice)
            dismiss()
        }

        tv_clear.apply {
            background = ViewHelper.bgOutlinePrimary1Corners4(context)
            setOnClickListener {
                selectedItem(tv_all)
            }
        }
    }

    private fun selectedItem(tv: TextView) {
        uncheck()
        selectedPrice = tv.text.toString()
        tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_24px, 0)
    }

    private fun uncheck() {
        for (item in arrItem) {
            item.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }

    interface FilterPriceCallback {
        fun selectFilter(price: String?)
    }
}