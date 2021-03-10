package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_holder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.ctsp_hdsp_stamp_holder.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.call_back.SlideHeaderStampHoaPhatListener
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.model.ICQAStamp

class QuestionAnswerStampHolder(parent: ViewGroup,private val headerImagelistener: SlideHeaderStampHoaPhatListener) : BaseViewHolder<ICQAStamp>(LayoutInflater.from(parent.context).inflate(R.layout.ctsp_hdsp_stamp_holder, parent, false)) {

    override fun bind(obj: ICQAStamp) {
        itemView.tv_dgsp.text = String.format("Hỏi đáp về sản phẩm (%d)", obj.size)
        itemView.tv_first_question.text = obj.first

        itemView.tv_xtcrv.setOnClickListener {
            headerImagelistener.viewAllQa()
        }

        if (obj.second.isNullOrEmpty()) {
            itemView.tv_second_question.visibility = View.GONE
        } else {
            itemView.tv_second_question.visibility = View.VISIBLE
            itemView.tv_second_question.text = obj.second
        }
    }
}