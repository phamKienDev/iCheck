package vn.icheck.android.component.product.mbtt

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.product_detail_mbtt2.view.*
import vn.icheck.android.R
import vn.icheck.android.adapters.base.BaseHolder
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.screen.user.product_detail.product.IckProductDetailActivity

class MbttHolder(view: View) : BaseHolder(view) {

    @SuppressLint("SetTextI18n")
    fun bind(obj: MbttModel) {
        itemView.progressbar.progressDrawable = ViewHelper.progressPrimaryBackgroundTransparentCorners8(itemView.context)

        showVoteLayout()
        hideLayoutProgress()

        ViewHelper.bgOutlinePrimary1Corners4(itemView.context).apply {
            itemView.btn_yes.background = this
            itemView.btn_no.background = this
        }

        setOnClick(R.id.btn_yes) { view ->
            view.isClickable = false
            IckProductDetailActivity.INSTANCE?.vote(true, obj.idProduct)
            Handler().postDelayed({
                view.isClickable = true
            }, 2000)
        }

        setOnClick(R.id.btn_no) {view ->
            view.isClickable = false
            IckProductDetailActivity.INSTANCE?.vote(false, obj.idProduct)
            Handler().postDelayed({
                view.isClickable = true
            }, 2000)
        }

        if (obj.icTransparency.isVoted != null) {
            hideVoteLayout()
            showProgressLayout()

            val totalYes = if (obj.icTransparency.totalVoted != null) (((obj.icTransparency.totalVoted
                    ?: 1).toDouble() / ((obj.icTransparency.total)
                    ?: 1).toDouble()) * 100).toInt() else 0
            val totalNo = if (obj.icTransparency.totalNoVoted != null) ((obj.icTransparency.totalNoVoted!!.toDouble() / ((obj.icTransparency.total)
                    ?: 1).toDouble()) * 100).toInt() else 0


            val progress = getProgressBar(R.id.progressbar)

            val tvYes = getTv(R.id.tvPercentYes)
            val tvNo = getTv(R.id.tvPercentNo)
            val textHintYes = getTv(R.id.textHintYes)
            val textHintNo = getTv(R.id.textHintNo)
            tvYes.text = "$totalYes%"
            tvNo.text = "$totalNo%"

            val primaryColor = ColorManager.getPrimaryColor(itemView.context)

            if (obj.icTransparency.isVoted!!) {
                progress.progress = totalYes
                progress.rotation = 0f

                tvYes.setTextColor(primaryColor)
                textHintYes.setTextColor(primaryColor)
                tvYes.textSize = 16f

                tvNo.setTextColor(ColorManager.getSecondTextColor(itemView.context))
                textHintNo.setTextColor(ColorManager.getSecondTextColor(itemView.context))
                tvNo.textSize = 14f
            } else {
                progress.progress = totalNo
                progress.rotation = 180f

                tvYes.setTextColor(ColorManager.getSecondTextColor(itemView.context))
                textHintYes.setTextColor(ColorManager.getSecondTextColor(itemView.context))
                tvYes.textSize = 14f

                tvNo.setTextColor(primaryColor)
                textHintNo.setTextColor(primaryColor)
                tvNo.textSize = 16f
            }
        } else {
            showVoteLayout()
            hideLayoutProgress()
        }
    }

    private fun showProgressLayout() {
        showView(R.id.tvPercentYes)
        showView(R.id.tvPercentNo)
        showView(R.id.progressbar)
        showView(R.id.bgProgress)
        showView(R.id.textHintYes)
        showView(R.id.textHintNo)
    }

    private fun hideVoteLayout() {
        hideView(R.id.btn_yes)
        hideView(R.id.btn_no)
        itemView.tvName.setText(R.string.cau_tra_loi_cua_ban_da_duoc_ghi_nhan)
    }

    private fun hideLayoutProgress() {
        hideView(R.id.tvPercentYes)
        hideView(R.id.tvPercentNo)
        hideView(R.id.progressbar)
        hideView(R.id.textHintYes)
        hideView(R.id.textHintNo)
        hideView(R.id.bgProgress)
    }

    private fun showVoteLayout() {
        showView(R.id.btn_yes)
        showView(R.id.btn_no)
    }

    companion object {
        fun create(parent: ViewGroup): MbttHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.product_detail_mbtt2, parent, false)
            return MbttHolder(view)
        }
    }

}