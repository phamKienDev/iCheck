package vn.icheck.android.network.base

import retrofit2.HttpException
import vn.icheck.android.network.util.JsonHelper

object ResultHelper {

    fun checkRequestError(throws: Throwable): ICResponseCode? {
        if (throws is HttpException) {
            val error = JsonHelper.parseJson(throws.response()?.errorBody()?.string()?.trim(), ICResponseCode::class.java)

            if (error != null) {
                return error
            }
        }

        return ICResponseCode()
    }

//    fun getError(code: Int?, message: String?): ICResponseCode {
//        val error = ICResponseCode()
//        if (code == APIConstants.INVALID_TOKEN_CODE || message == APIConstants.INVALID_TOKEN_MESSAGE) {
//            error.message = APIConstants.messageNotPermission
//        } else {
//            error.message = APIConstants.messageCanNotLoadData
//        }
//        return error
//    }
//
//    fun getError(error: ICResponseCode): ICResponseCode {
//        val responseError = ICResponseCode()
//        if (error.status == APIConstants.INVALID_TOKEN_CODE || error.message == APIConstants.INVALID_TOKEN_MESSAGE || error.error == APIConstants.INVALID_TOKEN_MESSAGE) {
//            responseError.message = APIConstants.messageNotPermission
//        } else {
//            error.message = APIConstants.messageCanNotLoadData
//        }
//        return responseError
//    }
//
//    fun checkError(error: ICResponseCode) {
//        if (error.status == APIConstants.INVALID_TOKEN_CODE || error.error == APIConstants.INVALID_TOKEN_MESSAGE || error.message == APIConstants.INVALID_TOKEN_MESSAGE) {
//            error.message = APIConstants.messageNotPermission
//        } else if (error.message.isNullOrEmpty()) {
//            error.message = APIConstants.messageCanNotLoadData
//        }
//    }
//
//    fun checkError(error: ICResponseCodeICheck) {
//        if (error.statusCode == APIConstants.INVALID_TOKEN_CODE || error.error == APIConstants.INVALID_TOKEN_MESSAGE || error.message == APIConstants.INVALID_TOKEN_MESSAGE) {
//            error.message = APIConstants.messageNotPermission
//        } else if (error.message.isNullOrEmpty()) {
//            error.message = APIConstants.messageCanNotLoadData
//        }
//    }
}