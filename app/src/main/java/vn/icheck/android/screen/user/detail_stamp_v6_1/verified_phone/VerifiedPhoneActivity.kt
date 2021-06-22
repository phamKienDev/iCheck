package vn.icheck.android.screen.user.detail_stamp_v6_1.verified_phone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_verified_phone.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.dialog.notify.notification.NotificationDialog
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ActivityVerifiedPhoneBinding
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICVariantProductStampV6_1
import vn.icheck.android.network.models.detail_stamp_v6_1.ICVerifiedPhone
import vn.icheck.android.screen.account.icklogin.viewmodel.IckLoginViewModel
import vn.icheck.android.screen.location.ICNationBottomDialog
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.update_information_first.UpdateInformationFirstActivity
import vn.icheck.android.screen.user.detail_stamp_v6_1.verified_phone.presenter.VerifiedPhonePresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.verified_phone.view.IVerifiedPhoneView

@AndroidEntryPoint
class VerifiedPhoneActivity : BaseActivityMVVM(), IVerifiedPhoneView {
    private lateinit var binding: ActivityVerifiedPhoneBinding

    private val loginViewModel: IckLoginViewModel by viewModels()
    private val presenter = VerifiedPhonePresenter(this)

    private var mIdDistributor: Long? = null
    private var mProductCode: String? = null
    private var mSerial: String? = null
    private var mIdProduct: Long? = null
    private var mIdVariant: ICVariantProductStampV6_1.ICVariant.ICObjectVariant? = null
    private var mCodeStamp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifiedPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StampDetailActivity.listActivities.add(this)

        setupToolbar()
        setupView()
        setupViewModel()
        setupListener()

        presenter.onGetDataIntent(intent)
    }

    private fun setupToolbar() {
        binding.layoutToolbar.txtTitle.setText(R.string.so_dien_thoai_kich_hoat)

        binding.layoutToolbar.imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupView() {
        binding.tvCountry.background=ViewHelper.lineUnderColorLine1(this)
        binding.tvCode.background=ViewHelper.lineUnderColorLine1(this)
        binding.edtPhone.background=ViewHelper.lineUnderColorLine1(this)
        binding.btnUpdate.background=ViewHelper.btnPrimaryCorners4(this)

        binding.edtPhone.setHintTextColor(vn.icheck.android.ichecklibs.ColorManager.getDisableTextColor(this))
    }

    private fun setupViewModel() {
        loginViewModel.nationLiveData.observe(this, {
            binding.tvCode.text = it.dialCode
        })
    }

    private fun setupListener() {
        binding.tvCountry.setOnClickListener {
            ICNationBottomDialog().show(supportFragmentManager, null)
        }

        binding.btnUpdate.setOnClickListener {
            val phone = if (binding.tvCode.text.toString() == "+84") {
                if (binding.edtPhone.text.toString().startsWith("0")) {
                    binding.edtPhone.text.toString()
                } else {
                    binding.tvCode.text.toString() + binding.edtPhone.text.toString()
                }
            } else {
                binding.tvCode.text.toString() + binding.edtPhone.text.toString()
            }

            presenter.onValidPhoneNumber(phone.replace(" ".toRegex(), "").replace("+", ""))
        }
    }

    override fun onErrorPhone(errorMessage: String) {
        showShortError(errorMessage)
    }

    override fun onErrorIntent() {
        DialogHelper.showNotification(this, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
            override fun onDone() {
                onBackPressed()
            }
        })
    }

    override fun onGetDataIntentSuccess(idDistributor: Long, productCode: String?, serial: String?, productId: Long?, objVariant: ICVariantProductStampV6_1.ICVariant.ICObjectVariant?, codeStamp: String?) {
        mIdDistributor = idDistributor
        mProductCode = productCode
        mSerial = serial
        mIdProduct = productId
        mIdVariant = objVariant
        mCodeStamp = codeStamp
    }

    override fun onVerifiedPhoneSuccess(data: ICVerifiedPhone.Data, phone: String) {
        if (data.is_true == true) {
            hideSoftKeyboard()
            val intent = Intent(this, UpdateInformationFirstActivity::class.java)
            intent.putExtra(Constant.DATA_1, 1)
            intent.putExtra(Constant.DATA_2, mIdDistributor)
            intent.putExtra(Constant.DATA_3, phone)
            intent.putExtra(Constant.DATA_4, mProductCode)
            intent.putExtra(Constant.DATA_5, mSerial)
            intent.putExtra(Constant.DATA_6, mIdProduct)
            intent.putExtra(Constant.DATA_7, mIdVariant)
            intent.putExtra(Constant.DATA_8, mCodeStamp)
            startActivity(intent)
        } else {
            val obj = object : NotificationDialog(this, null, getString(R.string.so_dien_thoai_da_nhap_ko_khop), getString(R.string.da_hieu), false) {
                override fun onDone() {
                    dismiss()
                }
            }
            obj.show()
            obj.hideTitle()
        }
    }

    override fun onVerifiedPhoneFail() {
        showShortError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
    }

    override fun showError(errorMessage: String) {
        showShortError(errorMessage)
    }

    override val mContext: Context
        get() = this

    override fun onShowLoading(isShow: Boolean) {
        if (isShow) {
            DialogHelper.showLoading(this)
        } else {
            DialogHelper.closeLoading(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        StampDetailActivity.listActivities.remove(this)
    }
}
