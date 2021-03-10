package vn.icheck.android.screen.user.detail_stamp_hoa_phat.home.call_back

import vn.icheck.android.network.models.ICCriteria

interface SlideHeaderStampHoaPhatListener {
    fun itemPagerClickToVideo(urlVideo: String?)
    fun itemPagerClick(list: ArrayList<String?>, position: Int)
    fun editReview(objCriteria: ICCriteria)
    fun viewAllQa()
    fun onClickRalatedProduct(barcode: String)
    fun showAllInformationProduct(title: String, content: String)
    fun showAllBottomReviews()
    fun dial(phone: String?)
    fun email(email: String?)
    fun web(website: String?)
    fun showPage(id: Long)
    fun scrollWithViewType(type: Int)
    fun itemPagerClickToImage(url: String, position: Int)
}