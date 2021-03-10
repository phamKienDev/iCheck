package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.view_holder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.ctsp_no_question_holder.view.*
import vn.icheck.android.R
import vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.call_back.SlideHeaderStampHoaPhatListener

class NoQuestionStampHolder(parent: ViewGroup, private val headerImagelistener: SlideHeaderStampHoaPhatListener) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ctsp_no_question_holder, parent, false)) {
    init {
        itemView.btn_ask_question.setOnClickListener {
            headerImagelistener.viewAllQa()
        }
    }
}