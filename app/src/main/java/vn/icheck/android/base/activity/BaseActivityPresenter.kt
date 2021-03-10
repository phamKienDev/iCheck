package vn.icheck.android.base.activity

open class BaseActivityPresenter(private val baseView: BaseActivityView) {

    fun showError(errorMessage: String) {
        baseView.showError(errorMessage)
    }

    fun showError(errorMessageID: Int) {
        baseView.showError(baseView.mContext.getString(errorMessageID))
    }

    fun getString(resId: Int): String {
        return baseView.mContext.getString(resId)
    }
}