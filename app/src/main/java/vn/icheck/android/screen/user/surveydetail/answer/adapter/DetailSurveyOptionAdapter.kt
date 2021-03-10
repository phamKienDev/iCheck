package vn.icheck.android.screen.user.surveydetail.answer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_survey_answer.view.*
import vn.icheck.android.R
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICOptions
import vn.icheck.android.screen.user.campaign.calback.IOptionListener

class DetailSurveyOptionAdapter(private val listData: MutableList<ICOptions>, private val type: String, private val listener: IOptionListener) : RecyclerView.Adapter<DetailSurveyOptionAdapter.ViewHolder>() {

    fun setData(list: MutableList<ICOptions>) {
        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_survey_answer, parent, false))
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listData[position])
    }

    inner class ViewHolder(view: View) : BaseViewHolder<ICOptions>(view) {

        override fun bind(obj: ICOptions) {
            itemView.txtTitle.text = obj.title

            itemView.setOnClickListener {
                if (type == Constant.SELECT) {
                    for (i in listData.size - 1 downTo 0) {
                        listener.onUnSelectedQuestion(i)
                    }
                }

                obj.isChecked = !obj.isChecked
                itemView.txtTitle.isChecked = obj.isChecked
                ViewCompat.setElevation(itemView.txtTitle, if (obj.isChecked) SizeHelper.size4.toFloat() else SizeHelper.size1.toFloat())
            }
        }

        fun unSelected() {
            listData[adapterPosition].isChecked = false
            itemView.txtTitle.isChecked = false
            ViewCompat.setElevation(itemView.txtTitle, SizeHelper.size1.toFloat())
        }
    }
}