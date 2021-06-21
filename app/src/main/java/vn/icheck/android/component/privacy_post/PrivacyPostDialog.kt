package vn.icheck.android.component.privacy_post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_message.*
import kotlinx.android.synthetic.main.privacy_post_dialog.*
import kotlinx.android.synthetic.main.toolbar_light_blue.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.base.model.ICError
import vn.icheck.android.databinding.PrivacyPostDialogBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.network.base.ICListResponse
import vn.icheck.android.network.base.ICNewApiListener
import vn.icheck.android.network.base.ICResponse
import vn.icheck.android.network.base.ICResponseCode
import vn.icheck.android.network.feature.product_review.ProductReviewInteractor
import vn.icheck.android.network.models.ICPrivacy
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.ToastUtils

class PrivacyPostDialog(val postId: Long?) : BaseBottomSheetDialogFragment() {
    private lateinit var adapter: PrivacyPostAdapter
    private lateinit var binding : PrivacyPostDialogBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = PrivacyPostDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        initRecylerView()
        binding.layoutToolbar.imgBack.setImageResource(R.drawable.ic_cancel_light_blue_24dp)
        binding.layoutToolbar.txtTitle.setText(R.string.chinh_sua_quyen_rieng_tu)

        binding.layoutToolbar.imgBack.setOnClickListener {
            dismiss()
        }

        binding.tvFinish.apply {
            background = vn.icheck.android.ichecklibs.ViewHelper.bgPrimaryCorners4(context)
            setOnClickListener {
                for (item in adapter.getListData) {
                    if (item.selected) {
                        editPrivacy(item)
                    }
                }
            }
        }
    }

    private fun initRecylerView() {
        adapter = PrivacyPostAdapter()
        recyclerView.adapter = adapter
    }

    fun getData() {
        if (postId != null) {
            getPrivacy()
        } else {
            setError(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
        }
    }

    private fun setError(error: ICError) {
        containerData.beGone()
        layoutMessage.beVisible()
        imgIcon.setImageResource(error.icon)
        txtMessage.text = error.message
    }


    private fun getPrivacy() {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            setError(ICError(R.drawable.ic_error_network, getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            return
        }

        DialogHelper.showLoading(this)

        ProductReviewInteractor().getPrivacy(postId!!, object : ICNewApiListener<ICResponse<ICListResponse<ICPrivacy>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICPrivacy>>) {
                DialogHelper.closeLoading(this@PrivacyPostDialog)

                if (obj.data?.rows.isNullOrEmpty()) {
                    layoutMessage.beGone()
                    setError(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
                } else {
                    containerData.beVisible()
                    adapter.setListData(obj.data!!.rows)
                }
            }

            override fun onError(error: ICResponseCode?) {
                DialogHelper.closeLoading(this@PrivacyPostDialog)
                setError(ICError(R.drawable.ic_error_request, getString(R.string.co_loi_xay_ra_vui_long_thu_lai), null, null))
            }
        })
    }

    private fun editPrivacy(privacy: ICPrivacy) {
        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            ToastUtils.showShortError(context, R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            return
        }

        DialogHelper.showLoading(this)
        ProductReviewInteractor().postPrivacy(postId!!, privacy.privacyElementId, object : ICNewApiListener<ICResponse<Boolean>> {
            override fun onSuccess(obj: ICResponse<Boolean>) {
                DialogHelper.closeLoading(this@PrivacyPostDialog)
                obj.data?.let {
                    context?.let { it1 -> DialogHelper.showDialogSuccessBlack(it1, it1.getString(R.string.ban_da_thay_doi_quyen_rieng_tu))}
                    dismiss()
                }
            }

            override fun onError(error: ICResponseCode?) {
                DialogHelper.closeLoading(this@PrivacyPostDialog)
                ToastUtils.showShortError(context, R.string.co_loi_xay_ra_vui_long_thu_lai)
            }
        })
    }

}