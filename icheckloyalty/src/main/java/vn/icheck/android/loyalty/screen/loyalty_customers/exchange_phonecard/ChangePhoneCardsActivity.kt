package vn.icheck.android.loyalty.screen.loyalty_customers.exchange_phonecard

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_change_phone_cards.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.ConstantsLoyalty
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.dialog.DialogNotification
import vn.icheck.android.loyalty.dialog.base.DialogHelperGame
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

        txtTitle.text = "Đổi quà thẻ cào"

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
                    edtPhone.text.toString().isNotEmpty() && edtPhone.text.toString().trim().length == 10 -> {
                        btnAccept.setBackgroundResource(R.drawable.bg_gradient_button_blue)

                        btnAccept.setOnClickListener {
                            KeyboardUtils.hideSoftInput(edtPhone)
                            DialogHelperGame.showLoading(this@ChangePhoneCardsActivity)
                            viewModel.exchangeGift(edtPhone.text.toString().trim(), obj.serviceId)
                        }
                    }
                    edtPhone.text.toString().isEmpty() -> {
                        showLongError("Bạn chưa nhập số điện thoại!")
                        btnAccept.setBackgroundResource(R.drawable.bg_b4_corner_20dp)
                    }
                    edtPhone.text.toString().trim().contains("-") -> {
                        showLongError("Số điện thoại không được nhập ký tự đặc biệt!")
                        btnAccept.setBackgroundResource(R.drawable.bg_b4_corner_20dp)
                    }
                    else -> {
                        showLongError("Số điện thoại bạn nhập không hợp lệ!")
                        btnAccept.setBackgroundResource(R.drawable.bg_b4_corner_20dp)
                    }
                }
            }
        })

        viewModel.showErrorDialog.observe(this@ChangePhoneCardsActivity, Observer {
            object : DialogNotification(this@ChangePhoneCardsActivity, null, it, "Ok", false) {
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
            ExchangePhonecardFailDialog().show(supportFragmentManager, null)
        })
    }
}