package vn.icheck.android.callback

interface ItemClickSmallAnimationListener<T> {

    fun onItemClickSmall(position: Int, item: T? = null)
}