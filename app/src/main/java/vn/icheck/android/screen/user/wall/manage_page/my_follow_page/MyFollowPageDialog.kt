package vn.icheck.android.screen.user.wall.manage_page.my_follow_page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.dialog_my_follow_page.*
import org.greenrobot.eventbus.EventBus
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.models.product.report.ICReportForm
import vn.icheck.android.room.dao.PageFollowDao
import vn.icheck.android.room.database.AppDatabase
import vn.icheck.android.screen.user.product_detail.product.wrongcontribution.ReportWrongContributionDialog
import vn.icheck.android.screen.user.product_detail.product.wrongcontribution.ReportWrongContributionSuccessDialog
import vn.icheck.android.util.kotlin.ToastUtils

class MyFollowPageDialog(val pageId: Long) : BaseBottomSheetDialogFragment() {
    val interactor = PageRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_my_follow_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pageDao = AppDatabase.getDatabase(requireContext()).pageFollowsDao()

        layoutFollow.setOnClickListener {
            unFollowPage(pageDao)
        }

        layoutReport.setOnClickListener {
            reportPage()
        }
    }

    private fun reportPage() {
        if (NetworkHelper.isNotConnected(requireContext())) {
            ToastUtils.showLongError(requireContext(), ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        DialogHelper.showLoading(this)
        //get list report
        interactor.getListReportFormPage(object : ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICReportForm>>) {
                DialogHelper.closeLoading(this@MyFollowPageDialog)
                if (!obj.data?.rows.isNullOrEmpty()) {
                    dialog?.dismiss()
                    ICheckApplication.currentActivity()?.let { activity ->
                        val reportDialog = ReportWrongContributionDialog(obj.data?.rows!!, R.string.bao_cao_trang)
                        reportDialog.setListener(object : ReportWrongContributionDialog.DialogClickListener {
                            override fun buttonClick(position: Int, listReason: MutableList<Int>, message: String, listMessage: MutableList<String>) {
                                DialogHelper.showLoading(this@MyFollowPageDialog)
                                //sent report
                                interactor.sendReportPage(pageId, listReason, message, object : ICNewApiListener<ICResponse<ICListResponse<ICReportForm>>> {
                                    override fun onSuccess(obj: ICResponse<ICListResponse<ICReportForm>>) {
                                        DialogHelper.closeLoading(this@MyFollowPageDialog)
                                        val listData = mutableListOf<ICReportForm>()
                                        if (message.isNotEmpty()) {
                                            listMessage.add(message)
                                        }
                                        if (!listMessage.isNullOrEmpty()) {
                                            for (i in 0 until listMessage.size) {
                                                listData.add(ICReportForm(null, listMessage[i]))
                                            }
                                        }
                                        reportDialog.dismiss()
                                        ReportWrongContributionSuccessDialog(activity).show(listData, "", ICheckApplication.getInstance().getString(R.string.report_wrong_contribution_success_page_title))
                                    }

                                    override fun onError(error: ICResponseCode?) {
                                        DialogHelper.closeLoading(this@MyFollowPageDialog)
                                        ToastUtils.showLongError(activity, error?.message
                                                ?: ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                                    }
                                })
                            }

                        })
                        reportDialog.show((ICheckApplication.currentActivity() as AppCompatActivity).supportFragmentManager, null)
                    }
                }
            }

            override fun onError(error: ICResponseCode?) {
                DialogHelper.closeLoading(this@MyFollowPageDialog)
                ToastUtils.showLongError(requireContext(), error?.message
                        ?: ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
            }
        })
    }

    private fun unFollowPage(pageDao: PageFollowDao) {
        if (NetworkHelper.isNotConnected(requireContext())) {
            ToastUtils.showLongError(requireContext(), ICheckApplication.getInstance().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai))
            return
        }

        DialogHelper.showConfirm(requireContext(), requireContext().getString(R.string.ban_chac_chan_bo_theo_doi_trang_nay), null,
                requireContext().getString(R.string.de_sau), requireContext().getString(R.string.dong_y), true, object : ConfirmDialogListener {
            override fun onDisagree() {
                EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.DISMISS_DIALOG))
            }

            override fun onAgree() {
                interactor.unFollowPage(pageId, object : ICNewApiListener<ICResponse<Boolean>> {
                    override fun onSuccess(obj: ICResponse<Boolean>) {
                        dismiss()
                        if (pageDao.getPageFollowByID(pageId) != null)
                            pageDao.deletePageFollowById(pageId)

                        DialogHelper.showDialogSuccessBlack(requireContext(), requireContext().getString(R.string.ban_da_huy_theo_doi_trang_nay))
                        EventBus.getDefault().post(ICMessageEvent(ICMessageEvent.Type.UNFOLLOW_PAGE, pageId))
                    }

                    override fun onError(error: ICResponseCode?) {
                        ToastUtils.showLongError(requireContext(), error?.message
                                ?: ICheckApplication.getInstance().getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }
                })
            }
        })
    }
}