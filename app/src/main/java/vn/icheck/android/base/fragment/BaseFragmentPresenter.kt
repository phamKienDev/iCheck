package vn.icheck.android.base.fragment

open class BaseFragmentPresenter(private val baseView: BaseFragmentView) {

    fun showError(errorMessage: String) {
        baseView.showError(errorMessage)
    }

    fun showError(errorMessageID: Int) {
        baseView.mContext?.let {
            baseView.showError(it.getString(errorMessageID))
        }
    }

    fun getString(resId: Int): String {
        baseView.mContext?.let {
            return it.getString(resId)
        }

        return ""
    }
}