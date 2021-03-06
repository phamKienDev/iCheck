package vn.icheck.android.screen.user.listcontribute

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_contribute.view.*
import kotlinx.android.synthetic.main.item_contribute.view.imgAvatar
import kotlinx.android.synthetic.main.item_contribute.view.tvName
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.IRecyclerViewCallback
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.TextHelper
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.models.ICContribute
import vn.icheck.android.network.models.product.report.ICReportContribute
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.base.holder.LoadingHolder
import vn.icheck.android.constant.Constant
import vn.icheck.android.screen.user.contribute_product.IckContributeProductActivity
import vn.icheck.android.screen.user.page_details.fragment.page.widget.message.MessageHolder
import vn.icheck.android.screen.dialog.report.ReportDialog
import vn.icheck.android.screen.dialog.report.ReportSuccessDialog
import vn.icheck.android.screen.user.wall.IckUserWallActivity
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.util.ick.setRankUser
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class ListContributeAdapter(val listener: IRecyclerViewCallback, val fragmentManager: FragmentActivity, val callback: IListContributeListener, val setOnClickListener: (Long) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listData = mutableListOf<ICContribute>()
    private val itemType = 1
    private val itemLoadMore = 2
    private val itemMessage = 3

    private var mMessageError: String? = null
    private var iconError = R.drawable.ic_error_request
    private var isLoading = false
    private var isLoadMore = false
    var didContribute = false

    private fun checkLoadMore(listCount: Int) {
        isLoadMore = listCount >= APIConstants.LIMIT
        isLoading = false
        mMessageError = ""
    }

    fun setError(icon: Int, error: String) {
        listData.clear()

        isLoadMore = false
        isLoading = false
        iconError = icon
        mMessageError = error
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return listData.isEmpty()
    }

    fun disableLoadMore() {
        isLoading = true
        notifyItemChanged(0, itemCount)
    }

    fun setData(obj: MutableList<ICContribute>) {
        checkLoadMore(obj.size)
        val filt = obj.firstOrNull {
            it.user?.id == SessionManager.session.user?.id
        }
        didContribute = filt != null
        listData.clear()
        listData.addAll(obj)
        notifyDataSetChanged()
    }

    fun addData(obj: MutableList<ICContribute>) {
        checkLoadMore(obj.size)
        val filt = obj.firstOrNull {
            it.user?.id == SessionManager.session.user?.id
        }
        didContribute = filt != null
        listData.addAll(obj)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            itemType -> ViewHolder(parent, fragmentManager)
            itemLoadMore -> LoadingHolder(parent)
            else -> MessageHolder(parent)
        }
    }

    override fun getItemCount(): Int {
        return if (listData.isEmpty()) {
            if (mMessageError != null) {
                1
            } else {
                0
            }
        } else {
            if (isLoadMore) {
                listData.size + 1
            } else {
                listData.size
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData.isNotEmpty()) {
            if (position < listData.size) {
                itemType
            } else {
                itemLoadMore
            }
        } else {
            if (mMessageError != null) {
                itemMessage
            } else {
                return super.getItemViewType(position)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(listData[position])
            }
            is LoadingHolder -> {
                if (!isLoading) {
                    isLoading = true
                    listener.onLoadMore()
                }
            }
            is MessageHolder -> {
                if (mMessageError.isNullOrEmpty()) {
                    holder.bind(iconError, "")
                } else {
                    holder.bind(iconError, mMessageError!!)
                }

                holder.listener(View.OnClickListener {
                    listener.onMessageClicked()
                })
            }
        }
    }

    inner class ViewHolder(parent: ViewGroup, val fragmentManager: FragmentActivity) : BaseViewHolder<ICContribute>(LayoutInflater.from(parent.context).inflate(R.layout.item_contribute, parent, false)) {
        val interactor = ProductInteractor()
        private var dialog: ReportDialog? = null

        @SuppressLint("SetTextI18n")
        override fun bind(obj: ICContribute) {
            unCheckAll()

            itemView.tvName.setTextColor(ColorManager.getNormalTextColor(itemView.context))

            itemView.imgAvatar.run {
                WidgetUtils.loadImageUrlRounded4(this, obj.user?.avatar, R.drawable.ic_avatar_default_84dp)

                setOnClickListener {
                    IckUserWallActivity.create(obj.user?.id, itemView.context)
                }
            }

            itemView.imgRank.setRankUser(obj.user?.rank?.level)

            itemView.tvName.run {
                if (obj.user?.kycStatus == 2) {
                    setDrawableNextEndText(obj.user?.getName, R.drawable.ic_verified_user_16dp)
                } else {
                    text = obj.user?.getName
                }

                setOnClickListener {
                    IckUserWallActivity.create(obj.user?.id, itemView.context)
                }
            }

            itemView.tvCountImage.run {
                if (!obj.data?.media.isNullOrEmpty()) {
                    WidgetUtils.loadImageUrlRounded4(itemView.imgProduct, obj.data?.media?.get(0)?.content)

                    if (obj.data?.media!!.size < 2) {
                        visibility = View.INVISIBLE
                    } else {
                        visibility = View.VISIBLE
                        text = "+${(obj.data?.media!!.size - 1)}"
                    }
                }
            }

            itemView.setOnClickListener {
                setOnClickListener(obj.productId)
            }

            itemView.tvNameProduct.run {
                if (!obj.data?.name.isNullOrEmpty()) {
                    text = obj.data?.name
                }
                setOnClickListener {
                    setOnClickListener(obj.productId)
                }
            }


            itemView.tvPrice.run {
                if (obj.data?.price != null && obj.data?.price != 0L) {
                    setTextColor(ColorManager.getPrimaryColor(context))
                    typeface = Typeface.createFromAsset(context.assets, "font/barlow_medium.ttf")
                    text = itemView.context.getString(R.string.s_d, TextHelper.formatMoney(obj.data?.price))
                } else {
                    setTextColor(ColorManager.getDisableTextColor(context))
                    setTypeface(null, Typeface.ITALIC)
                    text = context.getString(R.string.gia_dang_cap_nhat)
                }
            }

            if (!obj.data?.barcode.isNullOrEmpty()) {
                itemView.tvBarcode.text = obj.data?.barcode
            }

            if (obj.myVote != null) {
                if (obj.myVote == true) {
                    itemView.tvYes.isChecked = true
                } else {
                    itemView.tvNo.isChecked = true
                }
            } else {
                unCheckAll()
            }

            itemView.tvYes.run {
                visibility = View.VISIBLE
                setTextColor(ViewHelper.textColorDisableTextUncheckPrimaryChecked(context))

                text = if (obj.upVotes > 0) {
                    context.getString(R.string.dung_d, obj.upVotes)
                } else {
                    context.getString(R.string.dung)
                }


                setOnClickListener {
                    if (obj.myVote == true) {
                        postVote(obj, null, this)
                    } else {
                        if (obj.myVote == false) {
                            DialogHelper.showConfirm(itemView.context, itemView.context.getString(R.string.thay_doi_binh_chon), itemView.context.getString(R.string.content_change_vote_contribution), itemView.context.getString(R.string.khong_toi_khong_muon_giu), itemView.context.getString(R.string.chac_chan), true, object : ConfirmDialogListener {
                                override fun onDisagree() {
                                }

                                override fun onAgree() {
                                    postVote(obj, true, tvYes)
                                }
                            })
                        } else {
                            postVote(obj, true, tvYes)
                        }
                    }
                }
            }

            itemView.tvNo.run {
                visibility = View.VISIBLE
                setTextColor(ViewHelper.textColorDisableTextUncheckAccentYellowChecked(context))

                text = if (obj.downVotes > 0) {
                    context.getString(R.string.sai_d, obj.downVotes)
                } else {
                    context.getString(R.string.sai)
                }


                setOnClickListener {
                    if (obj.myVote == false) {
                        itemView.tvNo.isChecked = false
                        postVote(obj, null, tvNo)
                    } else {
                        itemView.tvNo.isClickable = false
                        getListReport(obj, this)
                    }
                }
            }

            itemView.btnAction.apply {
                background = ViewHelper.bgOutlinePrimary1Corners4(context)
                visibility = if (!obj.isMe) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                setOnClickListener {
                    ICheckApplication.currentActivity()?.let {
                        obj.data?.barcode?.let { barcode -> IckContributeProductActivity.start(it, barcode, obj.productId, "Ch???nh s???a ????ng g??p") }
                    }
                }
            }
        }

        private fun isChecked(view: AppCompatCheckedTextView): Boolean {
            return if (view.isChecked) {
                true
            } else {
                unCheckAll()
                view.isChecked = true
                false
            }
        }

        private fun unCheckAll() {
            itemView.tvYes.isChecked = false
            itemView.tvNo.isChecked = false
        }

        @SuppressLint("SetTextI18n")
        private fun postVote(data: ICContribute, isVote: Boolean?, view: AppCompatCheckedTextView) {
            ICheckApplication.currentActivity()?.let { activity ->
                if (NetworkHelper.isNotConnected(itemView.context)) {
                    ToastUtils.showLongError(itemView.context, itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                    return
                }

                DialogHelper.showLoading(activity)

                interactor.postVoteContributor(data.id, isVote, object : ICNewApiListener<ICResponse<ICContribute>> {
                    override fun onSuccess(obj: ICResponse<ICContribute>) {
                        DialogHelper.closeLoading(activity)
                        callback.onClickVote(isVote)

                        if (isVote != null) {
                            isChecked(view)

                            if (data.myVote != null) {
                                if (isVote) {
                                    if (data.myVote == false) {
                                        data.myVote = true

                                        data.downVotes = data.downVotes - 1
                                        itemView.tvNo.text = if (data.downVotes > 0) {
                                            itemView.context.getString(R.string.sai_d, data.downVotes)
                                        } else {
                                            itemView.context.getString(R.string.sai)
                                        }

                                        data.upVotes = data.upVotes + 1
                                        itemView.tvYes.setText(R.string.dung_d, data.upVotes)
                                    } else {
                                        data.myVote = null

                                        data.upVotes = data.upVotes - 1
                                        itemView.tvYes.apply {
                                            text = if (data.upVotes > 0) {
                                                context.getString(R.string.dung_d, data.upVotes)
                                            } else {
                                                context.getString(R.string.dung)
                                            }
                                        }
                                    }
                                } else {
                                    if (data.myVote == false) {
                                        data.myVote = null

                                        data.downVotes = data.downVotes - 1
                                        itemView.tvNo.apply {
                                            text = if (data.downVotes > 0) {
                                                context.getString(R.string.sai_d, data.downVotes)
                                            } else {
                                                context.getString(R.string.sai)
                                            }
                                        }

                                    } else {
                                        data.myVote = null

                                        data.upVotes = data.upVotes - 1
                                        itemView.tvYes.apply {
                                            text = if (data.upVotes > 0) {
                                                context.getString(R.string.dung_d, data.upVotes)
                                            } else {
                                                context.getString(R.string.dung)
                                            }
                                        }

                                        data.downVotes = data.downVotes + 1
                                        itemView.tvNo.setText(R.string.sai_d, data.downVotes)
                                    }
                                }
                            } else {
                                data.myVote = isVote

                                if (isVote) {
                                    data.upVotes = data.upVotes + 1
                                    itemView.tvYes.setText(R.string.dung_d, data.upVotes)
                                } else {
                                    data.downVotes = data.downVotes + 1
                                    itemView.tvNo.setText(R.string.sai_d, data.downVotes)
                                }
                            }
                        } else {
                            if (data.myVote != null) {

                                if (data.myVote == false) {
                                    data.downVotes = data.downVotes - 1
                                    itemView.tvNo.apply {
                                        text = if (data.downVotes > 0) {
                                            context.getString(R.string.sai_d, data.downVotes)
                                        } else {
                                            context.getString(R.string.sai)
                                        }
                                    }

                                } else {
                                    data.upVotes = data.upVotes - 1
                                    itemView.tvYes.apply {
                                        text = if (data.upVotes > 0) {
                                            context.getString(R.string.dung_d, data.upVotes)
                                        } else {
                                            context.getString(R.string.dung)
                                        }
                                    }
                                }
                                data.myVote = null
                                unCheckAll()
                            } else {
                                unCheckAll()
                            }
                        }
                    }

                    override fun onError(error: ICResponseCode?) {
                        DialogHelper.closeLoading(activity)
                        if (error?.statusCode == "S402") {
                            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REQUEST_VOTE_CONTRIBUTION, isVote))
                        } else {
                            ToastUtils.showLongError(itemView.context, if (!error?.message.isNullOrEmpty()) {
                                error?.message
                            } else {
                                itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                            })

                        }
                    }
                })
            }
        }

        private fun getListReport(data: ICContribute, view: AppCompatCheckedTextView) {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                ToastUtils.showLongError(itemView.context, itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                return
            }

            interactor.getListReportFormContribute(object : ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICReportForm>>) {
                    dialog?.dismiss()
                    itemView.tvNo.isClickable = true

                    if (!obj.data?.rows.isNullOrEmpty()) {
                        dialog = ReportDialog(obj.data?.rows!!)

                        dialog?.setListener(object : ReportDialog.DialogClickListener {
                            override fun buttonClick(listReasonId: MutableList<Int>, message: String, listReasonContent: MutableList<String>) {
                                sendReportContribute(listReasonId, message, data, listReasonContent, view)
                            }
                        })
                        dialog?.show(fragmentManager.supportFragmentManager, dialog?.tag)
                    } else {
                        ToastUtils.showLongError(itemView.context, itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    itemView.tvNo.isClickable = true
                    ToastUtils.showLongError(itemView.context, itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            })
        }

        private fun sendReportContribute(listReason: MutableList<Int>, message: String, data: ICContribute, listMessage: MutableList<String>, view: AppCompatCheckedTextView) {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                ToastUtils.showLongError(itemView.context, itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                return
            }

            interactor.sendReportContributor(data.id, listReason, message, object : ICNewApiListener<ICResponse<ICReportContribute>> {
                override fun onSuccess(obj: ICResponse<ICReportContribute>) {
                    if (!obj.data?.reports.isNullOrEmpty()) {
                        if (message.isNotEmpty()) {
                            for (i in obj.data!!.reports.size - 1 downTo 0) {
                                if (obj.data!!.reports[i].name == "Kh??c") {
                                    obj.data!!.reports.removeAt(i)
                                }
                            }
                            obj.data?.reports!!.add(ICReportForm(obj.data?.reports!!.size + 1, message))
                        }

                        dialog?.dismiss()

                        val listData = mutableListOf<ICReportForm>()
                        if (message.isNotEmpty()) {
                            listMessage.add(message)
                        }
                        if (!listMessage.isNullOrEmpty()) {
                            for (i in 0 until listMessage.size) {
                                listData.add(ICReportForm(null, listMessage[i]))
                            }
                        }

                        val dialogFragment = ReportSuccessDialog(itemView.context, true, data.id, isContributed = didContribute)

                        dialogFragment.show(listData, Constant.CONTRIBUTION, null, data.data?.barcode)

                        postVote(data, false, view)
                    } else {
                        ToastUtils.showLongError(itemView.context, itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    ToastUtils.showLongError(itemView.context, itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            })
        }
    }
}