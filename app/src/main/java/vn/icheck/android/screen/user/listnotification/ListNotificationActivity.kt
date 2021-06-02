package vn.icheck.android.screen.user.listnotification

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_list_notification.layoutContainer
import kotlinx.android.synthetic.main.activity_list_notification.layoutLoading
import kotlinx.android.synthetic.main.activity_list_notification.recyclerView
import kotlinx.android.synthetic.main.activity_list_notification.swipeLayout
import kotlinx.android.synthetic.main.toolbar_light_blue.*
import vn.icheck.android.R
import vn.icheck.android.RelationshipManager
import vn.icheck.android.base.activity.BaseActivityMVVM
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.ICViewTypes
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.ichecklibs.util.showShortSuccessToast
import vn.icheck.android.loyalty.base.setVisible
import vn.icheck.android.network.models.ICNotification
import vn.icheck.android.screen.user.campaign.calback.IMessageListener
import vn.icheck.android.util.ick.*

/**
 * Phạm Hoàng Phi Hùng
 * 0974815770
 * hungphp@icheck.vn
 */

@AndroidEntryPoint
class ListNotificationActivity : BaseActivityMVVM(), IMessageListener {
    private lateinit var viewModel: ListNotificationViewModel

    private val adapter = ListNotificationAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_notification)

        setupToolbar()
        setupViewModel()
        setupRecyclerView()
        setupSwipeLayout()
    }

    private fun setupToolbar() {
        txtTitle.text = getString(R.string.thong_bao)

        imgBack.setOnClickListener {
            onBackPressed()
        }

        imgAction.setImageResource(R.drawable.ic_read_all_light_blue_24dp)
        imgAction.beVisible()

        imgAction.setOnClickListener {
            viewModel.markReadAll()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(ListNotificationViewModel::class.java)

        viewModel.onAddData.observe(this, Observer {
            closeLoading()
            if (it.data is ICNotification) {
                viewModel.arrNotify.add(it.data as ICNotification)
            }
            adapter.addItem(it)
        })

        viewModel.onUpdateData.observe(this, Observer {
            if (it.data is ICNotification) {
                viewModel.arrNotify.add(it.data as ICNotification)
            }
            if (it.viewType == ICViewTypes.INTERACTIVE_TYPE || it.viewType == ICViewTypes.PRODUCT_NOTICE_TYPE || it.viewType == ICViewTypes.OTHER_NOTIFICATION_TYPE) {
                if (it.data != null) {
                    imgAction.setVisible()
                }
            }
            adapter.updateItem(it)
        })

        viewModel.onError.observe(this, Observer {
            closeLoading()
            adapter.setError(it.icon, it.message.toString(), it.button)
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

        viewModel.onMarkAllSuccess.observe(this, Observer {
            adapter.markReadAll()
            showShortSuccessToast(it)
//            DialogHelper.showSpecialNotification(this@ListNotificationActivity, null, it, false)
        })

        viewModel.onShowErrorMessage.observe(this, Observer {
            showLongError(it)
        })
    }

    private fun closeLoading() {
        if (swipeLayout.isRefreshing) {
            swipeLayout.isRefreshing = false
            adapter.removeAllView()
        }

        if (layoutLoading != null) {
            layoutContainer.removeView(layoutLoading)
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this@ListNotificationActivity)
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && viewModel.updateNotify == 0 && viewModel.arrNotify.size < viewModel.totalNotify && viewModel.arrNotify.size > 0) {
                    viewModel.getPosts()
                    viewModel.currentOffsetPost += 10
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (RelationshipManager.unreadNotify > 0L) {
            imgAction.beVisible()
        } else {
            imgAction.beInvisible()
        }
    }

    private fun setupSwipeLayout() {
        val primaryColor = vn.icheck.android.ichecklibs.Constant.getPrimaryColor(this)
        swipeLayout.setColorSchemeColors(primaryColor, primaryColor, primaryColor)

        swipeLayout.setOnRefreshListener {
            swipeLayout.isRefreshing = true
            viewModel.getLayout()
        }

        swipeLayout.post {
            viewModel.getLayout()
        }
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)
        when (event.type) {
            ICMessageEvent.Type.UPDATE_UNREAD_NOTIFICATION -> {

                imgAction.visibility = if (event.data as Long > 0) {

                    View.INVISIBLE
                } else {
                    View.VISIBLE
                }
            }
        }
    }

    override fun onMessageClicked() {
        swipeLayout.isRefreshing = true
        viewModel.getLayout()
    }
}