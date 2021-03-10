package vn.icheck.android.callback

interface ItemClickListener<T> {

    fun onItemClick(position: Int, item: T? = null)
}