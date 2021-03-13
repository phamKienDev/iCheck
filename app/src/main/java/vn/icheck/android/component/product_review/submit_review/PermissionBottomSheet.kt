package vn.icheck.android.component.product_review.submit_review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_select_permission_user.*
import kotlinx.android.synthetic.main.item_select_permission.view.*
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.adapter.RecyclerViewAdapter
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.models.ICPage
import vn.icheck.android.network.models.ICCommentPermission
import vn.icheck.android.util.kotlin.WidgetUtils

class PermissionBottomSheet(val listener: PermissionListener) : BaseBottomSheetDialogFragment() {
    lateinit var adapter: PermissionAdapter
    private var permission: ICCommentPermission? = null
    private var selectedPermission: ICCommentPermission? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog?.setOnShowListener {
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout?
            bottomSheet?.setBackgroundResource(R.drawable.rounded_dialog)
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_EXPANDED
        }
        return inflater.inflate(R.layout.dialog_select_permission_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PermissionAdapter()
        recyclerView.adapter = adapter

        selectedPermission = SettingManager.getPostPermission()
        permission = selectedPermission

        getListPermission()

        btnConfirm.setOnClickListener {
            if (permission != selectedPermission) {
                SettingManager.setPostPermission(selectedPermission)
                listener.getPermission(selectedPermission)
            }
            dismiss()
        }
        imgClose.setOnClickListener {
            dismiss()
        }
    }

    private fun getListPermission() {
        val listPermission = mutableListOf<ICCommentPermission>()

        if (NetworkHelper.isNotConnected(ICheckApplication.getInstance())) {
            adapter.setError(R.drawable.ic_error_network, requireContext().getString(R.string.khong_co_ket_noi_mang_vui_long_kiem_tra_va_thu_lai), R.string.thu_lai)
            return
        }

        SessionManager.session.user?.let { user ->
            listPermission.add(ICCommentPermission(user.id, user.avatar, user.getName, Constant.USER))
        }

        PageRepository().getMyOwnerPage(null, 20, 0, object : ICNewApiListener<ICResponse<ICListResponse<ICPage>>> {
            override fun onSuccess(obj: ICResponse<ICListResponse<ICPage>>) {
                for (item in obj.data!!.rows) {
                    listPermission.add(ICCommentPermission(item.id, item.avatar, item.name, Constant.PAGE))
                }
                when {
                    listPermission.size >= 3 -> {
                        recyclerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.dpToPx(196))
                    }
                    listPermission.size == 3 -> {
                        recyclerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, SizeHelper.dpToPx(168))
                    }
                    else -> {
                        recyclerView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    }
                }
                adapter.setListData(listPermission)
            }

            override fun onError(error: ICResponseCode?) {
                if (listPermission.isNullOrEmpty()) {
                    adapter.setError(R.drawable.ic_error_request, requireContext().getString(R.string.co_loi_xay_ra_vui_long_thu_lai), R.string.thu_lai)
                } else {
                    adapter.setListData(listPermission)
                }
            }
        })

    }

    inner class PermissionAdapter() : RecyclerViewAdapter<ICCommentPermission>() {
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is PermissionHolder) {
                holder.bind(listData[position])
            }
        }

        override fun viewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return PermissionHolder(parent)
        }

        inner class PermissionHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_select_permission, parent, false)) {
            fun bind(obj: ICCommentPermission) {
                if (selectedPermission == null) {
                    selectedPermission = obj
                }

                itemView.tvName.text = obj.name

                itemView.tvName.setCompoundDrawablesWithIntrinsicBounds(0, 0, if (obj.id == selectedPermission!!.id) {
                    R.drawable.ic_checkbox_single_on_24px
                } else {
                    0
                }, 0)

                WidgetUtils.loadImageUrl(itemView.imgAvatar, obj.avatar, if (obj.type == Constant.USER) {
                    R.drawable.ic_avatar_default_84px
                } else {
                    R.drawable.ic_business_v2
                })

                itemView.setOnClickListener {
                    if (selectedPermission != obj) {
                        selectedPermission = obj
                        notifyDataSetChanged()
                    }
                }
            }
        }

    }

    interface PermissionListener {
        fun getPermission(permission: ICCommentPermission?)
    }
}