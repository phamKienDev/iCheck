package vn.icheck.android.screen.user.search_home.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.dialog_filter_review.*
import kotlinx.android.synthetic.main.layout_title_filter_search.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.ichecklibs.Constant

class FilterReviewDialog(isWatched: Boolean = false, val time: MutableList<String>? = null, val from: MutableList<String>? = null, val callback: ReviewCallback) : BaseBottomSheetDialogFragment() {

    private var seletedWatched = isWatched
    private var seletedTime = mutableListOf<String>()
    private var seletedFrom = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_filter_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        seletedTime.addAll(time ?: mutableListOf())
        seletedFrom.addAll(from ?: mutableListOf())

        setYear(seletedTime)
        setFrom(seletedFrom)
        switch_watched.isChecked = seletedWatched

        imgClose.setOnClickListener {
            dismiss()
        }
        containerDate.setOnClickListener {
            FilterYearDialog(seletedTime, object : FilterYearDialog.TimeCallBack {
                override fun getTime(obj: MutableList<String>?) {
                    setYear(obj)
                }
            }).show(parentFragmentManager, null)
        }

        containerFrom.setOnClickListener {
            FilterReviewFromDialog(seletedFrom, object : FilterReviewFromDialog.ReviewFromCallback {
                override fun getFrom(from: MutableList<String>?) {
                    setFrom(from)
                }
            }).show(parentFragmentManager, null)
        }

        tv_clear.setOnClickListener {
            setFrom(null)
            setYear(null)
            switch_watched.isChecked = false

        }

        tvDone.setOnClickListener {
            callback.filterReview(switch_watched.isChecked, seletedTime, seletedFrom)
            dismiss()
        }

        img_clear_from.setOnClickListener {
            setFrom(null)
        }

        img_clear_time.setOnClickListener {
            setYear(null)
        }
    }

    fun setYear(year: MutableList<String>?) {
        seletedTime = year ?: mutableListOf()
        if (year.isNullOrEmpty()) {
            tv_time.setTextColor(Constant.getSecondTextColor(ICheckApplication.getInstance()))
            img_clear_time.visibility = View.GONE
            tv_time.text = getString(R.string.tat_ca)
        } else {
            tv_time.setTextColor(vn.icheck.android.ichecklibs.Constant.getPrimaryColor(dialog!!.context))
            tv_time.compoundDrawablePadding = SizeHelper.size8
            img_clear_time.visibility = View.VISIBLE
            tv_time.text = year.toString().substring(1, year.toString().length - 1)

            val listString = mutableListOf<String>()
            for (i in 0 until if (year.size > 3) {
                3
            } else {
                year.size
            }) {
                listString.add(year[i])
            }

            var yearString = listString.toString().substring(1, listString.toString().length - 1)
            if (year.size > 3) {
                yearString = "$yearString,..."
            }

            tv_time.text = yearString
        }
    }

    fun setFrom(fromType: MutableList<String>?) {
        seletedFrom = fromType ?: mutableListOf()
        if (fromType.isNullOrEmpty()) {
            tv_from.setTextColor(Constant.getSecondTextColor(ICheckApplication.getInstance()))
            img_clear_from.visibility = View.GONE
            tv_from.text = getString(R.string.moi_nguoi)
        } else {
            tv_from.setTextColor(vn.icheck.android.ichecklibs.Constant.getPrimaryColor(tv_from.context))
            tv_from.compoundDrawablePadding = SizeHelper.size8
            img_clear_from.visibility = View.VISIBLE
            tv_from.text = fromType.toString().substring(1, fromType.toString().length - 1)
        }
    }

    interface ReviewCallback {
        fun filterReview(isWatched: Boolean, time: MutableList<String>?, from: MutableList<String>?)
    }
}