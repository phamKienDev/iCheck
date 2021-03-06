package vn.icheck.android.component.infomation_contribution

import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.holder.BaseViewHolder
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.view.ListAvatar
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.MiddleMultilineTextView
import vn.icheck.android.ichecklibs.ColorManager
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.product.ProductInteractor
import vn.icheck.android.network.models.ICProductContribution
import vn.icheck.android.network.models.feed.ICAvatarOfFriend
import vn.icheck.android.network.models.product.report.ICReportContribute
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.screen.user.contribute_product.IckContributeProductActivity
import vn.icheck.android.screen.dialog.report.ReportDialog
import vn.icheck.android.screen.dialog.report.ReportSuccessDialog
import vn.icheck.android.util.ick.setRankUser
import vn.icheck.android.ichecklibs.util.showShortErrorToast
import vn.icheck.android.util.kotlin.ToastUtils
import vn.icheck.android.util.kotlin.WidgetUtils

class ContributionHolder(parent: ViewGroup) : BaseViewHolder<ContributrionModel>(ViewHelper.createContributionHolder(parent.context)) {
    lateinit var layoutAvatarUser: RelativeLayout
    lateinit var imgAvatarUser: CircleImageView
    lateinit var imgVerified: AppCompatTextView
    lateinit var imgRank: AppCompatImageView
    lateinit var tvNameUser: MiddleMultilineTextView
    lateinit var tvUpVote: AppCompatTextView
    lateinit var tvDownVote: AppCompatTextView
    lateinit var tvAction: AppCompatTextView
    lateinit var listAvatar: ListAvatar
    lateinit var tvListAvatar: AppCompatTextView
    lateinit var tvAll: AppCompatTextView
    lateinit var viewLine: View
    lateinit var layoutListAvatar: LinearLayout

    private val productInteractor = ProductInteractor()
    private var dialog: ReportDialog? = null

    private var clickVote: Boolean? = null

    override fun bind(obj: ContributrionModel) {
        (itemView as ViewGroup).run {
            (getChildAt(1) as LinearLayout).run {
                layoutAvatarUser = getChildAt(0) as RelativeLayout
                imgAvatarUser = layoutAvatarUser.getChildAt(0) as CircleImageView
                imgVerified = layoutAvatarUser.getChildAt(1) as AppCompatTextView
                imgRank = layoutAvatarUser.getChildAt(2) as AppCompatImageView

                tvNameUser = getChildAt(1) as MiddleMultilineTextView
                tvUpVote = getChildAt(2) as AppCompatTextView
                tvDownVote = getChildAt(3) as AppCompatTextView
            }
            tvAction = getChildAt(2) as AppCompatTextView
            viewLine = getChildAt(3) as View
            layoutListAvatar = getChildAt(4) as LinearLayout
            listAvatar = layoutListAvatar.getChildAt(0) as ListAvatar
            tvListAvatar = layoutListAvatar.getChildAt(1) as AppCompatTextView
            tvAll = getChildAt(5) as AppCompatTextView
        }

        imgVerified.visibility = View.GONE
        checkProductVerify(obj)

        if (obj.productVerify) {
            if (obj.manager != null) {
                WidgetUtils.loadImageUrl(imgAvatarUser, obj.manager.avatar, R.drawable.ic_business_v2, R.drawable.ic_business_v2)
                tvNameUser.text = if (obj.manager.name.isNullOrEmpty())
                    itemView.context.getString(R.string.chua_cap_nhat)
                else
                    obj.manager.name
            } else {
                for (i in 0 until itemView.childCount - 1) {
                    itemView.getChildAt(i).visibility = View.GONE
                }
                itemView.layoutParams = ViewHelper.createLayoutParams(0, 0).also {
                    it.topMargin = 0
                }
            }
        } else {
            if (obj.data?.contribution == null) {
                WidgetUtils.loadImageUrl(imgAvatarUser, "", R.drawable.ic_avatar_default_84dp, R.drawable.ic_avatar_default_84dp)
                tvNameUser.text = itemView.context.getString(R.string.chua_cap_nhat)
            } else {
                WidgetUtils.loadImageUrl(imgAvatarUser, obj.data!!.contribution?.user?.avatar, R.drawable.ic_avatar_default_84dp, R.drawable.ic_avatar_default_84dp)
                if (obj.data!!.contribution?.user?.kycStatus == 2) {
                    tvNameUser.setDrawableNextEndText(obj.data!!.contribution?.user?.getName, R.drawable.ic_verified_user_16dp)
                } else {
                    tvNameUser.text = obj.data!!.contribution?.user?.getName
                }
                imgRank.setRankUser(obj.data!!.contribution?.user?.rank?.level)
                checkVote(obj.data!!)
                checkListUserContribute(obj)
                initListener(obj)
            }
        }


    }

    private fun checkProductVerify(contribution: ContributrionModel) {
        if (contribution.productVerify || contribution.data == null) {
            tvUpVote.visibility = View.GONE
            tvDownVote.visibility = View.GONE
            tvAction.visibility = View.GONE
            viewLine.visibility = View.INVISIBLE
            layoutListAvatar.visibility = View.GONE
            tvAll.visibility = View.GONE
        } else {
            tvUpVote.visibility = View.VISIBLE
            tvDownVote.visibility = View.VISIBLE
            tvAction.visibility = View.VISIBLE
            viewLine.visibility = View.VISIBLE
            layoutListAvatar.visibility = View.VISIBLE
            tvAll.visibility = View.VISIBLE
        }
    }

    private fun checkVote(contribution: ICProductContribution) {
        /**
         * isMe:false ->  ????ng g??p c???a user kh??c
         * isMe:true ->  ????ng g??p c???a m??nh
         */
        tvAction.text = if (contribution.isMe) {
            itemView.context.getString(R.string.chinh_sua_dong_gop)
        } else {
            itemView.context.getString(R.string.dong_gop_thong_tin_ngay)
        }

        if (contribution.contribution != null) {
            if (contribution.contribution!!.myVote == null) {
                tvUpVote.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_contribute_correct_unfc_30_px, 0, 0)
                tvUpVote.setTextColor(ColorManager.getSecondTextColor(itemView.context))

                tvDownVote.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_contribute_incorrect_unfc_30px, 0, 0)
                tvDownVote.setTextColor(ColorManager.getSecondTextColor(itemView.context))
            } else {
                if (contribution.contribution!!.myVote!!) {
                    tvUpVote.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_contribute_correct_fc_30px, 0, 0)
                    tvUpVote.setTextColor(ColorManager.getPrimaryColor(itemView.context))


                    tvDownVote.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_contribute_incorrect_unfc_30px, 0, 0)
                    tvDownVote.setTextColor(ColorManager.getSecondTextColor(itemView.context))
                } else {
                    tvUpVote.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_contribute_correct_unfc_30_px, 0, 0)
                    tvUpVote.setTextColor(ColorManager.getSecondTextColor(itemView.context))


                    tvDownVote.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_contribute_incorrect_fc_30px, 0, 0)
                    tvDownVote.setTextColor(ColorManager.getAccentYellowColor(itemView.context))
                }
            }
            contribution.contribution?.let {
                tvUpVote.apply {
                    text = if (it.upVotes > 0) {
                        context.getString(R.string.dung_d, it.upVotes)
                    } else {
                        context.getString(R.string.dung)
                    }
                }
                tvDownVote.apply {
                    text = if (it.downVotes > 0) {
                        context.getString(R.string.sai_d, it.downVotes)
                    } else {
                        context.getString(R.string.sai)
                    }
                }
            }
        }
    }


    private fun initListener(obj: ContributrionModel) {
        tvUpVote.setOnClickListener {
            clickVote = true
            checkVote(obj.data!!)
            if (obj.data!!.contribution!!.myVote == null) {
                sendReportContribute(true, obj)
            } else {
                if (obj.data!!.contribution!!.myVote!!) {
                    sendReportContribute(null, obj)
                } else {
                    DialogHelper.showConfirm(itemView.context, itemView.context.getString(R.string.thay_doi_binh_chon), itemView.context.getString(R.string.content_change_vote_contribution), itemView.context.getString(R.string.khong_toi_khong_muon_giu), itemView.context.getString(R.string.chac_chan), true, object : ConfirmDialogListener {
                        override fun onDisagree() {
                            ICheckApplication.currentActivity()?.let { activity ->
                                DialogHelper.closeLoading(activity)
                            }
                        }

                        override fun onAgree() {
                            sendReportContribute(true, obj)
                        }
                    })
                }
            }
        }

        tvDownVote.setOnClickListener {
            clickVote = false
            checkVote(obj.data!!)
            if (obj.data!!.contribution!!.myVote == null) {
                getListReport(obj)
            } else {
                if (!obj.data!!.contribution!!.myVote!!) {
                    tvDownVote.isEnabled = true
                    sendReportContribute(null, obj)
                } else {
                    getListReport(obj)
                }
            }
        }

        tvAll.setOnClickListener {
            EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.OPEN_LIST_CONTRIBUTION, obj.barcode))
        }

        tvAction.setOnClickListener {
            ICheckApplication.currentActivity()?.let {
                if (SessionManager.isUserLogged) {
                    IckContributeProductActivity.start(it, obj.barcode, obj.productId)
                } else {
                    EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REQUEST_VOTE_CONTRIBUTION))
                }
            }
        }
    }

    fun vote(isVote: Boolean) {
        if (isVote) {
            tvUpVote.performClick()
        } else {
            tvDownVote.performClick()
        }
    }

    private fun checkListUserContribute(data: ContributrionModel) {
        if (!data.data!!.userContributions.isNullOrEmpty()) {
            tvAll.visibility = View.VISIBLE
            layoutListAvatar.visibility = View.VISIBLE
            viewLine.visibility = View.VISIBLE

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                listAvatar.bind(ICAvatarOfFriend(data.data!!.userContributions!!.toMutableList(), data.data!!.count))
            }
            tvListAvatar.apply {
                text = context.getString(
                    R.string.d_nguoi_khac_da_dong_gop_thong_tin,
                    data.data?.count ?: 0
                )
            }
        } else {
            tvAll.visibility = View.GONE
            layoutListAvatar.visibility = View.GONE
            viewLine.visibility = View.INVISIBLE
        }
    }

    private fun getListReport(model: ContributrionModel) {
        ICheckApplication.currentActivity()?.let {
            if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
                tvDownVote.isEnabled = true
                ToastUtils.showLongError(itemView.context, itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
                return
            }

            productInteractor.getListReportFormContribute(object : ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>> {
                override fun onSuccess(obj: ICResponse<ICListResponse<ICReportForm>>) {
                    tvDownVote.isEnabled = true
                    if (!obj.data?.rows.isNullOrEmpty()) {
                        dialog?.dismiss()
                        dialog = ReportDialog(obj.data?.rows!!)

                        dialog?.setListener(object : ReportDialog.DialogClickListener {
                            override fun buttonClick(listReasonId: MutableList<Int>, message: String, listReasonContent: MutableList<String>) {
                                sendReportContribute(false, model, listReasonId, message)
                            }
                        })
                        dialog?.show((it as AppCompatActivity).supportFragmentManager, dialog?.tag)
                    }
                }

                override fun onError(error: ICResponseCode?) {
                    tvDownVote.isEnabled = true
                    ToastUtils.showLongError(itemView.context, itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                }
            })
        }
    }

    private fun sendReportContribute(isVote: Boolean?, model: ContributrionModel, listReport: MutableList<Int>? = null, message: String? = null) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            ToastUtils.showLongError(itemView.context, itemView.context.getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        val contribution = model.data
        if (contribution!!.contribution != null) {
            productInteractor.sendReportContribute(contribution.contribution!!.id!!, isVote, listReport, message, object : ICNewApiListener<ICResponse<ICReportContribute>> {
                override fun onSuccess(obj: ICResponse<ICReportContribute>) {
                    if (dialog != null) {
                        dialog?.dismiss()
                    }

                    if (!obj.data?.reports.isNullOrEmpty()) {
                        if (isVote != null && !isVote) {
                            val dialogFragment = ReportSuccessDialog(itemView.context)

                            if (!message.isNullOrEmpty()) {
                                for (i in obj.data!!.reports.size - 1 downTo 0) {
                                    if (obj.data!!.reports[i].name == "Kh??c") {
                                        obj.data!!.reports.removeAt(i)
                                    }
                                }
                                obj.data?.reports?.add(ICReportForm(obj.data?.reports!!.size + 1, message))
                            }
                            dialogFragment.show(obj.data?.reports!!, Constant.CONTRIBUTION, null, model.barcode)
                        }
                    }


                    if (contribution.contribution!!.myVote == null) {
                        obj.data!!.isVote?.let {
                            if (it) {
                                contribution.contribution!!.upVotes++
                            } else {
                                contribution.contribution!!.downVotes++
                            }
                        }
                    } else {
                        if (contribution.contribution!!.myVote!!) {
                            if (isVote == null) {
                                contribution.contribution!!.upVotes--
                            } else {
                                contribution.contribution!!.upVotes--
                                contribution.contribution!!.downVotes++
                            }
                        } else {
                            if (isVote == null) {
                                contribution.contribution!!.downVotes--
                            } else {
                                contribution.contribution!!.upVotes++
                                contribution.contribution!!.downVotes--
                            }
                        }
                    }

                    contribution.contribution!!.myVote = obj.data?.isVote
                    checkVote(contribution)
                }

                override fun onError(error: ICResponseCode?) {
                    if (dialog != null) {
                        dialog?.dismiss()
                    }
                    if (error?.statusCode == "S402") {
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.REQUEST_VOTE_CONTRIBUTION, isVote))
                    } else {
                        itemView.context.showShortErrorToast(if (error?.message.isNullOrEmpty()) {
                            itemView.context.getString(R.string.co_loi_xay_ra_vui_long_thu_lai)
                        } else {
                            error?.message
                        })
                    }
                }
            })
        }
    }
}