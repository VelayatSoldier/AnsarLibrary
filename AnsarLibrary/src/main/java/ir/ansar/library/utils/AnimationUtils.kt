package ir.ansar.library.utils

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.view.View

object AnimationUtils {

    /** Visibility Animation **/
    fun animateVisibility(view: View, show: Boolean, hideMode: Int, duration: Long) {

        view.animate()
            .alpha(if (show) 1f else 0f)
            .setDuration(duration)
            .setInterpolator(CubicBezierInterpolator.DEFAULT)
            .setListener(object : AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    if (show)
                        view.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!show)
                        view.visibility = hideMode
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }

            }).start()
    }

    /** Height Animation **/
    fun animateHeight(view: View, startValue: Int, endValue: Int, duration: Long) {

        val animator = ValueAnimator.ofInt(startValue, endValue)
        animator.duration = duration
        animator.interpolator = CubicBezierInterpolator.DEFAULT
        animator.addUpdateListener {

            view.layoutParams.height =
                it.animatedValue as Int

            view.requestLayout()

        }
        animator.start()
    }

    fun animateHeight(
        view: View,
        startValue: Int,
        endValue: Int,
        duration: Long,
        listener: () -> Unit
    ) {

        val animator = ValueAnimator.ofInt(startValue, endValue)
        animator.duration = duration
        animator.interpolator = CubicBezierInterpolator.DEFAULT
        animator.addUpdateListener {

            view.layoutParams.height =
                it.animatedValue as Int

            view.requestLayout()
        }

        animator.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                listener.invoke()
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }


        })
        animator.start()
    }

    fun animateMinimumHeight(view: View, startValue: Int, endValue: Int, duration: Long) {

        val animator = ValueAnimator.ofInt(startValue, endValue)
        animator.duration = duration
        animator.interpolator = CubicBezierInterpolator.DEFAULT
        animator.addUpdateListener {

            view.minimumHeight =
                it.animatedValue as Int

            view.requestLayout()

        }
        animator.start()
    }

}