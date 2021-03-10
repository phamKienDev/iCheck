package vn.icheck.android.network.base

interface ICRequireLogin {

    fun onRequireLogin(requestCode: Int = 0)
    fun onRequireLoginSuccess(requestCode: Int = 0)
    fun onRequireLoginCancel()
}