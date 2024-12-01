package ir.ansar.library.dialog

import android.animation.Animator
import android.app.Activity
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.drawToBitmap
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialFade
import com.jgabrielfreitas.core.BlurImageView
import ir.ansar.library.utils.CubicBezierInterpolator

class BlurDialogBuilder(activity: Activity, private val rootLayout: ViewGroup) :
    SimpleDialogBuilder(activity) {

    private var background: BlurImageView = BlurImageView(activity)
    private var content: View? = null

    init {

        background.alpha = 0f
        background.scaleType = ImageView.ScaleType.FIT_XY
        background.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        background.setOnClickListener { }
        root.addView(background)

    }

    fun setView(view: View, gravity: Int, sideMargin: Int): BlurDialogBuilder {

        content = view
        content?.visibility = View.GONE

        val lp: RelativeLayout.LayoutParams =
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
        lp.setMargins(sideMargin, 0, sideMargin, 0)

        when (gravity) {
            Gravity.CENTER -> lp.addRule(
                RelativeLayout.CENTER_IN_PARENT,
                RelativeLayout.TRUE
            )

            Gravity.BOTTOM -> lp.addRule(
                RelativeLayout.ALIGN_PARENT_BOTTOM,
                RelativeLayout.TRUE
            )

            Gravity.TOP -> lp.addRule(
                RelativeLayout.ALIGN_PARENT_TOP,
                RelativeLayout.TRUE
            )
        }
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)

        root.addView(content, lp)

        return this
    }

    override fun show(): BlurDialogBuilder {

        super.show()

        background.setImageBitmap(rootLayout.drawToBitmap())
        background.setBlur(4)
        background.animate().alpha(1f).start()

        if (content != null) {
            val fade = MaterialFade()
            fade.interpolator = CubicBezierInterpolator.DEFAULT
            TransitionManager.beginDelayedTransition(root.parent as ViewGroup, fade)
            content?.visibility = View.VISIBLE
        }

        return this
    }

    override fun dismiss() {

        background.animate().alpha(0f)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {

                }

                override fun onAnimationEnd(animation: Animator) {
                    (root.parent as ViewGroup).removeView(root)
                }

                override fun onAnimationCancel(animation: Animator) {

                }

                override fun onAnimationRepeat(animation: Animator) {

                }

            }).start()

        if (content != null) {
            val fade = MaterialFade()
            fade.interpolator = CubicBezierInterpolator.DEFAULT
            TransitionManager.beginDelayedTransition(root.parent as ViewGroup, fade)
            content?.visibility = View.GONE
        }

    }
}