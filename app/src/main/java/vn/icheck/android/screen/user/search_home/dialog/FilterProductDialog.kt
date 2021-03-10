package vn.icheck.android.screen.user.search_home.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_filter_product_search.*
import kotlinx.android.synthetic.main.layout_title_filter_search.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment

class FilterProductDialog(val listener: FilterProductCallback, verify: Boolean, price: String?, val reviews: MutableList<String>?) : BaseBottomSheetDialogFragment() {

    private var selectedVerify = verify
    private var selectedPrice = price
    private var selectedReviews = mutableListOf<String>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_filter_product_search, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        selectedReviews.addAll(reviews ?: mutableListOf())

        product_verified_switch.isChecked = selectedVerify
        setPrice(selectedPrice)
        setReview(selectedReviews)

        containerPrice.setOnClickListener {
            val filterPriceDialog = FilterPriceDialog(tv_price.text.toString(), object : FilterPriceDialog.FilterPriceCallback {
                override fun selectFilter(price: String?) {
                    setPrice(price)
                }
            })
            filterPriceDialog.show(parentFragmentManager, null)
        }

        containerReview.setOnClickListener {
            val filterRateDialog = FilterRateDialog(selectedReviews, object : FilterRateDialog.FilterRateCallback {
                override fun setRate(listSelected: MutableList<String>) {
                    setReview(listSelected)
                }
            })
            filterRateDialog.show(parentFragmentManager, null)
        }

        imgClose.setOnClickListener {
            dismiss()
        }

        img_clear_price.setOnClickListener {
            setPrice(null)
        }

        img_clear_review.setOnClickListener {
            setReview(null)
        }

        tv_clear.setOnClickListener {
            product_verified_switch.isChecked = false
            setReview(null)
            setPrice(null)
        }

        tvDone.setOnClickListener {
            listener.onSelected(product_verified_switch.isChecked, selectedPrice, selectedReviews)
            dismiss()
        }
    }

    fun setReview(listSelected: MutableList<String>?) {
        selectedReviews = listSelected ?: mutableListOf()
        if (listSelected.isNullOrEmpty()) {
            tv_all_review.setText(R.string.tat_ca)
            tv_all_review.setTextColor(Color.parseColor("#757575"))
            img_clear_review.visibility = View.GONE
        } else {
            val listRate = mutableListOf<String>()
            for (i in 0 until if (listSelected.size > 3) {
                3
            } else {
                listSelected.size
            }) {
                listRate.add(listSelected[i])
            }

            var rates = listRate.toString().substring(1, listRate.toString().length - 1)
            if (listSelected.size > 3) {
                rates = "$rates,..."
            }

            tv_all_review.text = rates
            tv_all_review.setTextColor(Color.parseColor("#057DDA"))
            img_clear_review.visibility = View.VISIBLE
        }
    }

    fun setPrice(price: String?) {
        selectedPrice = price
        if (price == ICheckApplication.getInstance().getString(R.string.tat_ca) || price == null) {
            tv_price.setText(R.string.tat_ca)
            tv_price.setTextColor(Color.parseColor("#757575"))
            img_clear_price.visibility = View.GONE
        } else {
            tv_price.setText(price)
            tv_price.setTextColor(Color.parseColor("#057DDA"))
            img_clear_price.visibility = View.VISIBLE
        }
    }

    interface FilterProductCallback {
        fun onSelected(verify: Boolean, price: String?, reviews: MutableList<String>?)
    }
}