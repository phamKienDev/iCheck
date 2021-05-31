package vn.icheck.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_ctsp_srv.*
import vn.icheck.android.R
import vn.icheck.android.adapters.BottomReviewsAdapter
import vn.icheck.android.adapters.BottomReviewsAdapter.Companion.TYPE_PROGRESS
import vn.icheck.android.adapters.BottomReviewsAdapter.Companion.TYPE_XMB
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICCriteria
import vn.icheck.android.util.text.ReviewPointText

class ProductReviewsBottomDialog: BottomSheetDialogFragment() {

    private var icCriteria:ICCriteria? = null
    private lateinit var bottomReviewsAdapter: BottomReviewsAdapter
    private val listData = mutableListOf<BottomChild>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            dialog?.context?.let { it1 -> bottomSheet?.background = ViewHelper.bgWhiteRadiusTop16(it1) }
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return inflater.inflate(R.layout.bottom_ctsp_srv, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomReviewsAdapter = BottomReviewsAdapter(listData)
        bottom_rcv.isNestedScrollingEnabled = false
        bottom_rcv.adapter = bottomReviewsAdapter
        bottom_rcv.layoutManager = LinearLayoutManager(context)
        arguments?.let {
            icCriteria = it.get("criteria") as ICCriteria
        }
        icCriteria?.let {
            tv_score.text = String.format("%.1f", it.productEvaluation?.averagePoint?.times(2))
            it.productEvaluation?.averagePoint?.let {
                tv_quality.text = ReviewPointText.getText(it)
            }

            tv_total_reviews.text = String.format("%,d đánh giá", it.totalReviews)
        }
        icCriteria?.productGather?.let {

            for (item in it) {
                listData.add(ProgressBottom(item.criteria.name, item.averagePoint))
            }
            listData.add(BottomBehave {
                dismiss()
            })
            bottomReviewsAdapter.notifyItemInserted(0)
        }
        btn_clear.setOnClickListener {
            dismiss()
        }
    }

    open class BottomChild(val type: Int)

    class BottomBehave(val listener: () -> Unit):BottomChild(TYPE_XMB)

    class ProgressBottom(val name: String?, val point: Float?):BottomChild(TYPE_PROGRESS)
}