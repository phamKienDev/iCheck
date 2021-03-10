package vn.icheck.android.screen.user.home_page.my_gift_warehouse.detail_my_gift_warehouse

import android.content.Intent
import android.os.Build
import android.text.Html
import android.text.Spannable
import android.view.View
import kotlinx.android.synthetic.main.activity_detail_my_gift_ware_house.*
import kotlinx.android.synthetic.main.item_message.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.base.dialog.notify.callback.NotEnoughPointListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.network.models.ICAcceptGift
import vn.icheck.android.network.models.ICDetailGift
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.comfirm_ship_gift.ComfirmShipGiftActivity
import vn.icheck.android.screen.user.detail_my_reward.bottom_sheet.RefuseRoundedBottomSheet
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.detail_my_gift_warehouse.presenter.DetailMyGiftWareHousePresenter
import vn.icheck.android.screen.user.home_page.my_gift_warehouse.detail_my_gift_warehouse.view.IDetailMyGiftWareHouseView
import vn.icheck.android.screen.user.orderdetail.OrderDetailActivity
import vn.icheck.android.util.kotlin.GlideImageGetter
import vn.icheck.android.util.kotlin.WidgetUtils

class DetailMyGiftWareHouseActivity : BaseActivity<DetailMyGiftWareHousePresenter>(), IDetailMyGiftWareHouseView {

    override val getLayoutID: Int
        get() = R.layout.activity_detail_my_gift_ware_house

    override val getPresenter: DetailMyGiftWareHousePresenter
        get() = DetailMyGiftWareHousePresenter(this)

    private var mId: String? = null
    private var objGift = ICDetailGift()

    override fun isRegisterEventBus(): Boolean {
        return true
    }

    override fun onInitView() {
        presenter.getDataIntent(intent)
        listener()
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        btnTryAgain.setOnClickListener {
            presenter.getDataIntent(intent)
        }

        btnRefuse.setOnClickListener {
            val myRoundedBottomSheet = RefuseRoundedBottomSheet(mId)
            myRoundedBottomSheet.show(supportFragmentManager, myRoundedBottomSheet.tag)
        }

        btnAcceptShipGift.setOnClickListener {
            startActivity<ComfirmShipGiftActivity, ICDetailGift>(Constant.DATA_1, objGift)
        }

        btnFollowGift.setOnClickListener {
            val intent = Intent(this@DetailMyGiftWareHouseActivity,OrderDetailActivity::class.java)
            intent.putExtra(Constant.DATA_1,objGift.order_id)
            startActivity(intent)
        }

        btnShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, objGift.image)
            startActivity(Intent.createChooser(intent, "Send To"))
        }

        btnAcceptDaLay.setOnClickListener {
            DialogHelper.showDialogComfirmDaLayQua(this,true,object : NotEnoughPointListener{
                override fun onClose() {
                }

                override fun onGiveIcoin() {
                    presenter.onAcceptDaLayQua(mId)
                }
            })
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)

        when (event.type) {
            ICMessageEvent.Type.REFRESH_DATA -> {
                presenter.getDetailGiftSecond(mId)
            }

            ICMessageEvent.Type.MESSAGE_ERROR -> {
                showShortError(event.data as String)
            }
        }
    }

    override fun onAcceptDaLayQuaSuccess(obj: ICAcceptGift) {
        DialogHelper.closeLoading(this)
        presenter.getDetailGiftSecond(mId)
    }

    override fun getDataByIntent(idGift: String) {
        mId = idGift
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)
        showShortError(errorMessage)
    }

    override fun onGetDetailError(type: Int) {
        layoutScroll.visibility = View.GONE
        layoutError.visibility = View.VISIBLE

        when (type) {
            Constant.ERROR_INTERNET -> {
                imgIcon.setImageResource(R.drawable.ic_no_network)
                txtMessage.text = getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
            }

            Constant.ERROR_UNKNOW -> {
                imgIcon.setImageResource(R.drawable.ic_error_request)
                txtMessage.text = getString(R.string.khong_lay_duoc_du_lieu_vui_long_thu_lai)
            }
        }
    }

    override fun getDataDetailGifSuccess(obj: ICDetailGift) {
        objGift = obj
        layoutScroll.visibility = View.VISIBLE
        layoutError.visibility = View.GONE

        txtTitle.text = obj.name
        WidgetUtils.loadImageUrl(imgBannerVendor, obj.image)
        WidgetUtils.loadImageUrl(avaVendor, obj.shop_image)
        tvNameVendor.text = obj.shop_name
        tvGiftName.text = obj.name

        if (obj.desc != null) {

            val imageGetter = GlideImageGetter(tvDesc)

            val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(obj.desc, Html.FROM_HTML_MODE_LEGACY, imageGetter, null) as Spannable
            } else {
                Html.fromHtml(obj.desc, imageGetter, null)
            }
            tvDesc.text = html
        } else {
            tvDesc.text = getString(R.string.dang_cap_nhat)
        }

        tvRemainTime.text = obj.remain_time

        when(obj.rewardType){
            "spirit" -> {
                btnShare.visibility = View.VISIBLE
                btnAcceptDaLay.visibility = View.GONE
                btnRefuse.visibility = View.GONE
                btnDaTuChoi.visibility = View.GONE
                btnAcceptShipGift.visibility = View.GONE
                btnFollowGift.visibility = View.GONE
            }

            "receive_store" -> {
                btnRefuse.visibility = View.VISIBLE
                btnAcceptDaLay.visibility = View.VISIBLE
                btnDaTuChoi.visibility = View.GONE
                btnAcceptShipGift.visibility = View.GONE
                btnShare.visibility = View.GONE
                btnFollowGift.visibility = View.GONE
            }

            "reward" -> {

            }
        }

        when (obj.state) {
            // Chua nhan
            1 -> {
                 // neu rewardType == receive_store thi show button tu choi va xac nhan ship con khong thi khong show gi
                if (obj.rewardType == "receive_store"){
                    btnRefuse.visibility = View.VISIBLE
                    btnAcceptDaLay.visibility = View.VISIBLE
                    btnDaTuChoi.visibility = View.GONE
                    btnAcceptShipGift.visibility = View.GONE
                    btnShare.visibility = View.GONE
                    btnFollowGift.visibility = View.GONE
                }else if (obj.rewardType == "reward"){
                    btnRefuse.visibility = View.VISIBLE
                    btnAcceptShipGift.visibility = View.VISIBLE
                    btnDaTuChoi.visibility = View.GONE
                    btnAcceptDaLay.visibility = View.GONE
                    btnShare.visibility = View.GONE
                    btnFollowGift.visibility = View.GONE
                }
            }
            // Da ship
            2 -> {
                btnFollowGift.visibility = View.VISIBLE
                btnDaTuChoi.visibility = View.GONE
                btnAcceptShipGift.visibility = View.GONE
                btnRefuse.visibility = View.GONE
                btnAcceptDaLay.visibility = View.GONE
                btnShare.visibility = View.GONE
            }
            // Tu choi
            3 -> {
                //show button da tu choi nhung k co action gi
                btnDaTuChoi.visibility = View.VISIBLE
                btnAcceptShipGift.visibility = View.GONE
                btnRefuse.visibility = View.GONE
                btnAcceptDaLay.visibility = View.GONE
                btnShare.visibility = View.GONE
                btnFollowGift.visibility = View.GONE
            }

            4 -> {
                btnDaTuChoi.visibility = View.GONE
                btnAcceptShipGift.visibility = View.GONE
                btnRefuse.visibility = View.GONE
                btnAcceptDaLay.visibility = View.GONE
                btnShare.visibility = View.GONE
                btnFollowGift.visibility = View.GONE
            }
        }
    }
}
