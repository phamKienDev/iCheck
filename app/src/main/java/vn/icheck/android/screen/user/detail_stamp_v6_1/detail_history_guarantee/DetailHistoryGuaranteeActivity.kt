package vn.icheck.android.screen.user.detail_stamp_v6_1.detail_history_guarantee

import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_detai_history_guarantee.*
import kotlinx.android.synthetic.main.toolbar_blue.*
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
import vn.icheck.android.screen.user.detail_stamp_v6_1.home.StampDetailActivity
import vn.icheck.android.screen.user.view_item_image_stamp.ViewItemImageActivity
import vn.icheck.android.ui.view.TextBody1
import vn.icheck.android.util.kotlin.WidgetUtils
import java.text.SimpleDateFormat

class DetailHistoryGuaranteeActivity : BaseActivity<DetailHistoryGuaranteePresenter>(), IDetaiHistoryGuaranteeView {

    override val getLayoutID: Int
        get() = R.layout.activity_detai_history_guarantee

    override val getPresenter: DetailHistoryGuaranteePresenter
        get() = DetailHistoryGuaranteePresenter(this)

    private val adapter = ListNoteHistoryAdapter()

    override fun onInitView() {
        if (StampDetailActivity.isVietNamLanguage == false) {
            txtTitle.text = "Details of warranty information"
            tvCustomerInfor.text = "Customer Information"
            tvWarrantyInfor.text = "Warranty Information"
        } else {
            txtTitle.text = "Chi tiết bảo hành"
            tvCustomerInfor.text = "Thông tin khách hàng"
            tvWarrantyInfor.text = "Thông tin bảo hành"
        }

        initRecyclerview()
        setupListener()

        presenter.getObjectIntent(intent)
    }

    private fun initRecyclerview() {
        val manager = object : LinearLayoutManager(this) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
    }

    private fun setupListener() {
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
//        if (DetailStampActivity.isVietNamLanguage == false) {
//            tvNameCustomer.text = if (!item.customer?.name.isNullOrEmpty()) {
//                Html.fromHtml("<font color=#434343>Name : </font>" + "<b>" + item.customer?.name + "</b>")
//            } else {
//                Html.fromHtml("<font color=#434343>Name : </font>" + "<b>" + "updating" + "</b>")
//            }
//        } else {
//            tvNameCustomer.text = if (!item.customer?.name.isNullOrEmpty()) {
//                Html.fromHtml("<font color=#434343>Họ và tên : </font>" + "<b>" + item.customer?.name + "</b>")
//            } else {
//                Html.fromHtml("<font color=#434343>Họ và tên : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
//            }
//        }
        tvNameCustomer.text = item.customer?.name

//        if (DetailStampActivity.isVietNamLanguage == false) {
//            tvPhoneCustomer.text = if (!item.customer?.phone.isNullOrEmpty()) {
//                Html.fromHtml("<font color=#434343>Tel : </font>" + "<b>" + item.customer?.phone + "</b>")
//            } else {
//                Html.fromHtml("<font color=#434343>Tel : </font>" + "<b>" + "updating" + "</b>")
//            }
//        } else {
//            tvPhoneCustomer.text = if (!item.customer?.phone.isNullOrEmpty()) {
//                Html.fromHtml("<font color=#434343>Số điện thoại : </font>" + "<b>" + item.customer?.phone + "</b>")
//            } else {
//                Html.fromHtml("<font color=#434343>Số điện thoại : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
//            }
//        }
        tvPhoneCustomer.text = item.customer?.phone

//        if (DetailStampActivity.isVietNamLanguage == false) {
//            tvMailCustomer.text = if (!item.customer?.email.isNullOrEmpty()) {
//                Html.fromHtml("<font color=#434343>Email : </font>" + "<b>" + item.customer?.email + "</b>")
//            } else {
//                Html.fromHtml("<font color=#434343>Email : </font>" + "<b>" + "updating" + "</b>")
//            }
//        } else {
//            tvMailCustomer.text = if (!item.customer?.email.isNullOrEmpty()) {
//                Html.fromHtml("<font color=#434343>Email : </font>" + "<b>" + item.customer?.email + "</b>")
//            } else {
//                Html.fromHtml("<font color=#434343>Email : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
//            }
//        }
        tvMailCustomer.text = item.customer?.email

//        if (DetailStampActivity.isVietNamLanguage == false) {
//            tvAddressCustomer.text = if (!item.customer?.address.isNullOrEmpty()) {
//                Html.fromHtml("<font color=#434343>Address : </font>" + "<b>" + item.customer?.address + "</b>")
//            } else {
//                Html.fromHtml("<font color=#434343>Address : </font>" + "<b>" + "updating" + "</b>")
//            }
//        } else {
//            tvAddressCustomer.text = if (!item.customer?.address.isNullOrEmpty()) {
//                Html.fromHtml("<font color=#434343>Địa chỉ : </font>" + "<b>" + item.customer?.address + "</b>")
//            } else {
//                Html.fromHtml("<font color=#434343>Địa chỉ : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
//            }
//        }
        tvAddressCustomer.text = item.customer?.address

//        if (DetailStampActivity.isVietNamLanguage == false) {
//            tvTimeGuarantee.text = if (!item.created_time.isNullOrEmpty()) {
//                Html.fromHtml("<font color=#434343>Time : </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateTimeVnStamp(item.created_time) + "</b>")
//            } else {
//                Html.fromHtml("<font color=#434343>Time : </font>" + "<b>" + "updating" + "</b>")
//            }
//        } else {
//            tvTimeGuarantee.text = if (!item.created_time.isNullOrEmpty()) {
//                Html.fromHtml("<font color=#434343>Thời gian : </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateTimeVnStamp(item.created_time) + "</b>")
//            } else {
//                Html.fromHtml("<font color=#434343>Thời gian : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
//            }
//        }
        tvTimeGuarantee.text = if (!item.created_time.isNullOrEmpty()) {
            TimeHelper.convertDateTimeSvToDateTimeVnStamp(item.created_time)
        } else {
            null
        }

//        if (DetailStampActivity.isVietNamLanguage == false) {
//            tvNameStoreGuarantee.text = if (!item.store?.name.isNullOrEmpty()) {
//                Html.fromHtml("<font color=#434343>Warranty Center : </font>" + "<b>" + item.store?.name + "</b>")
//            } else {
//                Html.fromHtml("<font color=#434343>Warranty Center : </font>" + "<b>" + "updating" + "</b>")
//            }
//        } else {
//            tvNameStoreGuarantee.text = if (!item.store?.name.isNullOrEmpty()) {
//                Html.fromHtml("<font color=#434343>Điểm bảo hành : </font>" + "<b>" + item.store?.name + "</b>")
//            } else {
//                Html.fromHtml("<font color=#434343>Điểm bảo hành : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
//            }
//        }
        tvNameStoreGuarantee.text = item.store?.name

//        if (DetailStampActivity.isVietNamLanguage == false) {
//            tvStateGuarantee.text = if (!item.state?.name.isNullOrEmpty()) {
//                Html.fromHtml("<font color=#434343>Warranty Status : </font>" + "<b>" + item.state?.name + "</b>")
//            } else {
//                Html.fromHtml("<font color=#434343>Warranty Status : </font>" + "<b>" + "updating" + "</b>")
//            }
//        } else {
//            tvStateGuarantee.text = if (!item.state?.name.isNullOrEmpty()) {
//                Html.fromHtml("<font color=#434343>Tình trạng : </font>" + "<b>" + item.state?.name + "</b>")
//            } else {
//                Html.fromHtml("<font color=#434343>Tình trạng : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
//            }
//        }
        tvStateGuarantee.text = item.state?.name

//        if (DetailStampActivity.isVietNamLanguage == false){
//            tvReturnTimeGuarantee.text = if (!item.return_time.isNullOrEmpty()) {
//                Html.fromHtml("<font color=#434343>Date of return : </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateTimeVnStamp(item.return_time) + "</b>")
//            } else {
//                Html.fromHtml("<font color=#434343>Date of return : </font>" + "<b>" + "updating" + "</b>")
//            }
//        } else {
//            tvReturnTimeGuarantee.text = if (!item.return_time.isNullOrEmpty()) {
//                Html.fromHtml("<font color=#434343>Ngày hẹn trả : </font>" + "<b>" + TimeHelper.convertDateTimeSvToDateTimeVnStamp(item.return_time) + "</b>")
//            } else {
//                Html.fromHtml("<font color=#434343>Ngày hẹn trả : </font>" + "<b>" + getString(R.string.dang_cap_nhat) + "</b>")
//            }
//        }
        tvReturnTimeGuarantee.text = if (!item.return_time.isNullOrEmpty()) {
            TimeHelper.convertDateTimeSvToDateTimeVnStamp(item.return_time)
        } else {
            null
        }

        if (!item.note.isNullOrEmpty()) {
            if (StampDetailActivity.isVietNamLanguage == false) {
                layoutContent.addView(createTableRow("Notes of Warranty return:", item.note))
            } else {
                layoutContent.addView(createTableRow("Ghi chú trả BH:", item.note))
            }
        }

        for (i in item.fields ?: mutableListOf()) {
            if (!i.value.isNullOrEmpty() && !i.name.isNullOrEmpty()) {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                try {
                    val value = sdf.parse(i.value!!)
                    if (value != null) {
                        layoutContent.addView(createTableRow(i.name, TimeHelper.convertDateTimeSvToDateVn(i.value)))
                    } else {
                        layoutContent.addView(createTableRow(i.name, i.value))
                    }
                } catch (e: Exception) {
                    layoutContent.addView(createTableRow(i.name, i.value))
                }
            }
        }

        if (item.extra != null) {
            if (StampDetailActivity.isVietNamLanguage == false) {
                layoutContent.addView(createTableRow("Variation:", item.extra?.extra))
            } else {
                layoutContent.addView(createTableRow("Biến thể:", item.extra?.extra))
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

        if (!list.isNullOrEmpty()) {
            adapter.setListData(list)
        }
    }

    private fun createTableRow(title: String?, content: String?): TableRow {
        return TableRow(this).apply {
            layoutParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT).apply {
                topMargin = SizeHelper.size8
            }

            addView(TextBody1(context).apply {
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f).apply {
                    marginEnd = SizeHelper.size12
                }
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_warranty_blue_18px, 0, 0, 0)
                compoundDrawablePadding = SizeHelper.size8
                setTextColor(ContextCompat.getColor(context, R.color.darkGray3))
                textSize = 14f
                text = title
            })

            addView(TextBody1(context).apply {
                layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                compoundDrawablePadding = SizeHelper.size8
                setTextColor(ContextCompat.getColor(context, R.color.black2))
                setHintTextColor(ContextCompat.getColor(context, R.color.black2))
                gravity = Gravity.END
                textAlignment = View.TEXT_ALIGNMENT_GRAVITY
                textSize = 14f

                setHint(R.string.dang_cap_nhat)
                text = content
            })
        }
    }
}
