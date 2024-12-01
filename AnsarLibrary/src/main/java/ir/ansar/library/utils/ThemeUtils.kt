package ir.ansar.library.utils

import android.util.TypedValue
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import ir.ansar.library.R

object ThemeUtils {

    var themeName: MutableLiveData<Int> = MutableLiveData(0)

    var isDark: Boolean = true
    var isBlue: Boolean = true

    var colorPrimary: MutableLiveData<Int> = MutableLiveData()
    var colorSurface:MutableLiveData<Int> = MutableLiveData()
    var colorSurfaceContainer: MutableLiveData<Int> = MutableLiveData()
    var colorSurfaceContainer2: MutableLiveData<Int> = MutableLiveData()
    var colorOnSurface: MutableLiveData<Int> = MutableLiveData()
    var colorOnSurfaceVariant: MutableLiveData<Int> = MutableLiveData()
    var colorSurfaceDivider: MutableLiveData<Int> = MutableLiveData()
    var colorContainerDivider: MutableLiveData<Int> = MutableLiveData()
    var colorInactiveContainerDivider: MutableLiveData<Int> = MutableLiveData()
    var colorSurfaceDialog: MutableLiveData<Int> = MutableLiveData()
    var colorInactivePlan: MutableLiveData<Int> = MutableLiveData()

    fun loadTheme(activity: AppCompatActivity) {

        themeName.value = SettingsUtils.settings
            .getInt("theme_name", R.style.Pink_BlueDarkTheme_MyPlan)

        isBlue = SettingsUtils.settings
            .getBoolean("is_blue", true)

        update(activity)
    }

    fun setTheme(@StyleRes res: Int) {

        SettingsUtils.settings
            .edit()
            .putInt("theme_name", res)
            .apply()

        if (isDark) {
            isBlue = when (themeName.value) {
                R.style.Green_BlueDarkTheme_MyPlan -> true
                R.style.Pink_BlueDarkTheme_MyPlan -> true
                R.style.Blue_BlueDarkTheme_MyPlan -> true
                else -> false
            }

            SettingsUtils.settings
                .edit()
                .putBoolean(
                    "is_blue",
                    isBlue
                )
                .apply()

        }

        themeName.value = res
    }

    private fun update(activity: AppCompatActivity) {

        themeName.observe(activity){ theme ->

            activity.theme.applyStyle(theme, true)

            val typedValue = TypedValue()
            activity.theme.resolveAttribute(
                androidx.appcompat.R.attr.colorPrimary,
                typedValue,
                true
            )
            colorPrimary.value = typedValue.data

            activity.theme.resolveAttribute(
                R.attr.colorSurface,
                typedValue,
                true
            )
            colorSurface.value = typedValue.data
            isDark = colorSurface.value != activity.getColor(R.color.color_surface_light)

            activity.theme.resolveAttribute(
                R.attr.colorSurfaceContainer,
                typedValue,
                true
            )
            colorSurfaceContainer.value = typedValue.data

            activity.theme.resolveAttribute(
                R.attr.colorSurfaceContainer2,
                typedValue,
                true
            )
            colorSurfaceContainer2.value = typedValue.data

            activity.theme.resolveAttribute(
                R.attr.colorOnSurface,
                typedValue,
                true
            )
            colorOnSurface.value = typedValue.data

            activity.theme.resolveAttribute(
                R.attr.colorOnSurfaceVariant,
                typedValue,
                true
            )
            colorOnSurfaceVariant.value = typedValue.data

            activity.theme.resolveAttribute(
                R.attr.colorSurfaceDivider,
                typedValue,
                true
            )
            colorSurfaceDivider.value = typedValue.data

            activity.theme.resolveAttribute(
                R.attr.colorContainerDivider,
                typedValue,
                true
            )
            colorContainerDivider.value = typedValue.data

            activity.theme.resolveAttribute(
                R.attr.colorInactiveContainerDivider,
                typedValue,
                true
            )
            colorInactiveContainerDivider.value = typedValue.data

            activity.theme.resolveAttribute(
                R.attr.colorSurfaceDialog,
                typedValue,
                true
            )
            colorSurfaceDialog.value = typedValue.data


            activity.theme.resolveAttribute(
                R.attr.colorInactivePlan,
                typedValue,
                true
            )
            colorInactivePlan.value = typedValue.data

        }


    }

}
