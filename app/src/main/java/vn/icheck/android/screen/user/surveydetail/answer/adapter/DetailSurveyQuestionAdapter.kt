package vn.icheck.android.screen.user.surveydetail.answer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_detail_survey_question.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICQuestions
import vn.icheck.android.network.models.ICReqDirectSurvey
import vn.icheck.android.screen.user.campaign.calback.IOptionListener
import vn.icheck.android.ui.layout.CustomLinearLayoutManager

class DetailSurveyQuestionAdapter : RecyclerView.Adapter<DetailSurveyQuestionAdapter.ViewHolder>() {
    private val listData = mutableListOf<ICQuestions>()

    fun setData(list: List<ICQuestions>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    /**
     * Lấy câu trả lời theo format của server
     *
     * @return danh sách câu hỏi và đáp án
     */
    val getAnswered: MutableList<ICReqDirectSurvey>?
        get() {
            val respDirectSurvey = mutableListOf<ICReqDirectSurvey>()

            for (q in 0 until listData.size) {
                val resp = ICReqDirectSurvey()

                resp.question_id = q.toLong()

                var isAnswered = false

                for (i in 0 until listData[q].options.size) {
                    if (listData[q].options[i].isChecked) {
                        resp.option_ids.add(i.toLong())
                        isAnswered = true
                    }
                }

                if (!isAnswered) {
                    return null
                }

                respDirectSurvey.add(resp)
            }

            return respDirectSurvey
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_detail_survey_question, parent, false))
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(view: View) : BaseViewHolder<ICQuestions>(view) {

        override fun bind(obj: ICQuestions) {
            itemView.txtTitle.apply {
                background = ViewHelper.bgSecondaryCornersTop10(context)
                text = itemView.context.getString(R.string.cau_xxx, (adapterPosition + 1).toString())
            }
            itemView.txtContent.text = obj.title

            itemView.recyclerView.layoutManager = CustomLinearLayoutManager(itemView.context)

            itemView.recyclerView.adapter = DetailSurveyOptionAdapter(obj.options, obj.type, object : IOptionListener {
                override fun onUnSelectedQuestion(selectedPosition: Int) {
                    itemView.recyclerView.findViewHolderForAdapterPosition(selectedPosition)?.let {
                        if (it is DetailSurveyOptionAdapter.ViewHolder) {
                            it.unSelected()
                        }
                    }
                }
            })
        }
    }
}