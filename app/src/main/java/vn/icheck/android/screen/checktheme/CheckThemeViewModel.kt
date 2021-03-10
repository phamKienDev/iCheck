package vn.icheck.android.screen.checktheme

import vn.icheck.android.BuildConfig
import vn.icheck.android.base.viewmodel.BaseViewModel
import vn.icheck.android.network.feature.auth.AuthInteractor
import vn.icheck.android.network.feature.page.PageRepository
import vn.icheck.android.network.feature.setting.SettingRepository

class CheckThemeViewModel: BaseViewModel() {
    private val settingRepository = SettingRepository()
    private val authInteraction = AuthInteractor()
    private val pageRepository = PageRepository()

    var appInitScheme = ""

    fun loginAnonymous() = request(5000L) { authInteraction.loginAnonymousV2() }

    suspend fun getThemeSetting() = settingRepository.getThemeSetting()

    suspend fun getClientSetting(keyGroup: String, key: String?) = settingRepository.getClientSetting(keyGroup, key)

    suspend fun getRelationshipInformation() = pageRepository.getRelationshipInformation()

    suspend fun getConfigUpdateApp() = settingRepository.getConfigUpdateApp(BuildConfig.VERSION_NAME)
}