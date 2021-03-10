package vn.icheck.android.screen.user.share_post_of_page.bottom_sheet_option_page

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.dialog_option_page_share.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialog
import vn.icheck.android.callback.ItemClickListener
import vn.icheck.android.constant.Constant
import vn.icheck.android.network.models.ICChoosePage
import vn.icheck.android.network.models.ICPageUserManager
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.network.models.ICUser
import vn.icheck.android.screen.user.share_post_of_page.SharePostActivity
import vn.icheck.android.util.kotlin.ToastUtils

class OptionPageShare (context: Context) : BaseBottomSheetDialog(context, R.layout.dialog_option_page_share, true) {

    private val adapter = OptionPageShareAdapter(dialog.context)

    fun show(listPage: MutableList<ICPageUserManager>, user: ICUser,post:ICPost) {
        dialog.recyclerView.layoutManager = LinearLayoutManager(dialog.context)
        dialog.recyclerView.adapter = adapter
        adapter.setListData(listPage)
        adapter.setOnClickItemListener(object :ItemClickListener<ICPageUserManager>{
            override fun onItemClick(position: Int, item: ICPageUserManager?) {
                val intent = Intent(dialog.context, SharePostActivity::class.java)
                intent.putExtra(Constant.DATA_1, post)
                intent.putExtra(Constant.DATA_3, item)
                dialog.context.startActivity(intent)
            }
        })

        dialog.imgCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}