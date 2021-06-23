package vn.icheck.android.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bottom_xmbrv.view.*
import kotlinx.android.synthetic.main.progress_ctsp_bottom.view.*
import vn.icheck.android.R
import vn.icheck.android.fragments.ProductReviewsBottomDialog
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.DetailStampHoaPhatActivity
import vn.icheck.android.screen.user.detail_stamp_thinh_long.home.DetailStampThinhLongActivity

class BottomReviewsAdapter(val data: List<ProductReviewsBottomDialog.BottomChild>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        const val TYPE_PROGRESS = 1
        const val TYPE_XMB = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_PROGRESS -> ProgressHolder.create(parent)
            TYPE_XMB -> XmbHolder.create(parent)
            else -> ProgressHolder.create(parent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return data.get(position).type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (data.get(position).type) {
            TYPE_PROGRESS -> (holder as ProgressHolder).bind(data.get(position) as ProductReviewsBottomDialog.ProgressBottom)
            TYPE_XMB -> (holder as XmbHolder).bind((data.get(position) as ProductReviewsBottomDialog.BottomBehave).listener)
        }
    }

    class XmbHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(listener: () -> Unit) {
            view.btn_xall.background=ViewHelper.btnWhiteStroke1Corners36(view.context)
            view.btn_xall.setOnClickListener {
                listener()
                DetailStampHoaPhatActivity.INSTANCE?.showAllReviews()
                DetailStampThinhLongActivity.INSTANCE?.showAllReviews()
            }
        }

        companion object{
            fun create(parent: ViewGroup): XmbHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.bottom_xmbrv, parent, false)
                return XmbHolder(view)
            }
        }
    }

    class ProgressHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(progressBottom: ProductReviewsBottomDialog.ProgressBottom) {
            itemView.tv_title.text = progressBottom.name
            itemView.tv_score.apply {
                text = context.getString(R.string.format_1_f, progressBottom.point?.times(2))
            }
            progressBottom.point?.let {
                itemView.tv_progress.progress = (it * 20).toInt()
            }
        }

        companion object{
            fun create(parent: ViewGroup): ProgressHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.progress_ctsp_bottom, parent, false)
                return ProgressHolder(view)
            }
        }
    }
}