package vn.icheck.android.screen.user.mall

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_mall.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragment
import vn.icheck.android.component.view.ViewHelper.setScrollSpeed
import vn.icheck.android.constant.Constant
import vn.icheck.android.helper.DialogHelper
import vn.icheck.android.helper.SizeHelper
import vn.icheck.android.network.models.ICAds
import vn.icheck.android.network.models.ICCategory
import vn.icheck.android.network.util.JsonHelper
import vn.icheck.android.screen.user.campaign.calback.IBannerListener
import vn.icheck.android.screen.user.campaign.calback.IMessageListener
import vn.icheck.android.screen.user.home.HomeActivity
import vn.icheck.android.screen.user.mall.adapter.MallAdapter
import vn.icheck.android.screen.user.mall.adapter.MallCategoryHorizontalAdapter
import vn.icheck.android.screen.user.mall.adapter.MallCategoryVerticalAdapter
import vn.icheck.android.screen.user.mall.presenter.MallPresenter
import vn.icheck.android.screen.user.mall.view.IMallView
import vn.icheck.android.screen.user.search_home.main.SearchHomeActivity
import vn.icheck.android.util.AdsUtils
import vn.icheck.android.util.kotlin.WidgetUtils

/**
 * Created by VuLCL on 9/27/2019.
 * Phone: 0986495949
 * Email: vulcl@icheck.vn
 */
class MallFragment : BaseFragment<MallPresenter>(),
        IMallView,
        IBannerListener,
        IMessageListener,
        View.OnClickListener {
    private val categoryHorizontalAdapter = MallCategoryHorizontalAdapter()
    private val categoryVerticalAdapter = MallCategoryVerticalAdapter()
    private var mallAdapter = MallAdapter(this, this)

    private var isShowCategory = false
    private var isSetupView = false

    private val requestBannerSurvey = 1

    companion object {
        var nameCategory: String? = null
    }

    override val getLayoutID: Int
        get() = R.layout.fragment_mall

    override val getPresenter: MallPresenter
        get() = MallPresenter(this)

    override fun onInitView() {
        layoutContainer.setPadding(0, getStatusBarHeight + SizeHelper.size16, 0, 0)
    }

    private fun initCategory() {
        rvCategoryHorizontal.adapter = categoryHorizontalAdapter
        rvCategoryVertical.adapter = categoryVerticalAdapter
    }

    private fun initMallView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = mallAdapter
        recyclerView.setScrollSpeed()
    }

    private fun setupSwipeRefreshLayout() {
        swipeLayout.setColorSchemeColors(ContextCompat.getColor(requireContext(), R.color.blue), ContextCompat.getColor(requireContext(), R.color.blue), ContextCompat.getColor(requireContext(), R.color.lightBlue))

        swipeLayout.setOnRefreshListener {
            swipeLayout.isRefreshing = true
            presenter.checkInternet()
        }

        swipeLayout.post {
            presenter.checkInternet()
        }
    }

    private fun initListener() {
        WidgetUtils.setClickListener(this, txtMenu, txtSearch, imgHideOrShow, bgCategoryVertical)
    }

    private fun showOrHideCategory() {
        if (txtCategory.visibility == View.INVISIBLE) {
            val layoutParam = rvCategoryVertical.layoutParams
            layoutParam.height = (swipeLayout.height * 0.75).toInt()
            rvCategoryVertical.layoutParams = layoutParam

            txtCategory.visibility = View.VISIBLE
            bgCategoryVertical.visibility = View.VISIBLE

            showCategory(true)
        } else {
            if (isShowCategory) {
                hideCategory()
            } else {
                showCategory(false)
            }
        }

        isShowCategory = !isShowCategory
    }

    private fun showCategory(isFirst: Boolean) {
        WidgetUtils.changeViewHeight(txtCategory, if (isFirst) 0 else txtCategory.height, imgHideOrShow.height, 400)
        WidgetUtils.changeViewHeight(bgCategoryVertical, if (isFirst) 0 else bgCategoryVertical.height, swipeLayout.height, 400)
        imgHideOrShow.setImageResource(R.drawable.ic_arrow_drop_up_blue_24)
    }

    private fun hideCategory() {
        WidgetUtils.changeViewHeight(txtCategory, txtCategory.height, 1, 400)
        WidgetUtils.changeViewHeight(bgCategoryVertical, bgCategoryVertical.height, 1, 400)
        imgHideOrShow.setImageResource(R.drawable.ic_arrow_drop_down_blue_24)
    }

    private fun removeLoading() {
        if (layoutLoading != null) {
            layoutContainer.removeView(layoutLoading)
        }
    }

    override val getLifecycleScope: LifecycleCoroutineScope
        get() = lifecycleScope

    override fun showLoading() {
        DialogHelper.showLoading(this)
    }

    override fun closeLoading() {
        DialogHelper.closeLoading(this)
    }

    override fun onGetDataError(icon: Int, errorMessage: String) {
        removeLoading()
        swipeLayout.isRefreshing = false
        mallAdapter.setError(icon, errorMessage)
    }

    override fun onResetData() {
        mallAdapter.resetData()
    }

    override fun onGetListCategorySuccess(list: List<ICCategory>) {
        removeLoading()
        categoryHorizontalAdapter.setData(list)
        categoryVerticalAdapter.setData(list)
    }

    override fun onGetListBannerSuccess(obj: ICMall) {
        removeLoading()
        swipeLayout?.isRefreshing = false
        mallAdapter.addTopBanner(obj)
    }

    override fun onGetListBusinessSuccess(obj: ICMall) {
        removeLoading()
        swipeLayout.isRefreshing = false
        mallAdapter.addBusiness(obj)
    }

    override fun onGetListCampaign(list: MutableList<ICMall>) {
        removeLoading()
        swipeLayout.isRefreshing = false
        mallAdapter.addCampaign(list)
    }
    /**
     * End Mall View
     * */

    /*
    * Banner Survey
    * */
    override fun onBannerSurveyClicked(ads: ICAds) {
        AdsUtils.bannerSurveyClicked(this, requestBannerSurvey, ads)
    }
    /*
    * End Banner Survey
    * */

    /**
     * Message View
     * */
    override fun onMessageClicked() {
        swipeLayout.isRefreshing = true
        presenter.checkInternet()
    }
    /**
     * End Message View
     * */


    /**
     * View Click
     * */
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.imgHideOrShow -> {
                if (categoryVerticalAdapter.itemCount != 0) {
                    showOrHideCategory()
                }
            }
            R.id.bgCategoryVertical -> {
                showOrHideCategory()
            }
            R.id.txtMenu -> {
                activity?.let {
                    if (it is HomeActivity) {
                        it.openSlideMenu()
                    }
                }
            }
            R.id.txtSearch -> {
                startActivity<SearchHomeActivity>()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            requestBannerSurvey -> {
                if (resultCode == Activity.RESULT_CANCELED) {
                    JsonHelper.parseJson(data?.getStringExtra(Constant.DATA_1), ICAds::class.java)?.let {
                        mallAdapter.updateCollection(it)
                    }
                } else if (resultCode == Activity.RESULT_OK) {
                    val surveyID = data?.getLongExtra(Constant.DATA_1, -1)

                    if (surveyID != null && surveyID != -1L) {
                        mallAdapter.hideBannerSurvey(surveyID)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (!isSetupView) {
            initCategory()
            initMallView()
            setupSwipeRefreshLayout()
            initListener()
            isSetupView = true
        }
    }
}