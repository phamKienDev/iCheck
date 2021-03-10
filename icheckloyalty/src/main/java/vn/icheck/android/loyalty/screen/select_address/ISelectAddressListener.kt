package vn.icheck.android.loyalty.screen.select_address

interface ISelectAddressListener<T> {
    fun onMessageClicked()
    fun onItemClicked(item: T)
}