package ir.ansar.library.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import ir.ansar.library.R

object FragmentUtils {

    val fragmentChanged = MutableLiveData<Int>()

    fun initFragments(activity: AppCompatActivity, vararg fragment: Fragment) {

        fragment.forEach {
            activity.supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, it).commit()
        }

    }

}