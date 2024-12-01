package ir.ansar.library.dialog

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.LayoutRes
import ir.ansar.library.utils.CubicBezierInterpolator

open class SimpleDialogBuilder(private val activity: Activity) {

    protected var root: RelativeLayout = RelativeLayout(activity)
    private val layoutParams = RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )

    init {
        root.background = null
    }

    fun setDialogMargin(top: Int, right: Int, bottom: Int, left: Int): SimpleDialogBuilder {

        layoutParams.topMargin = top
        layoutParams.rightMargin = right
        layoutParams.bottomMargin = bottom
        layoutParams.leftMargin = left

        return this
    }

    open fun setView(view: View): SimpleDialogBuilder {

        val lp: RelativeLayout.LayoutParams =
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )

        view.setOnClickListener { }
        root.addView(view, lp)


        return this
    }

    open fun setView(@LayoutRes layout: Int): SimpleDialogBuilder {

        val view = LayoutInflater.from(activity).inflate(layout, null)

        val lp: RelativeLayout.LayoutParams =
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )

        view.setOnClickListener { }
        root.addView(view, lp)

        return this
    }

    open fun show(): SimpleDialogBuilder {

        activity.addContentView(root, layoutParams)

        return this
    }

    open fun dismiss() {

        root.animate()
            .setInterpolator(CubicBezierInterpolator.EASE_BOTH)
            .alpha(0f)
            .setListener(object : AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    (root.parent as ViewGroup).removeView(root)
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }


            })
            .start()


    }
}