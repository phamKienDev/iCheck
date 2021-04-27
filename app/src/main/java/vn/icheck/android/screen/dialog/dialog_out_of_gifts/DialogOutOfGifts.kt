package vn.icheck.android.screen.dialog.dialog_out_of_gifts

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_out_of_gifts.*
import vn.icheck.android.R
import vn.icheck.android.component.popup_view.BasePopupView
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.network.models.ICGiftReceived
import vn.icheck.android.util.kotlin.ToastUtils

abstract class DialogOutOfGifts(context: Context, val default: Int, val title: String, val message: String, val listData: MutableList<ICGiftReceived>) : BasePopupView(context, R.style.DialogTheme) {
    override val getLayoutID: Int
        get() = R.layout.dialog_out_of_gifts

    override val getIsCancelable: Boolean
        get() = true

    override fun onInitView() {
        imgDefault.setImageResource(default)

        txtTitle.text = title

        txtMessage.text = message

        btnClose.setOnClickListener {
            dismiss()
        }

        btnThucHien.apply {
            background = ViewHelper.bgOutlinePrimary1Corners4(context)
            setOnClickListener {
                ToastUtils.showLongWarning(context, "onClick Thực Hiện Thử Thách")
            }
        }

        btnListGifts.setOnClickListener {
            ToastUtils.showLongWarning(context, "onClick Danh sách quà")
        }

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = OutOfGiftsAdapter(listData)
    }
}