package ir.ansar.library.model

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes

data class BottomNavigationItemModel(
     val title: String,
    @IdRes  val fragmentId: Int,
    @DrawableRes  val inactiveIcon: Int,
    @DrawableRes  val activeIcon: Int
)

