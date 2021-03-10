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
import kotlinx.android.synthetic.main.dialog_filter_rate.*
import kotlinx.android.synthetic.main.layout_title_filter_search.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment

class FilterRateDialog(val reviews: MutableList<String>, val callback: FilterRateCallback) : BaseBottomSheetDialogFragment() {
    private var selectedReviews = mutableListOf<String>()
    private val arrItem = arrayListOf<TextView>()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_filter_rate, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tvTitle.setText(R.string.danh_gia)
        arrItem.add(tv_1)
        arrItem.add(tv_2)
        arrItem.add(tv_3)
        arrItem.add(tv_4)
        arrItem.add(tv_5)

        selectedReviews.addAll(reviews)
        for (item in selectedReviews) {
            for (itemView in arrItem) {
                if (itemView.text.toString() == item) {
                    itemView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_24px, 0)
                }
            }
        }

        for (itemView in arrItem) {
            itemView.setOnClickListener { tvItem ->
                var isSelected = false
                if (!selectedReviews.isNullOrEmpty()) {
                    for (item in selectedReviews) {
                        if ((tvItem as TextView).text.toString() == item) {
                            isSelected = true
                            break
                        } else {
                            isSelected = false
                        }
                    }
                } else {
                    isSelected = false
                }

                if (isSelected) {
                    itemView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    selectedReviews.remove((tvItem as TextView).text.toString())
                } else {
                    itemView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_24px, 0)
                    selectedReviews.add((tvItem as TextView).text.toString())
                }

            }
        }
        tv_clear.setOnClickListener {
            for (itemView in arrItem) {
                selectedReviews.remove(itemView.text.toString())
                itemView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }

        imgClose.setOnClickListener {
            dismiss()
        }

        tvDone.setOnClickListener {
            selectedReviews.sort()
            callback.setRate(selectedReviews)
            dismiss()
        }
    }

    interface FilterRateCallback {
        fun setRate(listSelected: MutableList<String>)
    }
}