package vn.icheck.android.network.base

data class Result<out T>(val status: Status, val data: T? = null, val message: String? = null) {

    companion object {
        fun <T> loading(): Result<T> = Result(status = Status.LOADING)
        fun <T> stopLoading(): Result<T> = Result(status = Status.LOADING, data = null, message = "stop")
        fun <T> errorNetwork(data: T?, message: String?): Result<T> = Result(status = Status.ERROR_NETWORK, data = data, message = message)
        fun <T> errorRequest(data: T?, message: String?): Result<T> = Result(status = Status.ERROR_REQUEST, data = data, message = message)
        fun <T> success(data: T): Result<T> = Result(status = Status.SUCCESS, data = data)
    }
}