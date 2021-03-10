package vn.icheck.android.network.base

object ICNetworkManager2 {
    private var loginProtocol: ICLoginProtocol? = null
    private var pvCombankListener: PVComBankListener? = null

    /**
     * Đăng ký sự kiện lắng nghe khi server yêu cầu đăng nhập
     *
     * @param requireLoginCallback
     */
    fun registerPVCombank(listener: PVComBankListener) {
        pvCombankListener = listener
    }

    fun registerLogin(loginProtocol: ICLoginProtocol) {
        this.loginProtocol = loginProtocol
    }

    /**
     * Hủy đăng ký sự kiện lắng nghe khi server yêu cầu đăng nhập
     *
     */

    /**
     * Gọi sự kiện server yêu cầu đăng ký
     *
     */
    fun onEndOfPVCombankToken() {
        pvCombankListener?.onEndOfToken()
    }

    val getLoginProtocol: ICLoginProtocol?
        get() {
            return loginProtocol
        }

    interface PVComBankListener {
        fun onEndOfToken()
    }
}