package vn.icheck.android.ichecklibs.event

data class ICMessageEvent(val type: Type, val data: Any? = null) {
    enum class Type {
        ON_UPDATE_POINT,
        GO_TO_HOME,
        NMDT,
        UPDATE_COUNT_GAME,
        SCAN_GAME,
        ON_COUNT_GIFT,
        EXCHANGE_PHONE_CARD,
        OPEN_DETAIL_GIFT,
        BACK,
        QR_SCANNED,
        SCAN_FAILED,
        ON_REQUIRE_LOGIN
    }
}