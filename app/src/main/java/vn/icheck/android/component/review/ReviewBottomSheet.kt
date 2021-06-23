package vn.icheck.android.component.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.review_vote_bottom_sheet.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.criterias.ICReviewBottom
import vn.icheck.android.ui.layout.CustomLinearLayoutManager
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.text.ReviewPointText

class ReviewBottomSheet : BottomSheetDialogFragment() {
    var reviewData: ICReviewBottom? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.review_vote_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        reviewData = arguments?.get(Constant.DATA_1) as ICReviewBottom
        if (!reviewData?.message.isNullOrEmpty()) {
            txtContent.visibility = View.VISIBLE
            txtContent.setText(R.string.danh_gia_cua_s, reviewData?.message)
        } else {
            txtContent.visibility = View.GONE
        }

        if (reviewData?.averagePoint != null) {
            ratingReview.rating = reviewData?.averagePoint!!
            tvPoint.text = ReviewPointText.getTextTotal(reviewData?.averagePoint!!)
        }

        if (!reviewData?.customerCriterias.isNullOrEmpty()) {
            recyclerView.layoutManager = context?.let { CustomLinearLayoutManager(it, RecyclerView.VERTICAL, false) }
            recyclerView.adapter = ReviewBottomSheetAdapter(reviewData?.customerCriterias!!)
        }
    }

    companion object {
        fun show(supportFragmentManager: FragmentManager, isCancelable: Boolean, criteria: ICReviewBottom) {
            val showDialog = ReviewBottomSheet()
            showDialog.arguments = Bundle().apply {
                this.putSerializable(Constant.DATA_1, criteria)
            }
            showDialog.isCancelable = isCancelable
            showDialog.show(supportFragmentManager, null)
        }
    }
}