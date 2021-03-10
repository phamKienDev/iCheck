package vn.icheck.android.screen.user.icheckstore.list

import vn.icheck.android.network.models.ICStoreiCheck

class ListProductStoreModel (
    var listData: MutableList<ICStoreiCheck>,
    var isLoadMore: Boolean = false
)