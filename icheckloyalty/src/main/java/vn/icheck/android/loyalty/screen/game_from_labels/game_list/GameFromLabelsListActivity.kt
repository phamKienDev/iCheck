package vn.icheck.android.loyalty.screen.game_from_labels.game_list

import android.content.Intent
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_game_from_labels_list.*
import kotlinx.android.synthetic.main.toolbar_blue.*
import vn.icheck.android.loyalty.R
import vn.icheck.android.loyalty.base.*
import vn.icheck.android.loyalty.base.activity.BaseActivityGame
import vn.icheck.android.loyalty.base.listener.IClickListener
import vn.icheck.android.loyalty.base.listener.IRecyclerViewCallback
import vn.icheck.android.loyalty.helper.SharedLoyaltyHelper
import vn.icheck.android.loyalty.model.ICKGame
import vn.icheck.android.loyalty.model.ListGameCampaign
import vn.icheck.android.loyalty.model.RowsItem
import vn.icheck.android.loyalty.screen.game_from_labels.redeem_points.onboarding.OnBoardingActivity
import vn.icheck.android.loyalty.screen.game_from_labels.vqmm.GameActivity
import vn.icheck.android.loyalty.screen.web.WebViewActivity
import vn.icheck.android.loyalty.sdk.CampaignType

class GameFromLabelsListActivity : BaseActivityGame(), IRecyclerViewCallback, IClickListener {

    private val adapter = GameFromLabelsListAdapter(this, this)

    private val viewModel by viewModels<GameFromLabelsListViewModel>()

    companion object {
        var name = ""
    }

    override val getLayoutID: Int
        get() = R.layout.activity_game_from_labels_list

    override fun onInitView() {
        initToolbar()
        initRecyclerView()
        initSwipeLayout()
        initListener()
    }

    private fun initToolbar() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        txtTitle.text = "Game từ nhãn hàng"
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    private fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            adapter.disableLoadMore()
            getData()
        }
        swipeLayout.post {
            getData()
        }
    }

    private fun getData() {
        swipeLayout.isRefreshing = true
        viewModel.getGameFromLabelsList()
    }

    private fun initListener() {
        viewModel.onError.observe(this, Observer {
            swipeLayout.isRefreshing = false

            if (adapter.isEmpty) {
                adapter.setError(it)
            } else {
                showLongError(it.title)
            }
        })

        viewModel.onErrorEmpty.observe(this, Observer {
            recyclerView.setGone()
            layoutNoGame.setVisible()

            layoutNoGame.setOnClickListener {
                getData()
            }
        })

        viewModel.onSetData.observe(this, Observer {
            recyclerView.setVisible()
            layoutNoGame.setGone()

            swipeLayout.isRefreshing = false
            adapter.setListData(it)
        })

        viewModel.onAddData.observe(this, Observer {
            swipeLayout.isRefreshing = false
            adapter.addListData(it)
        })
    }

    override fun onMessageClicked() {
        getData()
    }

    override fun onLoadMore() {
        viewModel.getGameFromLabelsList(true)
    }

    override fun onMessageEvent(event: ICMessageEvent) {
        super.onMessageEvent(event)

        when (event.type) {
            ICMessageEvent.Type.ON_UPDATE_POINT -> {
                getData()
            }
            ICMessageEvent.Type.UPDATE_COUNT_GAME -> {
                getData()
            }
        }
    }

    override fun onClick(obj: Any) {
        if (obj is ICKGame) {
            name = obj.name ?: ""
            when (obj.type) {
                CampaignType.ACCUMULATE_POINT -> {
                    if (obj.hasChanceCode != null) {
                        SharedLoyaltyHelper(this@GameFromLabelsListActivity).putBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_REDEEM_POINTS, obj.hasChanceCode)
                        OnBoardingActivity.startActivity(this, obj.id!!, obj.image?.medium, obj.description)
                    } else {
                        showLongError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }
                }
                CampaignType.RECEIVE_GIFT -> {
                    startActivity(Intent(this, WebViewActivity::class.java).apply {
                        putExtra(ConstantsLoyalty.DATA_1, obj.description ?: "")
                        putExtra(ConstantsLoyalty.DATA_3, "Thông tin chương trình")
                    })
                }
                CampaignType.MINI_GAME, CampaignType.MINI_GAME_QR_MAR -> {
                    if (obj.hasChanceCode != null) {
                        SharedLoyaltyHelper(this@GameFromLabelsListActivity).putBoolean(ConstantsLoyalty.HAS_CHANGE_CODE_VQMM, obj.hasChanceCode)

                        val item = obj.campaignGameUser?.firstOrNull()

                        val campaign = ListGameCampaign(header_image_rotation = obj.headerImageRotation, background_rotation = obj.backgroundRotation)

                        val rowItem = RowsItem(item?.play, item?.updatedAt, item?.icheckId, item?.timesPlay, item?.isSyncedAnonymousIcheck, item?.createdAt, campaign, item?.id, item?.deletedAt, item?.campaignId)

                        startActivity(Intent(this@GameFromLabelsListActivity, GameActivity::class.java).apply {
                            putExtra(ConstantsLoyalty.DATA_1, obj.id)
                            putExtra("owner", obj.owner?.name)
                            putExtra("banner", obj.image?.original)
                            putExtra("state", obj.statusTime)
                            putExtra("campaignName", obj.name)
                            putExtra("landing", obj.landingPage)
                            putExtra("titleButton", obj.titleButton)
                            putExtra("schema", obj.schemaButton)
                            putExtra("data", rowItem)
                        })

                    } else {
                        showLongError(getString(R.string.co_loi_xay_ra_vui_long_thu_lai))
                    }
                }
                else -> {
                    startActivity(Intent(this@GameFromLabelsListActivity, WebViewActivity::class.java).apply {
                        putExtra(ConstantsLoyalty.DATA_1, obj.description ?: "")
                        putExtra(ConstantsLoyalty.DATA_3, "Thông tin chương trình")
                    })
                }
            }
        }
    }
}