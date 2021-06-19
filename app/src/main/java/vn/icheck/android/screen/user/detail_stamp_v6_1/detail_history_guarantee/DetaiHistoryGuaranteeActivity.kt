package vn.icheck.android.screen.user.detail_stamp_v6_1.detail_history_guarantee

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
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
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.DialogHelper
import vn.icheck.android.network.models.detail_stamp_v6_1.ICListHistoryGuarantee
import vn.icheck.android.network.models.detail_stamp_v6_1.ICResp_Note_Guarantee
import vn.icheck.android.screen.user.detail_stamp_v6_1.detail_history_guarantee.adapter.ListNoteHistoryAdapter
import vn.icheck.android.screen.user.detail_stamp_v6_1.detail_history_guarantee.presenter.DetailHistoryGuaranteePresenter
import vn.icheck.android.screen.user.detail_stamp_v6_1.detail_history_guarantee.view.IDetaiHistoryGuaranteeView
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.screen.user.view_item_image_stamp.ViewItemImageActivity
import vn.icheck.android.util.ick.rText
import vn.icheck.android.util.kotlin.WidgetUtils
import java.text.SimpleDateFormat

class DetaiHistoryGuaranteeActivity : BaseActivityMVVM(), IDetaiHistoryGuaranteeView {

    val presenter = DetailHistoryGuaranteePresenter(this@DetaiHistoryGuaranteeActivity)

    private lateinit var adapter: ListNoteHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detai_history_guarantee)
        onInitView()
    }

    fun onInitView() {
        if (StampDetailActivity.isVietNamLanguage == false) {
            txtTitle rText R.string.detail_of_warranty_information
            tvCustomerInfor rText R.string.customer_information
            tvWarrantyInfor rText R.string.warranty_information
        } else {
            txtTitle rText R.string.chi_tiet_bao_hanh
            tvCustomerInfor rText R.string.thong_tin_khach_hang
            tvWarrantyInfor rText R.string.thong_tin_bao_hanh
        }

        listener()
        initRecyclerview()

        presenter.getObjectIntent(intent)
    }

    private fun initRecyclerview() {
        adapter = ListNoteHistoryAdapter()
        val manager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
//        recyclerViewNote.layoutManager = manager
//        recyclerViewNote.adapter = adapter
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
        showShortError(errorMessage)
    }

    override val mContext: Context
        get() = this@DetaiHistoryGuaranteeActivity

    override fun onShowLoading(isShow: Boolean) {
        DialogHelper.showLoading(this@DetaiHistoryGuaranteeActivity, isShow)
    }

    override fun getObjectIntentSuccess(item: ICListHistoryGuarantee, list: MutableList<ICResp_Note_Guarantee.ObjectLog.ObjectChildLog.ICItemNote>?) {

        if (StampDetailActivity.isVietNamLanguage == false) {
            tvNameCustomer.text = if (!item.customer?.name.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>${rText(R.string.name)} : </font>" + "<b>" + item.customer?.name + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>${rText(R.string.name)} : </font>" + "<b>" + rText(R.string.updating) + "</b>")
            }
        } else {
            tvNameCustomer.text = if (!item.customer?.name.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>${rText(R.string.ho_va_ten)} : </font>" + "<b>" + item.customer?.name + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>${rText(R.string.ho_va_ten)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
            }
        }

        if (StampDetailActivity.isVietNamLanguage == false) {
            tvPhoneCustomer.text = if (!item.customer?.phone.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>${rText(R.string.tel)} : </font>" + "<b>" + item.customer?.phone + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>${rText(R.string.tel)} : </font>" + "<b>" + rText(R.string.updating) + "</b>")
            }
        } else {
            tvPhoneCustomer.text = if (!item.customer?.phone.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>${rText(R.string.so_dien_thoai)} : </font>" + "<b>" + item.customer?.phone + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>${rText(R.string.so_dien_thoai)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
            }
        }

        if (StampDetailActivity.isVietNamLanguage == false) {
            tvMailCustomer.text = if (!item.customer?.email.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>${rText(R.string.email)} : </font>" + "<b>" + item.customer?.email + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>${rText(R.string.email)} : </font>" + "<b>" + rText(R.string.updating) + "</b>")
            }
        } else {
            tvMailCustomer.text = if (!item.customer?.email.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>${rText(R.string.email)} : </font>" + "<b>" + item.customer?.email + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>${rText(R.string.email)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
            }
        }

        if (StampDetailActivity.isVietNamLanguage == false) {
            tvAddressCustomer.text = if (!item.customer?.address.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>${rText(R.string.address)} : </font>" + "<b>" + item.customer?.address + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>${rText(R.string.address)} : </font>" + "<b>" + rText(R.string.updating) + "</b>")
            }
        } else {
            tvAddressCustomer.text = if (!item.customer?.address.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>${rText(R.string.dia_chi)} : </font>" + "<b>" + item.customer?.address + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>${rText(R.string.dia_chi)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
            }
        }

        if (StampDetailActivity.isVietNamLanguage == false) {
            tvTimeGuarantee.text = if (!item.created_time.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>${rText(R.string.time)} : </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateTimeVnStamp(item.created_time) + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>${rText(R.string.time)} : </font>" + "<b>" + rText(R.string.updating) + "</b>")
            }
        } else {
            tvTimeGuarantee.text = if (!item.created_time.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>${rText(R.string.thoi_gian)} : </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateTimeVnStamp(item.created_time) + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>${rText(R.string.thoi_gian)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
            }
        }

        if (StampDetailActivity.isVietNamLanguage == false) {
            tvNameStoreGuarantee.text = if (!item.store?.name.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>${rText(R.string.warranty_center)} : </font>" + "<b>" + item.store?.name + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>${rText(R.string.warranty_center)} : </font>" + "<b>" + rText(R.string.updating) + "</b>")
            }
        } else {
            tvNameStoreGuarantee.text = if (!item.store?.name.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>${rText(R.string.diem_bao_hanh)} : </font>" + "<b>" + item.store?.name + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>${rText(R.string.diem_bao_hanh)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
            }
        }

        if (StampDetailActivity.isVietNamLanguage == false) {
            tvStateGuarantee.text = if (!item.state?.name.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>${rText(R.string.warranty_status)} : </font>" + "<b>" + item.state?.name + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>${rText(R.string.warranty_status)} : </font>" + "<b>" + rText(R.string.updating) + "</b>")
            }
        } else {
            tvStateGuarantee.text = if (!item.state?.name.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>${rText(R.string.tinh_trang)} : </font>" + "<b>" + item.state?.name + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>${rText(R.string.tinh_trang)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
            }
        }

        if (StampDetailActivity.isVietNamLanguage == false) {
            tvReturnTimeGuarantee.text = if (!item.return_time.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>${rText(R.string.date_of_return)} : </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateTimeVnStamp(item.return_time) + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>${rText(R.string.date_of_return)} : </font>" + "<b>" + rText(R.string.updating) + "</b>")
            }
        } else {
            tvReturnTimeGuarantee.text = if (!item.return_time.isNullOrEmpty()) {
                Html.fromHtml("<font color=#434343>${rText(R.string.ngay_hen_tra)} : </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateTimeVnStamp(item.return_time) + "</b>")
            } else {
                Html.fromHtml("<font color=#434343>${rText(R.string.ngay_hen_tra)} : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
            }
        }

        if (!item.note.isNullOrEmpty()) {
//            if (StampDetailActivity.isVietNamLanguage == false) {
//                layoutContentHistory.addView(AppCompatTextView(this).also { textNote ->
//                    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                    layoutParams.setMargins(0, SizeHelper.size8, 0, 0)
//                    textNote.layoutParams = layoutParams
//                    textNote.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
//                    textNote.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
//                    textNote.gravity = Gravity.CENTER or Gravity.START
//                    textNote.setTextColor(ContextCompat.getColor(this, R.color.black))
//                    textNote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_blue_18px, 0, 0, 0)
//                    textNote.compoundDrawablePadding = SizeHelper.size8
//                    textNote.text = Html.fromHtml("<font color=#434343>Notes of Warranty return: </font>" + "<b>" + item.note + "</b>")
//                })
//            } else {
//                layoutContentHistory.addView(AppCompatTextView(this).also { textNote ->
//                    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                    layoutParams.setMargins(0, SizeHelper.size8, 0, 0)
//                    textNote.layoutParams = layoutParams
//                    textNote.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
//                    textNote.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
//                    textNote.gravity = Gravity.CENTER or Gravity.START
//                    textNote.setTextColor(ContextCompat.getColor(this, R.color.black))
//                    textNote.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_blue_18px, 0, 0, 0)
//                    textNote.compoundDrawablePadding = SizeHelper.size8
//                    textNote.text = Html.fromHtml("<font color=#434343>Ghi chú trả BH: </font>" + "<b>" + item.note + "</b>")
//                })
//            }
        }

        for (i in item.fields ?: mutableListOf()) {
            if (!i.value.isNullOrEmpty() && !i.name.isNullOrEmpty()) {
//                layoutContentHistory.addView(AppCompatTextView(this).also { text ->
//                    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                    layoutParams.setMargins(0, SizeHelper.size8, 0, 0)
//                    text.layoutParams = layoutParams
//                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
//                    text.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
//                    text.gravity = Gravity.CENTER or Gravity.START
//                    text.setTextColor(ContextCompat.getColor(this, R.color.black))
//                    text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_blue_18px, 0, 0, 0)
//                    text.compoundDrawablePadding = SizeHelper.size8
//
//                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//                    try {
//                        val value = sdf.parse(i.value!!)
//                        if (value != null) {
//                            text.text = Html.fromHtml("<font color=#434343>${i.name}: </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateVn(i.value) + "</b>")
//                        } else {
//                            text.text = Html.fromHtml("<font color=#434343>${i.name}: </font>" + "<b>" + i.value + "</b>")
//                        }
//                    } catch (e: Exception) {
//                        text.text = Html.fromHtml("<font color=#434343>${i.name}: </font>" + "<b>" + i.value + "</b>")
//                    }
//                })
            }
        }

        if (item.extra != null) {
//            if (StampDetailActivity.isVietNamLanguage == false) {
//                layoutContentHistory.addView(AppCompatTextView(this).also { text ->
//                    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                    layoutParams.setMargins(0, SizeHelper.size8, 0, 0)
//                    text.layoutParams = layoutParams
//                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
//                    text.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
//                    text.gravity = Gravity.CENTER or Gravity.START
//                    text.text = Html.fromHtml("<font color=#434343>Variation: </font>" + "<b>" + item.extra?.extra + "</b>")
//                    text.setTextColor(ContextCompat.getColor(this, R.color.black))
//                    text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time_start_blue_18_px, 0, 0, 0)
//                    text.compoundDrawablePadding = SizeHelper.size8
//                })
//            } else {
//                layoutContentHistory.addView(AppCompatTextView(this).also { text ->
//                    val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//                    layoutParams.setMargins(0, SizeHelper.size8, 0, 0)
//                    text.layoutParams = layoutParams
//                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
//                    text.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
//                    text.gravity = Gravity.CENTER or Gravity.START
//                    text.text = Html.fromHtml("<font color=#434343>Biến thể: </font>" + "<b>" + item.extra?.extra + "</b>")
//                    text.setTextColor(ContextCompat.getColor(this, R.color.black))
//                    text.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_time_start_blue_18_px, 0, 0, 0)
//                    text.compoundDrawablePadding = SizeHelper.size8
//                })
//            }
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

        if (!list.isNullOrEmpty()) {
            adapter.setListData(list)
        }
    }
}
