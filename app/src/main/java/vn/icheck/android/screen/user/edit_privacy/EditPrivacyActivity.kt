package vn.icheck.android.screen.user.edit_privacy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_edit_privacy.*
import kotlinx.android.synthetic.main.dialog_select_post_privacy_item.view.*
import kotlinx.android.synthetic.main.item_message.*
import kotlinx.android.synthetic.main.toolbar_light_blue.*
import vn.icheck.android.R
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.util.ick.beGone
import vn.icheck.android.util.ick.beVisible

class EditPrivacyActivity : BaseActivityMVVM() {
    lateinit var viewModel: EditPrivacyViewModel

    private var selectedPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_privacy)

        initView()
        setupViewModel()
        listenerData()
    }

    private fun initView() {
        imgBack.setImageResource(R.drawable.ic_cancel_light_blue_24dp)
        imgBack.setOnClickListener {
            onBackPressed()
        }

        txtTitle.text = getString(R.string.chinh_sua_quyen_rieng_tu)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(EditPrivacyViewModel::class.java)

        viewModel.onStatus.observe(this, Observer {
            when (it) {
                ICMessageEvent.Type.ON_SHOW_LOADING -> {
                    layoutLoading.beVisible()
                }
                ICMessageEvent.Type.ON_CLOSE_LOADING -> {
                    layoutLoading.beGone()
                }
                else -> {
                }
            }
        })

        viewModel.onSetPrivacy.observe(this, Observer {
            layoutMessage.beGone()
            layoutLoading.beGone()

            layoutContent.apply {
                for (i in 0 until it.size) {
                    val parent = LayoutInflater.from(context).inflate(R.layout.dialog_select_post_privacy_item, this, false) as ViewGroup

                    if (it[i].selected) {
                        selectedPosition = i
                        parent.imgPublic.setImageResource(R.drawable.ic_radio_on_24dp)
                    } else {
                        parent.imgPublic.setImageResource(R.drawable.ic_radio_un_checked_gray_24dp)
                    }
                    parent.tvPublicName.text = it[i].privacyElementName
                    parent.tvPublicContent.text = it[i].privacyElementDescription

                    parent.setOnClickListener {
                        unSelected(selectedPosition)
                        selectedPosition = i
                        selected(selectedPosition)
                    }

                    addView(parent)
                }
            }
        })

        viewModel.onEditSuccess.observe(this, Observer {
            val result = Intent()
            result.putExtra(Constant.DATA_1, it)
            setResult(Activity.RESULT_OK, intent)
            DialogHelper.showDialogSuccessBlack(this, getString(R.string.ban_da_thay_doi_quyen_rieng_tu))
            Handler().postDelayed({
                finish()
            }, 1800)
        })

        viewModel.onGetDataError.observe(this, Observer {
            layoutMessage.beVisible()
            layoutLoading.beGone()

            imgIcon.setImageResource(it.icon)
            txtMessage.text = it.message
        })

        viewModel.onRequestError.observe(this, Observer {
            DialogHelper.showNotification(this@EditPrivacyActivity, it)
        })

        viewModel.onShowMessage.observe(this, Observer {
            showLongError(it)
        })

        viewModel.getData(intent)
    }

    private fun unSelected(position: Int) {
        (layoutContent.getChildAt(position) as ViewGroup).imgPublic.apply {
            setImageResource(R.drawable.ic_radio_un_checked_gray_24dp)
        }
    }

    private fun selected(position: Int) {
        (layoutContent.getChildAt(position) as ViewGroup).imgPublic.apply {
            setImageResource(R.drawable.ic_radio_on_24dp)
        }
    }

    private fun listenerData() {
        btnTryAgain.setOnClickListener {
            viewModel.getPrivacy()
        }

        tvFinish.setOnClickListener {
            viewModel.editPrivacy(selectedPosition)
        }
    }
}