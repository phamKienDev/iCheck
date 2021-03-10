package vn.icheck.android.component.product_list_review

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.ICViewModel
import vn.icheck.android.component.product_question_answer.ItemQuestionAdapter
import vn.icheck.android.component.product_question_answer.ProductQuestionModel
import vn.icheck.android.component.product_review.list_review.ItemListReviewModel
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.screen.user.list_product_review.ListProductReviewActivity
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible

class ProductListReviewHolder(parent: ViewGroup, recycledViewPool: RecyclerView.RecycledViewPool?) : RecyclerView.ViewHolder(ViewHelper.createListReviewProductHolder(parent.context)) {
    val adapter = ItemReviewAdapter(1)

    init {
        ((itemView as ViewGroup).getChildAt(1) as RecyclerView).setRecycledViewPool(recycledViewPool)
    }

    fun bind(obj: ProductListReviewModel) {
        (itemView as ViewGroup).apply {
            val list = mutableListOf<ItemListReviewModel>()
            (getChildAt(0) as ViewGroup).apply {
                if (obj.data.isNullOrEmpty()) {
                    beGone()
                } else {
                    beVisible()

                    (getChildAt(0) as AppCompatTextView).text = String.format(context.getString(R.string.danh_gia_san_pham_x), obj.count)

                    getChildAt(1).setOnClickListener {
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_LIST_REVEWS))
                    }

                    obj.data.first().marginTop = SizeHelper.size10
                    for (i in 0 until obj.data.size) {
                        list.add(ItemListReviewModel(obj.data[i]))
                    }
                }
            }

            (getChildAt(1) as RecyclerView).adapter = adapter
            adapter.setData(list)
        }
    }

    fun updateReview(review: ItemListReviewModel) {
        adapter.updateReview(review)
    }
}