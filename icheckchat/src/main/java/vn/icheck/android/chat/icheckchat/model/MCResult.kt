package vn.icheck.android.chat.icheckchat.model

data class MCResult<out T>(val status: MCStatus, val data: T? = null, val message: String? = null) {

    companion object {
        fun <T> errorNetwork(data: T?, message: String?): MCResult<T> = MCResult(status = MCStatus.ERROR_NETWORK, data = data, message = message)
        fun <T> errorRequest(data: T?, message: String?): MCResult<T> = MCResult(status = MCStatus.ERROR_REQUEST, data = data, message = message)
        fun <T> loading(): MCResult<T> = MCResult(status = MCStatus.LOADING)
        fun <T> success(data: T): MCResult<T> = MCResult(status = MCStatus.SUCCESS, data = data)
    }
}