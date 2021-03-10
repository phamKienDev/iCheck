package vn.icheck.android.activities.product.product_questions_v1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_product_answer.view.*
import kotlinx.android.synthetic.main.item_product_answer.view.imgItem
import kotlinx.android.synthetic.main.item_product_answer.view.imgVerified
import kotlinx.android.synthetic.main.item_product_answer.view.rcv_send_image
import kotlinx.android.synthetic.main.item_product_answer.view.tvMessage
import kotlinx.android.synthetic.main.item_product_answer.view.tvName
import kotlinx.android.synthetic.main.item_product_answer.view.tvTimeLeft
import vn.icheck.android.R
import vn.icheck.android.base.adapter.HorizontalImageAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.activities.product.product_questions_v1.view.IProductQuestionsView
import vn.icheck.android.network.models.v1.ICQuestionsAnswers
import vn.icheck.android.util.kotlin.WidgetUtils


class ProductAnswerAdapter(val questionPosition: Int, val listener: IProductQuestionsView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listData = mutableListOf<ICQuestionsAnswers>()
    private var questionId = -1L
    private var totalCount = 0

    fun setQuestionId(id: Long) {
        questionId = id
    }


    fun setData(list: MutableList<ICQuestionsAnswers>, count: Int) {
        listData.clear()
        listData.addAll(list)
        totalCount = count
        notifyDataSetChanged()
    }

    fun clearData(){
        listData.clear()
        notifyDataSetChanged()
    }

    fun addList(list: MutableList<ICQuestionsAnswers>) {
        val currentPosition = listData.size
        notifyItemChanged(currentPosition-1)
        listData.addAll(list)
        notifyItemRangeInserted(currentPosition, list.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AnswerHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product_answer, parent, false))
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as AnswerHolder
        holder.bind(listData[position])
    }

    inner class AnswerHolder(view: View) : BaseViewHolder<ICQuestionsAnswers>(view) {

        override fun bind(obj: ICQuestionsAnswers) {

            if(obj.actor.type==null){
                WidgetUtils.loadImageUrlRounded4(itemView.imgItem, obj.actor.avatarThumbnails?.original, R.drawable.img_shop_default)
            }else{
                when (obj.actor.type) {
                    "user" -> {
                        WidgetUtils.loadImageUrlRounded4(itemView.imgItem, obj.actor.avatarThumbnails?.original, R.drawable.ic_user_orange_circle)
                    }
                    "page" -> {
                        WidgetUtils.loadImageUrlRounded4(itemView.imgItem, obj.actor.avatarThumbnails?.original, R.drawable.img_default_business_logo)
                    }
                    else -> {
                        WidgetUtils.loadImageUrlRounded4(itemView.imgItem, obj.actor.avatarThumbnails?.original, R.drawable.img_shop_default)
                    }
                }
            }


            itemView.tvName.text = obj.actor.name
            itemView.tvMessage.text = obj.content

            val date=TimeHelper.simpleDateFormatVn("yyyy-MM-dd'T'HH:mm:ss").parse(obj.created_at)
            itemView.tvTimeLeft.text = TimeHelper.convertDateTimeSvToCurrentTime(
                    TimeHelper.simpleDateFormatSv.format(date)
            )

            itemView.imgItem.setOnClickListener {
                listener.onClickDetailUser(obj.actor.type,obj.actor.id)
            }
            itemView.tvName.setOnClickListener {
                listener.onClickDetailUser(obj.actor.type,obj.actor.id)
            }

            if (adapterPosition == itemCount - 1) {
                if (totalCount > listData.size) {
                    itemView.tvAnswerCount.visibility = View.VISIBLE
                    itemView.tvAnswerCount.text = ("Xem thêm ${(totalCount - listData.size)} trả lời khác")
                } else {
                    itemView.tvAnswerCount.visibility = View.GONE
                    itemView.progressBar.visibility = View.GONE
                }
            } else {
                itemView.tvAnswerCount.visibility = View.GONE
                itemView.progressBar.visibility = View.GONE
            }

            val imageQuestionAdapter = HorizontalImageAdapter()
            itemView.rcv_send_image.adapter = imageQuestionAdapter
            imageQuestionAdapter.clearData()
            itemView.rcv_send_image.layoutManager = LinearLayoutManager(listener.mContext, LinearLayoutManager.HORIZONTAL, false)
            if (!obj.attachments.isNullOrEmpty()) {
                val listImage= mutableListOf<String>()
                for(i in obj.attachments){
                    i.thumbnails?.original?.let { listImage.add(it) }
                }
                itemView.rcv_send_image.visibility = View.VISIBLE
                imageQuestionAdapter.setData(listImage)
            } else {
                itemView.rcv_send_image.visibility = View.GONE
            }


            if(obj.actor.verified){
                itemView.imgVerified.visibility=View.VISIBLE
            }else{
                itemView.imgVerified.visibility=View.GONE
            }

            itemView.tvAnswerCount.setOnClickListener {
                if (questionId != -1L) {
                    listener.onClickGetListAnswer(questionPosition, questionId,listData.size)
                    itemView.tvAnswerCount.visibility = View.INVISIBLE
                    itemView.progressBar.visibility = View.VISIBLE
                }
            }
        }

    }
}