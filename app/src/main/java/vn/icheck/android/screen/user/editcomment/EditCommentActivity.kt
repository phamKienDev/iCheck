package vn.icheck.android.screen.user.editcomment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_edit_comment.*
import kotlinx.android.synthetic.main.toolbar_light_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.dialog.notify.callback.NotificationDialogListener
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.view.ViewHelper
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICCommentPost
import vn.icheck.android.network.models.ICProductQuestion
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible
import vn.icheck.android.util.kotlin.ActivityUtils
import vn.icheck.android.util.kotlin.WidgetUtils

/**
 * Created by VuLCL on 7/16/2020.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class EditCommentActivity : BaseActivityMVVM() {
    private lateinit var viewModel: EditCommentViewModel

    companion object {
        fun start(activity: FragmentActivity, obj: ICProductQuestion, barcode: String?, requestCode: Int) {
            val intent = Intent(activity, EditCommentActivity::class.java)
            intent.putExtra(Constant.DATA_1, obj)
            intent.putExtra(Constant.DATA_2, barcode)
            ActivityUtils.startActivityForResult(activity, intent, requestCode)
        }

        fun start(activity: FragmentActivity, obj: ICCommentPost, barcode: String?, requestCode: Int) {
            val intent = Intent(activity, EditCommentActivity::class.java)
            intent.putExtra(Constant.DATA_3, obj)
            intent.putExtra(Constant.DATA_2, barcode)
            ActivityUtils.startActivityForResult(activity, intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_comment)

        setupToolbar()
        setupViewModel()
        setupListener()
    }

    private fun setupToolbar() {
        layoutContainer.setPadding(0, getStatusBarHeight, 0, 0)

        txtTitle.setText(R.string.chinh_sua_binh_luan)
        txtTitle.typeface = ViewHelper.createTypeface(this, R.font.barlow_semi_bold)

        imgBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(EditCommentViewModel::class.java)

        viewModel.onSetProductImage.observe(this, {
//            WidgetUtils.loadImageUrl(imgProduct, it)
        })

        viewModel.onSetCommentQuestion.observe(this, {
            if (it.page != null) {
                WidgetUtils.loadImageUrl(layoutAvatar, it.page?.avatar, R.drawable.img_default_business_logo_big)
                imgLevel.beGone()
            } else {
                WidgetUtils.loadImageUrl(layoutAvatar, it.user?.avatar, R.drawable.ic_avatar_default_84px)
                imgLevel.setImageResource(Constant.getAvatarLevelIcon16(it.user?.rank?.level))
            }

            edtComment.setText(it.content)
            edtComment.setSelection(edtComment.text.toString().length)

            if (it.media.isNullOrEmpty()) {
                imageView.visibility = View.GONE
            } else {
                imageView.visibility = View.VISIBLE
                WidgetUtils.loadImageUrlRounded(imageView, it.media!![0].content, SizeHelper.size4)
            }
        })

        viewModel.onSetCommentPost.observe(this, {
            if (it.page != null) {
                WidgetUtils.loadImageUrl(layoutAvatar, it.page?.avatar, R.drawable.img_default_business_logo_big)
                imgLevel.beGone()
            } else {
                WidgetUtils.loadImageUrl(layoutAvatar, it.user?.avatar, R.drawable.ic_avatar_default_84px)
                imgLevel.setImageResource(Constant.getAvatarLevelIcon16(it.user?.rank?.level))
            }

            edtComment.setText(it.content)
            edtComment.setSelection(edtComment.text.toString().length)

            if (it.media.isNullOrEmpty()) {
                imageView.visibility = View.GONE
            } else {
                imageView.visibility = View.VISIBLE
                WidgetUtils.loadImageUrlRounded(imageView, it.media!![0]?.content, SizeHelper.size4)
            }
        })

        viewModel.onUpdateQuestion.observe(this, {
            val intent = Intent()
            intent.putExtra(Constant.DATA_1, it)
            setResult(Activity.RESULT_OK, intent)
            onBackPressed()
        })

        viewModel.onUpdatePost.observe(this, {
            val intent = Intent()
            intent.putExtra(Constant.DATA_1, it)
            setResult(Activity.RESULT_OK, intent)
            onBackPressed()
        })

        viewModel.onStatus.observe(this, Observer {
            when (it) {
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    DialogHelper.showLoading(this)
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    DialogHelper.closeLoading(this)
                }
            }
        })

        viewModel.onError.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                showLongError(it)
            } else {
                DialogHelper.showNotification(this@EditCommentActivity, R.string.co_loi_xay_ra_vui_long_thu_lai, false, object : NotificationDialogListener {
                    override fun onDone() {
                        onBackPressed()
                    }
                })
            }
        })

        viewModel.getData(intent)
    }

    private fun setupListener() {
        btnCancel.setOnClickListener {
            onBackPressed()
        }

        btnUpdate.setOnClickListener {
            viewModel.update(edtComment.text.toString().trim())
        }
    }
}