package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICThemeSetting(
        @Expose val version: Int? = null,
        @Expose val theme: ICTheme? = null,
        @Expose val updatedAt: String? = null
) {

    data class ICTheme(
            @Expose val homeHeaderIconColor: String? = null,
            @Expose val homeHeaderImage: String? = null,
            @Expose val homeBackgroundImage: String? = null,
            @Expose val productOverlayImage: String? = null,
            @Expose val bottomBarSelectedTextColor: String? = null,
            @Expose val bottomBarSelectedIcons: List<String>? = null,
            @Expose val bottomBarNormalIcons: List<String>? = null,
            var homeBackgroundImagePath: String? = null,
            var homeHeaderImagePath: String? = null
    )
}