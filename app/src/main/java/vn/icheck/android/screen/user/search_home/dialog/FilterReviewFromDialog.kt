package vn.icheck.android.screen.user.search_home.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_filter_review_from.*
import kotlinx.android.synthetic.main.layout_title_filter_search.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.ichecklibs.ViewHelper

class FilterReviewFromDialog(val from: MutableList<String>, val callback: ReviewFromCallback) : BaseBottomSheetDialogFragment() {
    val arrItem = mutableListOf<TextView>()
    private var selectedFrom = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_filter_review_from, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvTitle.setText(R.string.nguoi_dang)
        arrItem.add(tv_friend)
        arrItem.add(tv_friend_2)
        arrItem.add(tv_page)

        selectedFrom.addAll(from)
        checkSelected(selectedFrom)

        for (itemView in arrItem) {
            itemView.setOnClickListener { tvItem ->
                var isSelected = false
                if (!selectedFrom.isNullOrEmpty()) {
                    for (item in selectedFrom) {
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
                    selectedFrom.remove((tvItem as TextView).text.toString())
                } else {
                    tv_all.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                    itemView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_24px, 0)
                    selectedFrom.add((tvItem as TextView).text.toString())
                }

            }
        }

        tv_all.setOnClickListener {
            selectedFrom.clear()
            checkSelected(selectedFrom)
        }

        imgClose.setOnClickListener {
            dismiss()
        }

        tv_clear.apply {
            background = ViewHelper.bgOutlinePrimary1Corners4(context)
            setOnClickListener {
                selectedFrom.clear()
                checkSelected(selectedFrom)
            }
        }

        tvDone.setOnClickListener {
            dismiss()
            selectedFrom.sort()
            callback.getFrom(selectedFrom)
        }
    }

    private fun checkSelected(from: MutableList<String>?) {
        if (!from.isNullOrEmpty()) {
            tv_all.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            for (item in from) {
                for (itemView in arrItem) {
                    if (itemView.text.toString() == item) {
                        itemView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_24px, 0)
                    }
                }
            }
        } else {
            tv_all.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_checkbox_single_on_24px, 0)
            for (itemView in arrItem) {
                itemView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }

    interface ReviewFromCallback {
        fun getFrom(from: MutableList<String>?)
    }
}