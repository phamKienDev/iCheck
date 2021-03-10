package vn.icheck.android.screen.user.share_post_of_page.bottom_sheet_chat_share

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_via_chat_share.*
import vn.icheck.android.R
import vn.icheck.android.base.dialog.notify.base.BaseBottomSheetDialogFragment
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.network.models.ICPost
import vn.icheck.android.util.kotlin.WidgetUtils

class ShareViaChat(val obj: ICPost) : BaseBottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_via_chat_share, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        listener()
    }

    private fun initView(){
        avatarUserPost.setData(obj.page?.avatar, -1, R.drawable.img_default_business_logo)
        tvName.text = obj.page?.name
        tvDesc.text = obj.content

        when (obj.targetType) {
            "page" -> {
            }
            "product" -> {
                when (obj.involveType) {
                    "question" -> {
                        tvTime.visibility = View.VISIBLE
                        tvTime.text = TimeHelper.convertDateTimeSvToCurrentDate(obj.createdAt)
                    }
                }
            }
        }

        if (obj.attachment.isNullOrEmpty()) {
            imageVideo.visibility = View.GONE
        } else {
            imageVideo.visibility = View.VISIBLE
            WidgetUtils.loadImageUrl(imageVideo, obj.attachment!![0].original)
        }
    }

    private fun listener() {
        imgCancel.setOnClickListener {
            dialog?.dismiss()
        }
    }
}