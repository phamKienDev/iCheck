package vn.icheck.android.screen.bottomsheet

import android.content.Context
import kotlinx.android.synthetic.main.bottom_sheet_information_page.*
import vn.icheck.android.R
import vn.icheck.android.component.popup_view.BaseBottomSheetView
import vn.icheck.android.network.models.ICPageInformations

open class BottomSheetInformationPage(context: Context, val data: ICPageInformations) : BaseBottomSheetView(context, true) {

    override val getLayoutID: Int
        get() = R.layout.bottom_sheet_information_page

    override val title: String?
        get() = data.name

    override fun onInitView() {
        if (!data.description.isNullOrEmpty()) {
            dialog.tvDetail.text = data.description
        }
    }
}