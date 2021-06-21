package vn.icheck.android.screen.user.recharge_phone

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_recharge_phone.*
import kotlinx.android.synthetic.main.activity_recharge_phone.btnAgainError
import kotlinx.android.synthetic.main.activity_recharge_phone.btnPayment
import kotlinx.android.synthetic.main.activity_recharge_phone.constraintLayout10
import kotlinx.android.synthetic.main.activity_recharge_phone.imgActionHistoryTopup
import kotlinx.android.synthetic.main.activity_recharge_phone.imgBack
import kotlinx.android.synthetic.main.activity_recharge_phone.imgError
import kotlinx.android.synthetic.main.activity_recharge_phone.layoutError
import kotlinx.android.synthetic.main.activity_recharge_phone.nestedScrollView
import kotlinx.android.synthetic.main.activity_recharge_phone.rcvCardValue
import kotlinx.android.synthetic.main.activity_recharge_phone.rcvNetwork
import kotlinx.android.synthetic.main.activity_recharge_phone.tvMessageError
import kotlinx.android.synthetic.main.activity_recharge_phone.tvPrice
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.ConfirmDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.callback.OnItemClickListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.*
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.ichecklibs.ViewHelper.fillDrawableEndText
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.recharge_phone.ICMenhGia
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.history_loading_card.home.HistoryCardActivity
import vn.icheck.android.screen.user.payment_topup.PaymentTopupActivity
import vn.icheck.android.screen.user.recharge_phone.adapter.MenhGiaTheAdapter
import vn.icheck.android.screen.user.recharge_phone.adapter.NetworkAdapter
import vn.icheck.android.screen.user.recharge_phone.viewmodel.RechargePhoneVIewModel
import vn.icheck.android.tracking.TrackingAllHelper
import vn.icheck.android.ichecklibs.util.setText

class RechargePhoneActivity : BaseActivityMVVM() {

    companion object {
        val listActivities = mutableListOf<AppCompatActivity>()
    }

    private lateinit var viewModel: RechargePhoneVIewModel

    private var listBuyTopup = mutableListOf<ICRechargePhone>()
    private var listMenhGia = ArrayList<ICMenhGia>()
    private var value: String = ""
    private var idValue: Long = -1L
    private var card: ICRechargePhone? = null

    private lateinit var nhaMangAdapter: NetworkAdapter
    private val menhGiaAdapter = MenhGiaTheAdapter(listMenhGia)

    private val permissionContact = 1
    private val requestContact = 2

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recharge_phone)

        viewModel = ViewModelProvider(this).get(RechargePhoneVIewModel::class.java)
        initView()
        listenerGetData()
        initListNetworkOperator()
        initListMenhGia()
        displayData()
        listener()
        TrackingAllHelper.trackPhoneTopupStart()
    }

    private fun initView() {
        btnPayment.background = ViewHelper.bgPaymentState(this)
        tvPrice.setHintTextColor(vn.icheck.android.ichecklibs.Constant.getPrimaryColor(this))
        edtPhone.fillDrawableEndText(R.drawable.ic_phonebook_24dp)

        if (SessionManager.isUserLogged) {
            tv1.visibility = View.VISIBLE
        } else {
            tv1.visibility = View.GONE
        }
    }

    private fun listenerGetData() {
        viewModel.getData(1)

        viewModel.data.observe(this, Observer { obj ->
            if (obj.isNullOrEmpty()) {
                nestedScrollView.visibility = View.GONE
                constraintLayout10.visibility = View.GONE
                layoutError.visibility = View.VISIBLE
            } else {
                layoutError.visibility = View.GONE
                nestedScrollView.visibility = View.VISIBLE
                constraintLayout10.visibility = View.VISIBLE
                listBuyTopup.clear()
                listBuyTopup.addAll(obj)
                nhaMangAdapter.notifyDataSetChanged()
                selectNetWork(0)
            }
        })

        viewModel.errorType.observe(this, Observer { typeMessage ->
            layoutError.visibility = View.VISIBLE
            nestedScrollView.visibility = View.GONE
            constraintLayout10.visibility = View.GONE
            when (typeMessage) {
                Constant.ERROR_UNKNOW -> {
                    imgError.setImageResource(R.drawable.ic_error_request)
                    tvMessageError.setText(R.string.khong_the_truy_cap_vui_long_thu_lai_sau)
                }
                Constant.ERROR_EMPTY -> {
                    imgError.setImageResource(R.drawable.ic_error_request)
                    tvMessageError.setText(R.string.khong_the_truy_cap_vui_long_thu_lai_sau)
                }
                Constant.ERROR_INTERNET -> {
                    imgError.setImageResource(R.drawable.ic_error_network)
                    tvMessageError.setText(R.string.ket_noi_mang_cua_ban_co_van_de_vui_long_thu_lai)
                }
            }
        })

        viewModel.statusCode.observe(this, Observer {
            when (it) {
                ICMessageEvent.Type.ON_NO_INTERNET -> {
                    DialogHelper.showConfirm(this, R.string.khong_co_ket_noi_mang_vui_long_thu_lai_sau, R.string.huy_bo, R.string.thu_lai, object : ConfirmDialogListener {
                        override fun onDisagree() {
                            onBackPressed()
                        }

                        override fun onAgree() {
                            viewModel.getData(1)
                        }
                    })
                }
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
                else -> {
                }
            }
        })

        viewModel.errorData.observe(this, Observer {
            showShortError(it)
        })
    }

    private fun initListNetworkOperator() {
        nhaMangAdapter = NetworkAdapter(rcvNetwork, listBuyTopup)

        rcvNetwork.layoutManager = object : GridLayoutManager(this, 3, VERTICAL, false) {
            override fun canScrollVertically(): Boolean = false
        }
        rcvNetwork.adapter = nhaMangAdapter

        nhaMangAdapter.setOnItemClickListener(object : OnItemClickListener {
            @SuppressLint("SetTextI18n")
            override fun onItemClick(itemView: View, position: Int) {
                selectNetWork(position)
                for (temp in listBuyTopup) {
                    temp.select = false
                }
                listBuyTopup[position].select = true
                nhaMangAdapter.notifyDataSetChanged()
                tvPrice.setText(R.string.s_d, TextHelper.formatMoneyPhay(listMenhGia[0].menhGia.toLong()))
            }
        })
    }

    private fun initListMenhGia() {
        rcvCardValue.layoutManager = object : GridLayoutManager(this, 3, VERTICAL, false) {
            override fun canScrollVertically(): Boolean = false
        }
        rcvCardValue.adapter = menhGiaAdapter

        menhGiaAdapter.setOnItemClickListener(object : OnItemClickListener {
            @SuppressLint("SetTextI18n")
            override fun onItemClick(itemView: View, position: Int) {
                for (temp in listMenhGia) {
                    temp.select = false
                }
                value = listMenhGia[position].menhGia
                listMenhGia[position].select = true
                menhGiaAdapter.notifyDataSetChanged()
                tvPrice.setText(R.string.s_d, TextHelper.formatMoneyPhay(listMenhGia[position].menhGia.toLong()))
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun listener() {
        imgActionHistoryTopup.setOnClickListener {
            startActivity<HistoryCardActivity, Int>(Constant.DATA_1, 1)
        }

        edtPhone.setOnTouchListener { _, event ->
            val drawableEnd = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (edtPhone.right - edtPhone.compoundDrawables[drawableEnd].bounds.width())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), permissionContact)
                    } else {
                        if (PermissionHelper.checkPermission(this, Manifest.permission.READ_CONTACTS, permissionContact)) {
                            val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                            startActivityForResult(intent, requestContact)
                        }
                    }
                    return@setOnTouchListener true
                }
            }
            false
        }

        imgBack.setOnClickListener {
            onBackPressed()
        }

        btnPayment.setOnClickListener {
            if (card != null) {
                if (value.isNotEmpty()) {
                    if (SessionManager.isUserLogged) {
                        if (edtPhone.text.toString().isNullOrEmpty()) {
                            val intent = Intent(this, PaymentTopupActivity::class.java)
                            intent.putExtra("type", 1)
                            intent.putExtra("value", value)
                            intent.putExtra("idValue", idValue)
                            card?.let { card ->
                                intent.putExtra("card", JsonHelper.toJson(card))
                            }
                            intent.putExtra("phone", edtPhone.text.toString())
                            startActivity(intent)
                        } else {
                            if (ValidHelper.checkValidatePhoneNumberBuyCard(this, edtPhone.text.toString())) {
                                val intent = Intent(this, PaymentTopupActivity::class.java)
                                intent.putExtra("type", 1)
                                intent.putExtra("value", value)
                                intent.putExtra("idValue", idValue)
                                card?.let { card ->
                                    intent.putExtra("card", JsonHelper.toJson(card))
                                }
                                intent.putExtra("phone", edtPhone.text.toString())
                                startActivity(intent)
                            }
                        }
                    } else {
                        if (ValidHelper.checkValidatePhoneNumberBuyCard(this, edtPhone.text.toString())) {
                            val intent = Intent(this, PaymentTopupActivity::class.java)
                            intent.putExtra("type", 1)
                            intent.putExtra("value", value)
                            intent.putExtra("idValue", idValue)
                            card?.let { card ->
                                intent.putExtra("card", JsonHelper.toJson(card))
                            }
                            intent.putExtra("phone", edtPhone.text.toString())
                            startActivity(intent)
                        }
                    }
                }
            }
        }

        btnAgainError.setOnClickListener {
            viewModel.getData(1)
        }
    }

    private fun displayData() {
        if (listBuyTopup.isNotEmpty()) {
            listBuyTopup[0].select = true
            card = listBuyTopup[0]

            listMenhGia.clear()

            for (i in 0 until (listBuyTopup[0].denomination as MutableList<String>).size) {
                val temp = ICMenhGia((listBuyTopup[0].denomination as MutableList<String>)[i])
                listMenhGia.add(temp)
            }
        }

        nhaMangAdapter.notifyDataSetChanged()
        menhGiaAdapter.notifyDataSetChanged()
    }

    private fun selectNetWork(position: Int) {
        for (temp in listBuyTopup) {
            temp.select = false
        }

        listBuyTopup[position].select = true

        try {
            value = ""
            card = listBuyTopup[position]
            try {
                listMenhGia.clear()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            listMenhGia.clear()

            (listBuyTopup[position].denomination as MutableList<String>).let {
                for (i in 0 until it.size) {
                    val temp = ICMenhGia(it[i])
                    listMenhGia.add(temp)
                }
                listMenhGia[0].select = true
                value = listMenhGia[0].menhGia
                tvPrice.setText(R.string.s_d, TextHelper.formatMoneyPhay(listMenhGia[0].menhGia.toLong()))
            }
            menhGiaAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.REFRESH_DATA -> {
                edtPhone.setText("")
                tvPrice.setText(R.string.zero_d)
                selectNetWork(0)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionContact) {
            if (PermissionHelper.checkResult(grantResults)) {
                val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                startActivityForResult(intent, requestContact)
            } else {
                showLongWarning(R.string.vui_long_cap_quyen_truy_cap_danh_ba_de_su_dung_chuc_nang_nay)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestContact) {
            if (resultCode == Activity.RESULT_OK) run {
                val contactData = data!!.data
                val c = this@RechargePhoneActivity.managedQuery(contactData, null, null, null, null) as Cursor
                if (c.moveToFirst()) {
                    val id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                    val hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    try {
                        if (hasPhone == "1") {
                            val phones = this@RechargePhoneActivity.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)
                            phones!!.moveToFirst()
                            val cNumber = phones.getString(phones.getColumnIndex("data1"))
                            val input = cNumber.replace(" ", "")
                            edtPhone.setText(input)
                        } else {
                            showShortToast(R.string.lien_he_nay_khong_co_so_dien_thoai)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            }
        }
    }
}
