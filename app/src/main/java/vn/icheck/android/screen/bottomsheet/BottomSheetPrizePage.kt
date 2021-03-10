package vn.icheck.android.screen.bottomsheet

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.recycler_view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.component.popup_view.BaseBottomSheetView
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICPageInformations

open class BottomSheetPrizePage (context: Context, val data: ICPageInformations) : BaseBottomSheetView(context, true) {
    override val getLayoutID: Int
        get() = R.layout.recycler_view

    override val title: String?
        get() = data.name

    override fun onInitView() {
        if (!data.data.isNullOrEmpty()){
            dialog.recyclerView.layoutManager = LinearLayoutManager(ICheckApplication.getInstance(), LinearLayoutManager.VERTICAL, false)
            dialog.recyclerView.adapter = BottomSheetPageAdapter(data.data!!, SizeHelper.size50, SizeHelper.size50, 16f, 14f, SizeHelper.size12)
        }
    }
}