package vn.icheck.android.screen.user.search_home.product

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.model.ICError
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.helper.NetworkHelper
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.*
import vn.icheck.android.network.feature.search.SearchInteractor
import vn.icheck.android.network.models.ICProductTrend

class SearchProductViewModel : BaseViewModel() {
    val interactor = SearchInteractor()

    private var isVerify = false
    private var price: String? = null
    private var listReviews = mutableListOf<String>()

    val getIsVerify: Boolean
        get() = isVerify

    val getPrice: String?
        get() = price

    val getReviews: MutableList<String>
        get() = listReviews

    fun setIsVerify(isVerify: Boolean) {
        this.isVerify = isVerify
    }

    fun setPrice(price: String?) {
        this.price = price
    }

    fun setReviews(reviews: MutableList<String>?) {
        listReviews.clear()
        listReviews.addAll(reviews ?: mutableListOf())
    }


    fun getData(key: String, offset: Int) = request {
        var ratings = ""
        if (!listReviews.isNullOrEmpty()) {
            ratings = listReviews.toString().substring(1, listReviews.toString().length - 1).replace(" sao", "").trim()
        }

        val order: String? = when (price) {
            getString(R.string.tu_gia_cao_nhat) -> {
                "-price"
            }
            getString(R.string.tu_gia_thap_nhat) -> {
                "price"
            }
            else -> {
                null
            }
        }

        val verified = if (isVerify) {
            1
        } else {
            0
        }

        interactor.getDataProduct(APIConstants.LIMIT, offset, key, verified, order, ratings)
    }
}