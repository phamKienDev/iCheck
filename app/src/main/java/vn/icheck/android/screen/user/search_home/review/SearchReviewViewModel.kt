package vn.icheck.android.screen.user.search_home.review

import vn.icheck.android.ICheckApplication
import vn.icheck.android.R
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.network.base.APIConstants
import vn.icheck.android.network.feature.search.SearchInteractor

class SearchReviewViewModel : BaseViewModel() {
    val interactor = SearchInteractor()

    private var isWatched: Boolean = false
    private var reviewYear = mutableListOf<String>()
    private var reviewFrom = mutableListOf<String>()

    fun setWatched(isWatched: Boolean) {
        this.isWatched = isWatched
    }

    val getIsWatched: Boolean
        get() = isWatched

    fun setTime(time: MutableList<String>?) {
        reviewYear.clear()
        reviewYear.addAll(time ?: mutableListOf())
    }

    val getReviewYear: MutableList<String>
        get() = reviewYear

    fun setFrom(from: MutableList<String>?) {
        reviewFrom.clear()
        reviewFrom.addAll(from ?: mutableListOf())
    }

    val getReviewFrom: MutableList<String>
        get() = reviewFrom


    fun getData(key: String, offset: Int) = request {
        val years = if (!reviewYear.isNullOrEmpty()) {
            reviewYear.toString().substring(1, reviewYear.toString().length - 1)
        } else {
            ""
        }

        val froms = mutableListOf<String>()
        if (!reviewFrom.isNullOrEmpty()) {
            for (i in reviewFrom) {
                when (i) {
                    getString(R.string.chinh_ban) -> {
                        froms.add("me")
                    }
                    getString(R.string.trang_cua_ban) -> {
                        froms.add("myPages")
                    }
                    else -> {
                        froms.add("myFriends")
                    }
                }
            }
        }

        val from = if (!froms.isNullOrEmpty()) {
            froms.toString().substring(1, froms.toString().length - 1)
        } else {
            ""
        }

        interactor.getDataReview(APIConstants.LIMIT, offset, key, isWatched, years, from)
    }
}