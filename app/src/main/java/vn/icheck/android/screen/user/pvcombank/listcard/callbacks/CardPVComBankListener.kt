package vn.icheck.android.screen.user.pvcombank.listcard.callbacks

import vn.icheck.android.network.models.pvcombank.ICListCardPVBank

interface CardPVComBankListener {
    fun onClickUseDefaulCard(item: ICListCardPVBank, position: Int)
    fun onClickLockCard(item: ICListCardPVBank, position: Int)
    fun onClickUnLockCard(item: ICListCardPVBank, position: Int)
    fun onClickChangePassword(item: ICListCardPVBank, position: Int)
    fun onClickSecuriryCard(item: ICListCardPVBank, position: Int)
    fun onClickShowHide(item: ICListCardPVBank, position: Int)
    fun onRefresh()
}