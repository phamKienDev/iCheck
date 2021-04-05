package vn.icheck.android.screen.user.pvcombank.listcard.callbacks

import vn.icheck.android.network.models.pvcombank.ICListCardPVBank

interface CardPVComBankListener {
    fun onClickUseDefaulCard(item: ICListCardPVBank, position: Int)
    fun onClickLockCard(item: ICListCardPVBank, position: Int)
    fun onClickUnLockCard(item: ICListCardPVBank, position: Int)
    fun onClickChangePassword(item: ICListCardPVBank, position: Int)
    fun onClickShowOrHide(item: ICListCardPVBank, position: Int)
    fun onClickShow(item: ICListCardPVBank, position: Int)
    fun onAuthenCard(item: ICListCardPVBank)
    fun onRefresh()
}