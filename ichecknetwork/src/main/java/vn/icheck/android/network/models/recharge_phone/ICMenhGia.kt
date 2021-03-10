package vn.icheck.android.network.models.recharge_phone

class ICMenhGia {
    var menhGia: String = ""
    var content: String = ""
    var select: Boolean = false

    constructor(menhGia: String) {
        this.menhGia = menhGia
        this.select = false
    }
}