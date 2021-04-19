package vn.icheck.android.network.base

object ICNetworkManager {
    private val callbacks = mutableListOf<ICNetworkCallback>()
    private var loginProtocol: ICLoginProtocol? = null
    private var pvCombankListener = mutableListOf<PVComBankListener>()

    /**
     * Đăng ký sự kiện lắng nghe khi server yêu cầu đăng nhập
     *
     * @param requireLoginCallback
     */
    fun register(listener: ICNetworkCallback) {
        callbacks.add(listener)
    }

    fun registerPVCombank(listener: PVComBankListener) {
        pvCombankListener.add(listener)
    }

    fun registerLogin(loginProtocol: ICLoginProtocol) {
        this.loginProtocol = loginProtocol
    }

    /**
     * Hủy đăng ký sự kiện lắng nghe khi server yêu cầu đăng nhập
     *
     */
    fun unregister(listener: ICNetworkCallback) {
        callbacks.remove(listener)
    }

    fun unregisterPVCombank(listener: PVComBankListener) {
        pvCombankListener.remove(listener)
    }

    fun unregisterLogin(loginProtocol: ICLoginProtocol) {
        this.loginProtocol = loginProtocol
    }

    /**
     * Gọi sự kiện server yêu cầu đăng ký
     *
     */
    fun onEndOfToken() {
        for (callback in callbacks) {
            callback.onEndOfToken()
        }
    }

    fun onEndOfPVCombankToken() {
        for (callback in pvCombankListener) {
            callback.onEndOfToken()
        }
    }

    val getLoginProtocol: ICLoginProtocol?
        get() {
            return loginProtocol
        }

    interface PVComBankListener {
        fun onEndOfToken()
    }
}