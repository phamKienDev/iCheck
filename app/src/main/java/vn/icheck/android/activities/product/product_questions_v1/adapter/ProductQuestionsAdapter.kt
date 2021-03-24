package vn.icheck.android.activities.product.product_questions_v1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_message.view.*
import kotlinx.android.synthetic.main.item_product_questions_v1.view.*
import vn.icheck.android.R
import vn.icheck.android.base.adapter.HorizontalImageAdapter
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.screen.user.campaign.holder.base.LoadingHolder
import vn.icheck.android.activities.product.product_questions_v1.view.IProductQuestionsView
import vn.icheck.android.network.models.v1.ICQuestionRow
import vn.icheck.android.network.models.v1.ICQuestionsAnswers
import vn.icheck.android.util.kotlin.WidgetUtils


class ProductQuestionsAdapter(val listener: IProductQuestionsView) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listData = mutableListOf<ICQuestionRow>()

    private var isLoadmore = false
    private var isLoading = false
    private var error: String? = null

    private val itemType = 1
    private val loadingType = 2
    private val errorType = 3

    fun addAnswer(position: Int, obj: ICQuestionsAnswers) {
        if (!listData[position].answers.isNullOrEmpty()) {
            listData[position].answers.add(0, obj)
        } else {
            listData[position].answers = mutableListOf()
            listData[position].answers.add(obj)
        }

        listData[position].answer_count += 1
        notifyItemChanged(position)
    }

    fun checkLoadmore(list: MutableList<ICQuestionRow>) {
        isLoadmore = list.size >= APIConstants.LIMIT
        isLoading = false
        error = null
    }

    fun setData(list: MutableList<ICQuestionRow>) {
        checkLoadmore(list)

        listData.clear()
        listData.addAll(list)
        notifyDataSetChanged()
    }

    fun addObj(obj: ICQuestionRow) {
        if (listData.isNotEmpty())
            listData.add(0, obj)
        else
            listData.add(obj)
        notifyDataSetChanged()
    }

    fun addData(list: MutableList<ICQuestionRow>) {
        checkLoadmore(list)

        listData.addAll(list)
        notifyDataSetChanged()
    }


    val totalItem: Int
        get() {
            return listData.size
        }


    fun setErrorCode(error: String) {
        listData.clear()
        this.error = error
        isLoadmore = false
        isLoading = false

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ItemQuestionHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_product_questions_v1, parent, false))
            loadingType -> LoadingHolder(parent)
            else -> ErrorHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false))
        }

    }

    override fun getItemCount(): Int {
        return if (!listData.isNullOrEmpty()) {
            if (isLoadmore) {
                listData.size + 1
            } else {
                listData.size
            }
        } else {
            if (!error.isNullOrEmpty()) {
                1
            } else {
                0
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (!listData.isNullOrEmpty()) {
            if (position < listData.size) {
                itemType
            } else {
                loadingType
            }
        } else {
            if (!error.isNullOrEmpty()) {
                errorType
            } else {
                super.getItemViewType(position)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemQuestionHolder -> {
                holder.bind(listData[position])
            }
            is LoadingHolder -> {
                if (!isLoading) {
                    isLoading = true
                }
                listener.onLoadmore()
            }
            is ErrorHolder -> {
                error?.let { holder.bind(it) }

            }
        }
    }

    inner class ItemQuestionHolder(view: View) : BaseViewHolder<ICQuestionRow>(view) {
        var answerAdapter: ProductAnswerAdapter? = null

        override fun bind(obj: ICQuestionRow) {

            when (obj.actor.type) {
                "user" -> {
                    WidgetUtils.loadImageUrlRounded4(itemView.imgItem, obj.actor.avatarThumbnails?.original, R.drawable.ic_user_orange_circle)
                }
                "page" -> {
                    WidgetUtils.loadImageUrlRounded4(itemView.imgItem, obj.actor.avatarThumbnails?.original, R.drawable.ic_business_v2)
                }
                else -> {
                    WidgetUtils.loadImageUrlRounded4(itemView.imgItem, obj.actor.avatarThumbnails?.original, R.drawable.img_shop_default)
                }
            }

            itemView.tvName.text = obj.actor.name
            itemView.tvMessage.text = obj.content

            val date = TimeHelper.simpleDateFormatVn("yyyy-MM-dd'T'HH:mm:ss").parse(obj.created_at)

            itemView.tvTimeLeft.text = TimeHelper.convertDateTimeSvToCurrentTime(
                    TimeHelper.simpleDateFormatSv.format(date)
            )

            itemView.imgItem.setOnClickListener {
                listener.onClickDetailUser(obj.actor.type, obj.actor.id)
            }
            itemView.tvName.setOnClickListener {
                listener.onClickDetailUser(obj.actor.type, obj.actor.id)
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
                imageQuestionAdapter.setData(listImage)
                itemView.rcv_send_image.visibility = View.VISIBLE
            } else {
                itemView.rcv_send_image.visibility = View.GONE
            }

            if (obj.actor.verified) {
                itemView.imgVerified.visibility = View.VISIBLE
            } else {
                itemView.imgVerified.visibility = View.GONE
            }

            itemView.rcvAnswers.layoutManager = LinearLayoutManager(listener.mContext)
            answerAdapter = ProductAnswerAdapter(adapterPosition, listener)
            answerAdapter?.clearData()
            itemView.rcvAnswers.adapter = answerAdapter

            if (!obj.answers.isNullOrEmpty()) {
                answerAdapter?.setData(obj.answers, obj.answer_count)
                answerAdapter?.setQuestionId(obj.id)
            }

            itemView.tvResponse.setOnClickListener {
                listener.onClickCreateAnswer(obj.id, obj.actor.name, adapterPosition)
            }
        }

        fun updateAnswers(list: MutableList<ICQuestionsAnswers>) {
            listData[adapterPosition].answers.addAll(list)
            answerAdapter?.addList(list)
        }
    }

    inner class ErrorHolder(view: View) : BaseViewHolder<String>(view) {
        override fun bind(obj: String) {
            itemView.txtMessage.text = obj
            if (obj == itemView.resources.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)) {
                itemView.imgIcon.background = itemView.resources.getDrawable(R.drawable.ic_error_network)
            } else {
                itemView.imgIcon.background = itemView.resources.getDrawable(R.drawable.ic_error_request)
            }
            itemView.btnTryAgain.setOnClickListener {
                listener.onClickTryAgain()
            }
        }
    }
}