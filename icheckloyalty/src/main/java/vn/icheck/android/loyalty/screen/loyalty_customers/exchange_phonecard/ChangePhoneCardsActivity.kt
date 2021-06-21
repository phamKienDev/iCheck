package vn.icheck.android.loyalty.screen.loyalty_customers.exchange_phonecard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_change_phone_cards.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.ichecklibs.util.rText
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.dialog.DialogNotification
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame
import vn.icheck.android.loyalty.helper.ValidHelper
import vn.icheck.android.loyalty.model.TopupServices
import vn.icheck.android.loyalty.utils.KeyboardUtils

class ChangePhoneCardsActivity : BaseActivityGame() {

    companion object {
        fun start(activity: Activity, id: Long, typeGift: String, requestCode: Int) {
            val intent = Intent(activity, ChangePhoneCardsActivity::class.java)
            intent.putExtra(ConstantsLoyalty.DATA_1, id)
            intent.putExtra(ConstantsLoyalty.DATA_2, typeGift)
            activity.startActivityForResult(intent, requestCode)
            activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
        }
        fun start(activity: Activity, id: Long, typeGift: String,campaignId:Long, requestCode: Int) {
            val intent = Intent(activity, ChangePhoneCardsActivity::class.java)
            intent.putExtra(ConstantsLoyalty.DATA_1, id)
            intent.putExtra(ConstantsLoyalty.DATA_2, typeGift)
            intent.putExtra(ConstantsLoyalty.DATA_3, campaignId)
            activity.startActivityForResult(intent, requestCode)
            activity.overridePendingTransition(R.anim.right_to_left_enter, R.anim.none)
        }
    }

    private val viewModel by viewModels<ChangePhoneCardsViewModel>()

    private val adapter = ChangePhoneCardsAdapter()

    private var services: TopupServices.Service? = null

    override val getLayoutID: Int
        get() = R.layout.activity_change_phone_cards

    override fun onInitView() {

        initToolbar()
        initRecyclerView()
        initListener()
    }

    @SuppressLint("SetTextI18n")
    private fun initToolbar() {
        viewModel.getDataIntent(intent)

        txtTitle rText R.string.doi_qua_the_cao

        imgBack.setOnClickListener {
            onBackPressed()
        }

        KeyboardUtils.showSoftInput(edtPhone)
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = adapter
    }

    private fun initListener() {
        adapter.setListener(object : ChangePhoneCardsAdapter.ITopUpServiceListener {
            override fun onSelected(obj: TopupServices.Service) {
                services = obj
                when {
                    edtPhone.text.toString().isNotEmpty() && ValidHelper.validPhoneNumber(this@ChangePhoneCardsActivity, edtPhone.text.toString().trim()).isNullOrEmpty() -> {
                        btnAccept.setBackgroundResource(R.drawable.bg_gradient_button_blue)

                        btnAccept.setOnClickListener {
                            KeyboardUtils.hideSoftInput(edtPhone)
                            DialogHelperGame.showLoading(this@ChangePhoneCardsActivity)
                            viewModel.exchangeGift(edtPhone.text.toString().trim(), obj.serviceId)
                        }
                    }
                    edtPhone.text.toString().isEmpty() -> {
                        showLongError(rText(R.string.ban_chua_nhap_so_dien_thoai))
                        btnAccept.setBackgroundResource(R.drawable.bg_b4_corner_20dp)
                    }
                    edtPhone.text.toString().trim().contains("-") -> {
                        showLongError(rText(R.string.so_dien_thoai_khong_duoc_nhap_ky_tu_dac_biet))
                        btnAccept.setBackgroundResource(R.drawable.bg_b4_corner_20dp)
                    }
                    else -> {
                        showLongError(rText(R.string.so_dien_thoai_ban_nhap_khong_hop_le))
                        btnAccept.setBackgroundResource(R.drawable.bg_b4_corner_20dp)
                    }
                }
            }
        })

        viewModel.showErrorDialog.observe(this@ChangePhoneCardsActivity, Observer {
            object : DialogNotification(this@ChangePhoneCardsActivity, null, it, rText(R.string.ok), false) {
                override fun onDone() {
                    onBackPressed()
                }
            }.show()
        })

        viewModel.onTopUpServiceSuccess.observe(this@ChangePhoneCardsActivity, Observer {
            adapter.setData(it)
        })

        viewModel.onExchangeSuccess.observe(this@ChangePhoneCardsActivity, Observer {
            DialogHelperGame.closeLoading()
            setResult(RESULT_OK, Intent().apply {
                putExtra("phone", edtPhone.text.toString().trim())
                putExtra("provider", services?.provider)
                putExtra("price", it.gift?.icoin)
            })
            finish()
        })

        viewModel.showDialogError.observe(this@ChangePhoneCardsActivity, Observer {
            DialogHelperGame.closeLoading()
            ExchangePhonecardFailDialog(it).show(supportFragmentManager, null)
        })
    }
}