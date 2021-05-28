package vn.icheck.android.screen.user.share_post_of_page

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_share_post_with_wall.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.helper.TimeHelper
import vn.icheck.android.ichecklibs.ViewHelper
import vn.icheck.android.util.kotlin.WidgetUtils

class SharePostActivity : BaseActivityMVVM() {

    private lateinit var viewmodel: SharePostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_post_with_wall)
        viewmodel = ViewModelProvider(this).get(SharePostViewModel::class.java)
        viewmodel.getDataIntent(intent)

        linearLayout.background=ViewHelper.bgTransparentStrokeLineColor1(this)
        layoutBottom.background=ViewHelper.bgWhiteRadiusTop25(this)

        initView()
        listenerGetData()
        listener()
    }

    private fun initView() {
        txtTitle.text = "Chia sẻ trang cá nhân"
    }

    private fun listenerGetData() {
        viewmodel.post.observe(this, Observer {
            avatarUserPost.setData(it.page?.avatar, -1, R.drawable.ic_business_v2)
            tvNameUserPost.text = it.page?.name

            when (it.targetType) {
                "page" -> {
                }
                "product" -> {
                    when (it.involveType) {
                        "question" -> {
                            tvTime.visibility = View.VISIBLE
                            tvTime.text = TimeHelper.convertDateTimeSvToCurrentDate(it.createdAt)
                        }
                    }
                }
            }

            tvDesc.text = it.content

            if (it.attachment.isNullOrEmpty()) {
                layoutImageVideo.visibility = View.GONE
            } else {
                layoutImageVideo.visibility = View.VISIBLE
                WidgetUtils.loadImageUrl(imageVideo, it.attachment!![0].original)
            }
            if (it.meta?.product != null) {
                productInFeed.setData(it.meta)
            } else {
                layoutProductBottom.visibility = View.GONE
            }
        })

        viewmodel.user.observe(this, Observer {
            avatarUser.setData(it.avatar, it.level, R.drawable.ic_avatar_default_84px)
            tvName.text = it.getName
        })

        viewmodel.page.observe(this, Observer {
            avatarUser.setData(it.avatar, -1, R.drawable.ic_business_v2)
            tvName.text = it.name
            tvSubName.visibility = View.GONE
        })
    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        btnShare.setOnClickListener {

        }
    }
}