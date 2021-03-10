package vn.icheck.android.screen.user.bookmarkproduct

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_bookmark_product.*
import vn.icheck.android.R
import vn.icheck.android.base.fragment.BaseFragment
import vn.icheck.android.base.model.ICMessageEvent
import vn.icheck.android.component.shopvariant.history.IShopVariantListener
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.helper.PermissionHelper
import vn.icheck.android.network.models.ICHistory_Product
import vn.icheck.android.screen.user.bookmarkproduct.adapter.BookmarkProductAdapter
import vn.icheck.android.screen.user.bookmarkproduct.presenter.BookmarkProductPresenter
import vn.icheck.android.screen.user.bookmarkproduct.view.IBookmarkProductView

class BookmarkProductFragment : BaseFragment<BookmarkProductPresenter>(), IBookmarkProductView, IShopVariantListener {
    val adapter = BookmarkProductAdapter(this, this, "bookmarks/products/{id}")

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var lat: String? = null
    private var long: String? = null
    private val permissionLocation = 90
    private var isCreateView = false

    override val getLayoutID: Int
        get() = R.layout.fragment_bookmark_product
    override val getPresenter: BookmarkProductPresenter
        get() = BookmarkProductPresenter(this)

    override fun onInitView() {
        Handler().postDelayed({
            initSwipeLayout()
            initRecyclerView()
        }, 300)
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    private fun initSwipeLayout() {
        swipeLayout.setOnRefreshListener {
            adapter.disableLoadMore()
            swipeLayout.isRefreshing = true
            presenter.getListProductBookmark(false, lat, long)
        }

        swipeLayout.post {
            swipeLayout.isRefreshing = true
            presenter.getListProductBookmark(false, lat, long)
        }
    }

    private val checkAllowPermission: Boolean
        get() {
            return PermissionHelper.isAllowPermission(Manifest.permission.ACCESS_COARSE_LOCATION, context)
                    && PermissionHelper.isAllowPermission(Manifest.permission.ACCESS_FINE_LOCATION, context)
        }

    private fun checkPermission() {
        requestPermissions(
                arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ), permissionLocation
        )
    }

    private fun getLocation() {
        mFusedLocationClient?.lastLocation?.addOnSuccessListener { location: Location? ->
            if (location != null) {
                lat = location.latitude.toString()
                long = location.longitude.toString()

                presenter.getListProductBookmark(false, location.latitude.toString(), location.longitude.toString())
            } else {
                presenter.getListProductBookmark(false, null, null)
            }
        }?.addOnFailureListener {
            presenter.getListProductBookmark(false, null, null)
        }?.addOnCanceledListener {
            presenter.getListProductBookmark(false, null, null)
        }
    }

    override fun onGetListProductBookmarkError(error: String) {
        swipeLayout.isRefreshing = false

        if (adapter.isEmpty) {
            adapter.setError(error)
        } else {
            showLongError(error)
        }
    }

    override fun onGetListProductBookmarkSuccess(obj: MutableList<ICHistory_Product>, isLoadMore: Boolean) {
        swipeLayout.isRefreshing = false

        if (!isLoadMore) {
            adapter.setData(obj)
        } else {
            adapter.addData(obj)
        }
    }

    override fun onLoadMore() {
        presenter.getListProductBookmark(true, lat, long)
    }

    override fun onLayoutMessageClicked() {
        swipeLayout.isRefreshing = true
        presenter.getListProductBookmark(false, lat, long)
    }

    override fun onDeleteItemSuccess(adapterPosition: Int) {
        adapter.removeItem(adapterPosition)
    }

    override fun onResume() {
        super.onResume()
        if (!isCreateView) {
            mFusedLocationClient = context?.let { LocationServices.getFusedLocationProviderClient(it) }
            if (!checkAllowPermission) {
                checkPermission()
            } else {
                getLocation()
            }
            isCreateView = true
        } else {
            if (ICMessageEvent.BarcodeHistoryChanged && PermissionHelper.isAllowPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) && PermissionHelper.isAllowPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) && context?.let { NetworkHelper.isConnected(it) }!!) {
                getLocation()
            }
        }
        ICMessageEvent.BarcodeHistoryChanged = false
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionLocation) {
            if (PermissionHelper.checkResult(grantResults)) {
                getLocation()
            } else {
                getLocation()
            }
        }
    }
}