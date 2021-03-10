package vn.icheck.android.screen.user.detail_stamp_v6_1.detail_history_guarantee

import android.graphics.Typeface
import android.text.Html
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_detai_history_guarantee.*
import kotlinx.android.synthetic.main.activity_detai_history_guarantee.layoutContentHistory
import kotlinx.android.synthetic.main.toolbar_blue.imgBack
import kotlinx.android.synthetic.main.toolbar_blue.txtTitle
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivity
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICListHistoryGuarantee
import vn.icheck.android.network.models.detail_stamp_v6_1.ICResp_Note_Guarantee
import vn.icheck.android.screen.user.detail_stamp_v6_1.detail_history_guarantee.adapter.ListNoteHistoryAdapter
import vn.icheck.android.screen.user.detail_stamp_v6_1.detail_history_guarantee.presenter.DetailHistoryGuaranteePresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.detail_history_guarantee.view.IDetaiHistoryGuaranteeView
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.DetailStampActivity
import vn.icheck.android.screen.user.view_item_image_stamp.ViewItemImageActivity
import vn.icheck.android.util.kotlin.WidgetUtils
import java.text.SimpleDateFormat

class DetaiHistoryGuaranteeActivity : BaseActivity<DetailHistoryGuaranteePresenter>(), IDetaiHistoryGuaranteeView {

    override val getLayoutID: Int
        get() = R.layout.activity_detai_history_guarantee

    override val getPresenter: DetailHistoryGuaranteePresenter
        get() = DetailHistoryGuaranteePresenter(this)

    private lateinit var adapter: ListNoteHistoryAdapter

    override fun onInitView() {
        if (DetailStampActivity.isVietNamLanguage == false){
            txtTitle.text = "Details of warranty information"
            tvCustomerInfor.text = "Customer Information"
            tvWarrantyInfor.text = "Warranty Information"
        } else {
            txtTitle.text = "Chi tiết bảo hành"
            tvCustomerInfor.text = "Thông tin khách hàng"
            tvWarrantyInfor.text = "Thông tin bảo hành"
        }

        listener()
        initRecyclerview()

        presenter.getObjectIntent(intent)
    }

    private fun initRecyclerview() {
        adapter = ListNoteHistoryAdapter(this)
        val manager = object :LinearLayoutManager(this){
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        recyclerViewNote.layoutManager = manager
        recyclerViewNote.adapter = adapter
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun getDataError(errorInternet: Int) {
        showShortError(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai)
    }

    override fun showError(errorMessage: String) {
        super.showError(errorMessage)
        showShortError(errorMessage)
    }

    override fun getObjectIntentSuccess(item: ICListHistoryGuarantee, list: MutableList<ICResp_Note_Guarantee.ObjectLog.ObjectChildLog.ICItemNote>?) {

        if (DetailStampActivity.isVietNamLanguage == false) {
            tvNameCustomer.text = if (!item.customer?.name.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>Name : </font>" + "<b>" + item.customer?.name + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>Name : </font>" + "<b>" + "updating" + "</b>")
            }
        } else {
            tvNameCustomer.text = if (!item.customer?.name.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>Họ và tên : </font>" + "<b>" + item.customer?.name + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>Họ và tên : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
            }
        }

        if (DetailStampActivity.isVietNamLanguage == false) {
            tvPhoneCustomer.text = if (!item.customer?.phone.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>Tel : </font>" + "<b>" + item.customer?.phone + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>Tel : </font>" + "<b>" + "updating" + "</b>")
            }
        } else {
            tvPhoneCustomer.text = if (!item.customer?.phone.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>Số điện thoại : </font>" + "<b>" + item.customer?.phone + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>Số điện thoại : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
            }
        }

        if (DetailStampActivity.isVietNamLanguage == false) {
            tvMailCustomer.text = if (!item.customer?.email.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>Email : </font>" + "<b>" + item.customer?.email + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>Email : </font>" + "<b>" + "updating" + "</b>")
            }
        } else {
            tvMailCustomer.text = if (!item.customer?.email.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>Email : </font>" + "<b>" + item.customer?.email + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>Email : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
            }
        }

        if (DetailStampActivity.isVietNamLanguage == false) {
            tvAddressCustomer.text = if (!item.customer?.address.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>Address : </font>" + "<b>" + item.customer?.address + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>Address : </font>" + "<b>" + "updating" + "</b>")
            }
        } else {
            tvAddressCustomer.text = if (!item.customer?.address.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>Địa chỉ : </font>" + "<b>" + item.customer?.address + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>Địa chỉ : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
            }
        }

        if (DetailStampActivity.isVietNamLanguage == false) {
            tvTimeGuarantee.text = if (!item.created_time.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>Time : </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateTimeVnStamp(item.created_time) + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>Time : </font>" + "<b>" + "updating" + "</b>")
            }
        } else {
            tvTimeGuarantee.text = if (!item.created_time.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>Thời gian : </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateTimeVnStamp(item.created_time) + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>Thời gian : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
            }
        }

        if (DetailStampActivity.isVietNamLanguage == false) {
            tvNameStoreGuarantee.text = if (!item.store?.name.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>Warranty Center : </font>" + "<b>" + item.store?.name + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>Warranty Center : </font>" + "<b>" + "updating" + "</b>")
            }
        } else {
            tvNameStoreGuarantee.text = if (!item.store?.name.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>Điểm bảo hành : </font>" + "<b>" + item.store?.name + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>Điểm bảo hành : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
            }
        }

        if (DetailStampActivity.isVietNamLanguage == false) {
            tvStateGuarantee.text = if (!item.state?.name.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>Warranty Status : </font>" + "<b>" + item.state?.name + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>Warranty Status : </font>" + "<b>" + "updating" + "</b>")
            }
        } else {
            tvStateGuarantee.text = if (!item.state?.name.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>Tình trạng : </font>" + "<b>" + item.state?.name + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>Tình trạng : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
            }
        }

        if (DetailStampActivity.isVietNamLanguage == false){
            tvReturnTimeGuarantee.text = if (!item.return_time.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>Date of return : </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateTimeVnStamp(item.return_time) + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>Date of return : </font>" + "<b>" + "updating" + "</b>")
            }
        } else {
            tvReturnTimeGuarantee.text = if (!item.return_time.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>Ngày hẹn trả : </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateTimeVnStamp(item.return_time) + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>Ngày hẹn trả : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
            }
        }

        if (!item.note.isNullOrEmpty()) {
            if (DetailStampActivity.isVietNamLanguage == false) {
                layoutContentHistory.addView(AppCompatTextView(this).also { textNote ->
                    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams.setMargins(0, SizeHelper.size8, 0, 0)
                    textNote.layoutParams = layoutParams
                    textNote.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    textNote.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                    textNote.gravity = Gravity.CENTER or Gravity.START
                    textNote.setTextColor(ContextCompat.getColor(this, R.color.black))
                    textNote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_blue_18px, 0, 0, 0)
                    textNote.compoundDrawablePadding = SizeHelper.size8
                    textNote.text = Html.fromHtml("<font color=#434343>Notes of Warranty return: </font>" + "<b>" + item.note + "</b>")
                })
            } else {
                layoutContentHistory.addView(AppCompatTextView(this).also { textNote ->
                    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams.setMargins(0, SizeHelper.size8, 0, 0)
                    textNote.layoutParams = layoutParams
                    textNote.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    textNote.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                    textNote.gravity = Gravity.CENTER or Gravity.START
                    textNote.setTextColor(ContextCompat.getColor(this, R.color.black))
                    textNote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_blue_18px, 0, 0, 0)
                    textNote.compoundDrawablePadding = SizeHelper.size8
                    textNote.text = Html.fromHtml("<font color=#434343>Ghi chú trả BH: </font>" + "<b>" + item.note + "</b>")
                })
            }
        }

        for (i in item.fields ?: mutableListOf()) {
            if (!i.value.isNullOrEmpty() && !i.name.isNullOrEmpty()) {
                layoutContentHistory.addView(AppCompatTextView(this).also { text ->
                    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams.setMargins(0, SizeHelper.size8, 0, 0)
                    text.layoutParams = layoutParams
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    text.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                    text.gravity = Gravity.CENTER or Gravity.START
                    text.setTextColor(ContextCompat.getColor(this, R.color.black))
                    text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_blue_18px, 0, 0, 0)
                    text.compoundDrawablePadding = SizeHelper.size8

                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    try {
                        val value = sdf.parse(i.value!!)
                        if (value != null) {
                            text.text = Html.fromHtml("<font color=#434343>${i.name}: </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateVn(i.value) + "</b>")
                        } else {
                            text.text = Html.fromHtml("<font color=#434343>${i.name}: </font>" + "<b>" + i.value + "</b>")
                        }
                    } catch (e: Exception) {
                        text.text = Html.fromHtml("<font color=#434343>${i.name}: </font>" + "<b>" + i.value + "</b>")
                    }
                })
            }
        }

        if (item.extra != null) {
            if (DetailStampActivity.isVietNamLanguage == false) {
                layoutContentHistory.addView(AppCompatTextView(this).also { text ->
                    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams.setMargins(0, SizeHelper.size8, 0, 0)
                    text.layoutParams = layoutParams
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    text.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                    text.gravity = Gravity.CENTER or Gravity.START
                    text.text = Html.fromHtml("<font color=#434343>Variation: </font>" + "<b>" + item.extra?.extra + "</b>")
                    text.setTextColor(ContextCompat.getColor(this, R.color.black))
                    text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time_start_blue_18_px, 0, 0, 0)
                    text.compoundDrawablePadding = SizeHelper.size8
                })
            } else {
                layoutContentHistory.addView(AppCompatTextView(this).also { text ->
                    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    layoutParams.setMargins(0, SizeHelper.size8, 0, 0)
                    text.layoutParams = layoutParams
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    text.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                    text.gravity = Gravity.CENTER or Gravity.START
                    text.text = Html.fromHtml("<font color=#434343>Biến thể: </font>" + "<b>" + item.extra?.extra + "</b>")
                    text.setTextColor(ContextCompat.getColor(this, R.color.black))
                    text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time_start_blue_18_px, 0, 0, 0)
                    text.compoundDrawablePadding = SizeHelper.size8
                })
            }
        }

        if (!item.images.isNullOrEmpty()) {
            layoutImage.visibility = View.VISIBLE
            if (item.images!!.size <= 5) {
                for (i in item.images ?: mutableListOf()) {
                    layoutImage.addView(AppCompatImageView(this).also { itemImage ->
                        val paramsImage = LinearLayout.LayoutParams(SizeHelper.size40, SizeHelper.size40)
                        paramsImage.setMargins(0, 0, SizeHelper.size8, 0)
                        itemImage.layoutParams = paramsImage
                        itemImage.scaleType = ImageView.ScaleType.FIT_CENTER
                        WidgetUtils.loadImageUrlRounded(itemImage, i, SizeHelper.size4)

                        itemImage.setOnClickListener {
                            startActivity<ViewItemImageActivity, String>(Constant.DATA_1, i)
                        }
                    })
                }
            }
        }

        if (!list.isNullOrEmpty()){
            adapter.setListData(list)
        }
    }
}
