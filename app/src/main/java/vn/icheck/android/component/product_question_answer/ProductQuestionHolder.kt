package vn.icheck.android.component.product_question_answer

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.R
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.product.ProductDetailListener
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.ichecklibs.util.setText

class ProductQuestionHolder(parent: ViewGroup, recycledViewPool: RecyclerView.RecycledViewPool?, questionListener: ProductDetailListener) : RecyclerView.ViewHolder(ViewHelper.createListQuestionProductHolder(parent.context)) {
    private val adapter = ItemQuestionAdapter(questionListener)

    init {
        ((itemView as ViewGroup).getChildAt(1) as RecyclerView).setRecycledViewPool(recycledViewPool)
    }

    fun bind(obj: ProductQuestionModel) {
        (itemView as ViewGroup).apply {
            (getChildAt(0) as ViewGroup).apply {
                if (obj.data.rows.isNullOrEmpty()) {
                    beGone()
                } else {
                    beVisible()

                    (getChildAt(0) as AppCompatTextView).setText(R.string.hoi_dap_ve_san_pham_x, obj.data.count)

                    getChildAt(1).setOnClickListener {
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_LIST_QUESTIONS))
                    }
                }
            }

            (getChildAt(1) as RecyclerView).adapter = adapter
            adapter.setData(obj.data.rows)

            getChildAt(2).setOnClickListener {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_LIST_QUESTIONS, true))
            }
        }
    }
}