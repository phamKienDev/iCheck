package vn.icheck.android.base.model

data class ICMessageEvent(val type: Type, val data: Any? = null) {
    companion object {
        var BarcodeHistoryChanged = false
    }

    enum class Type {
        BACK,
        SELECT_DISTRICT,
        SELECT_CITY,
        REFRESH_DATA,
        MESSAGE_ERROR,
        ERROR_EMPTY,
        ERROR_SERVER,
        ON_LOG_IN,
        ON_LOG_OUT,
        UPDATE_UNREAD_NOTIFICATION,
        UPDATE_COUNT_CART,
        GO_TO_HOME,
        UPDATE_COIN_AND_RANK,
        ON_LOG_IN_FIREBASE,
        REFRESH_DATA_HISTORY_BARCODE,
        REFRESH_DATA_GIFT_STORE,
        NEW_MESSAGE,
        ON_REQUIRE_LOGIN,
        ON_LOGIN_SUCCESS,
        ON_NO_INTERNET,
        ON_SHOW_LOADING,
        ON_CLOSE_LOADING,
        ON_REGISTER_FACEBOOK_PHONE,
        HIDE_OR_SHOW_FOLLOW,
        FOLLOW_PAGE,
        UNFOLLOW_PAGE,
        UPDATE_FOLLOW_USER,
        UPDATE_SUBCRIBE_STATUS,
        SHOW_BOTTOM_SHEET_REPORT,
        ON_UPDATE_PAGE_NAME,
        ON_UPDATE_AUTO_PLAY_VIDEO,
        REQUEST_VOTE_CONTRIBUTION,
        REQUEST_POST_REVIEW,
        ON_EDIT_POST,
        ON_CREATE_POST,
        RESULT_EDIT_POST,
        RESULT_DETAIL_POST_ACTIVITY,
        RESULT_COMMENT_POST_ACTIVITY,
        RESULT_MEDIA_POST_ACTIVITY,
        RESULT_CREATE_POST,
        OPEN_SEARCH_PRODUCT,
        OPEN_SEARCH_USER,
        OPEN_SEARCH_REVIEW,
        OPEN_DETAIL_POST,
        OPEN_DETAIL_USER,
        OPEN_DETAIL_PAGE,
        OPEN_LIST_QUESTIONS,
        OPEN_LIST_REVEWS,
        ON_UPDATE_BOOKMARK,
        ADD_OR_REMOVE_BOOKMARK,
        TAKE_IMAGE_DIALOG,
        PIN_POST,
        UN_PIN_POST,
        SKIP_INVITE_FOLLOW_PAGE,
        FINISH_ALL_PVCOMBANK,
        FINISH_CREATE_PVCOMBANK,
        UPDATE_STATUS_ORDER_HISTORY,
        ONCLICK_PAGE_OVERVIEW,
        ONCLICK_LISTPOST_OF_PAGE,
        CLICK_LISTPOST_OF_PAGE,
        CLICK_PRODUCT_OF_PAGE,
        OPEN_MEDIA_IN_POST,
        DISMISS_DIALOG,
        SHOW_FULL_MEDIA,
        SHOW_DIALOG_MY_FOLLOW_PAGE,
        HIDE_CONTAINER_FOLLOW_PAGE,
        SHOW_OR_HIDE_PV_FULLCARD,
        CLICK_START_REVIEW,
        OPEN_PRODUCT_DETAIL,
        OPEN_LIST_CONTRIBUTION,
        REFRESH_DATA_HISTORY_SCAN,
        OPEN_DETAIL_MISSION,
        DELETE_DETAIL_POST,
        UNREAD_COUNT,
        FRIEND_LIST_UPDATE,
        UPDATE_PRICE,
        ON_SHOW_LOGIN,
        ON_CHANGE_FOLLOW,
        INIT_MENU_HISTORY,
        CLOSE_MENU_HISTORY,
        ON_TICK_HISTORY,
        ON_UNTICK_HISTORY,
        SCROLL_DETAIL_MEDIA,
        BACK_TO_SHAKE,
        UPDATE_REMINDER,
        IS_SCROLL_MEDIA,
        DISMISS_REMINDER,
        ON_SET_THEME,
        UPDATE_CONVERSATION,
        DO_REMINDER,
        ON_CHECK_UPDATE_LOCATION,
        SOCKET_TIMEOUT,
        UNFRIEND,
        ON_KYC_SUCCESS,
        ON_DESTROY_PVCOMBANK,
        ON_DISMISS,
        TAKE_IMAGE,
        OPEN_SEARCH_REVIEW_OR_PAGE,
        REQUEST_MISSION_SUCCESS,
        REFRESH_HOME_FRAGMENT
    }
}