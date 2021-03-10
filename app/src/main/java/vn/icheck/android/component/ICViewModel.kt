package vn.icheck.android.component

interface ICViewModel {
    /**
     * Method trả vè tag để thực hiện logic ẩn hiện vùng dữ liệu ở màn product detail
     */
    fun getTag():String

    /**
     * Method get viewtype
     */
    fun getViewType():Int
}