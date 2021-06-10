package vn.icheck.android.screen.user.detail_stamp_v6_1.detail_history_guarantee

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
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
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.constant.Constant
import vn.icheck.android.databinding.ItemDetailHistoryGuaranteeBinding
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.DialogHelper
import vn.icheck.android.ichecklibs.util.beGone
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

class DetailHistoryGuaranteeActivity : BaseActivityMVVM(), IDetaiHistoryGuaranteeView {

    private val presenter = DetailHistoryGuaranteePresenter(this)

    private val adapter = ListNoteHistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detai_history_guarantee)

        txtTitle.text = "Chi tiết bảo hành"
        tvCustomerInfor.text = "Thông tin khách hàng"
        tvWarrantyInfor.text = "Thông tin bảo hành"

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
        showShortError(errorMessage)
    }

    override val mContext: Context
        get() = this

    override fun onShowLoading(isShow: Boolean) {
        if (isShow)
            DialogHelper.showLoading(this)
        else
            DialogHelper.closeLoading(this)
    }

    override fun getObjectIntentSuccess(item: ICListHistoryGuarantee, list: MutableList<ICResp_Note_Guarantee.ObjectLog.ObjectChildLog.ICItemNote>?) {
        tvNameCustomer.text = item.customer?.name
        tvPhoneCustomer.text = item.customer?.phone
        tvMailCustomer.text = item.customer?.email
        tvAddressCustomer.text = item.customer?.address
        for (field in item.customer?.fields ?: mutableListOf()) {
            val binding = ItemDetailHistoryGuaranteeBinding.inflate(LayoutInflater.from(this), layoutCustomer, false)
            binding.tvTitle.text = field.name
            if (field.type == "date") {
                binding.tvContent.text = vn.icheck.android.ichecklibs.TimeHelper.convertDateTimeSvToDateVn(field.value)
            } else {
                binding.tvContent.text = field.value
            }
            layoutCustomer.addView(binding.root)
        }

        tvTimeGuarantee.text = if (!item.created_time.isNullOrEmpty()) {
            TimeHelper.convertDateTimeSvToDateTimeVnStamp(item.created_time)
        } else {
            null
        }
        tvNameStoreGuarantee.text = item.store?.name
        tvStateGuarantee.text = item.state?.name
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
                        layoutContent.addView(createTableRow(i.name, TimeHelper.convertDateTimeSvToTimeDateVnPhay(i.value)))
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
