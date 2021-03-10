package vn.icheck.android.screen.user.campaign.adapter.direct_survey

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_direct_survey_question.view.*
import vn.icheck.android.R
import vn.icheck.android.network.models.ICQuestions
import vn.icheck.android.screen.user.campaign.calback.IOptionListener
import vn.icheck.android.ui.layout.CustomLinearLayoutManager

class DirectSurveyQuestionAdapter(private val listData: List<ICQuestions>) : RecyclerView.Adapter<DirectSurveyQuestionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_direct_survey_question, parent, false))
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(obj: ICQuestions) {
            itemView.txtTitle.text = obj.title

            itemView.recyclerView.layoutManager = CustomLinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, reverseLayout = false, isScrollEnabled = false)
            itemView.recyclerView.setHasFixedSize(false)

            itemView.recyclerView.adapter = DirectSurveyOptionAdapter(obj.options, obj.type, object : IOptionListener {
                override fun onUnSelectedQuestion(selectedPosition: Int) {
                    itemView.recyclerView.findViewHolderForAdapterPosition(selectedPosition)?.let {
                        if (it is DirectSurveyOptionAdapter.ViewHolder) {
                            it.unSelected()
                        }
                    }
                }
            })
        }
    }
}