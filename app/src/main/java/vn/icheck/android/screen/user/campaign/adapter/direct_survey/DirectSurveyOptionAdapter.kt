package vn.icheck.android.screen.user.campaign.adapter.direct_survey

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_survey_answer.view.*
import vn.icheck.android.R
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICOptions
import vn.icheck.android.screen.user.campaign.calback.IOptionListener

class DirectSurveyOptionAdapter(private val listData: List<ICOptions>, private val type: String, private val listener: IOptionListener) : RecyclerView.Adapter<DirectSurveyOptionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_survey_answer, parent, false))
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            listData[adapterPosition].let { options ->
                itemView.txtTitle.text = options.title
                itemView.txtTitle.isChecked = options.isChecked
                ViewCompat.setElevation(itemView.txtTitle, if (options.isChecked) SizeHelper.size4.toFloat() else SizeHelper.size1.toFloat())
            }

            itemView.setOnClickListener {
                if (type == Constant.SELECT) {
                    for (i in listData.size - 1 downTo 0) {
                        if (listData[i].isChecked) {
                            listData[i].isChecked = false
                            listener.onUnSelectedQuestion(i)
                        }
                    }
                }

                listData[adapterPosition].let { options ->
                    val isChecked = !options.isChecked
                    options.isChecked = isChecked
                    itemView.txtTitle.isChecked = isChecked
                    ViewCompat.setElevation(itemView.txtTitle, if (options.isChecked) SizeHelper.size4.toFloat() else SizeHelper.size1.toFloat())
                }
            }
        }

        fun unSelected() {
            itemView.txtTitle.isChecked = false
            ViewCompat.setElevation(itemView.txtTitle, SizeHelper.size1.toFloat())
        }
    }
}