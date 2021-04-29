package vn.icheck.android.network.model

import retrofit2.Response

/**
 * Common class used by API responses.
 * @param <T> the type of the response object
</T> */
@Suppress("unused") // T is used in extending classes
open class ApiResponse<T> {
    companion object {
        fun <T> create(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(error)
        }

        fun <T> create(response: T): ApiResponse<T> {
            return ApiSuccessResponse(response)
        }
    }
}


data class ApiSuccessResponse<T>(
        val body: T
) : ApiResponse<T>()

data class ApiErrorResponse<T>(val error: Throwable) : ApiResponse<T>()
