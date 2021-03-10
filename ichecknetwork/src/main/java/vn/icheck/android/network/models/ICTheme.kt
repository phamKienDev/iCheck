package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICTheme(
        @Expose val id: Long?,
        @Expose val title: String?,
        @Expose val color: Color?,
        @Expose val float_button: ICThemeFunction?,
        @Expose val break_reward: BreakReward?,
        @Expose val primary_functions: MutableList<ICThemeFunction>?,
        @Expose val secondary_functions: MutableList<ICThemeFunction>?
) {

    data class Color(
            @Expose val primary: String?,
            @Expose val primary_dark: String?,
            @Expose val secondary: String?,
            @Expose val secondary_light: String?,
            @Expose val text_primary: String?,
            @Expose val text_holder: String?,
            @Expose val input_border: String?,
            @Expose val error: String?,
            @Expose val icon_color: String?,
            @Expose val icon_color_secondary: String?,
            @Expose val background_color: String?,
            @Expose val button_text_color: String?,
            @Expose val button_background_color: String?
//            @Expose val button_background_pressed_color: String?,
//            @Expose val button_background_stroke_color: String?,
//            @Expose val button_background_stroke_pressed_color: String?
    )

    data class BreakReward (
            @Expose val theme_id:Int?,
            @Expose val actions:MutableList<Action>?
    ){
        data class Action(
                @Expose val label: String?,
                @Expose val width: Int?,
                @Expose val height: Int?,
                @Expose val scheme: String?,
                @Expose val source: String?,
                @Expose val label_color: String?,
                @Expose val required_login: Boolean?
        )
    }

}