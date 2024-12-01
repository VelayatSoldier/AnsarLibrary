package ir.ansar.library.component

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.addListener
import androidx.core.view.get
import androidx.core.view.iterator
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.motion.MotionUtils
import com.google.android.material.navigation.NavigationBarMenu
import ir.ansar.library.utils.ThemeUtils
import kotlinx.coroutines.launch

@SuppressLint("RestrictedApi")
class CurvedBottomNavigation @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : BottomNavigationView(context, attrs) {

    private val mainActivity = context as AppCompatActivity

    private var bottomNavHeight: Int = 0
    private var bottomNavWidth: Int = 0
    private val fabRadius = 100f
    private var fabY = fabRadius

    private val curveOffset = fabRadius * 2
    private val curveHeight = fabRadius + 10f

    private var bottomNavigationOffset = 0f

    private var navBg: Int = Color.WHITE

    private val startCurve: PointF = PointF()
    private val controlPoint1: PointF = PointF()
    private val controlPoint2: PointF = PointF()
    private val halfCurve: PointF = PointF()
    private val controlPoint3: PointF = PointF()
    private val controlPoint4: PointF = PointF()
    private val endCurve: PointF = PointF()

    private var path: Path

    private var paint: Paint
    private var paintFab: Paint

    private lateinit var menu: NavigationBarMenu
    private lateinit var selectedListener: OnItemSelectedListener

    private val animDuration: Long = 350

    private var itemCount = 3
    private var itemSelectedWidth = 0

    private var previousItemSelectedWidth = 0

    private var itemId = 0
    private var previousItemId = 0

    private var halfCurveCurrent = 0f

    private var animating: Boolean = false

    init {

        setBackgroundColor(Color.TRANSPARENT)

        path = Path()

        paint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            color = navBg
            setShadowLayer(8f, 0f, -1f, Color.parseColor("#30000000"))

        }

        paintFab = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            color = navBg
            setShadowLayer(10f, 0f, 0f, Color.parseColor("#35000000"))

        }

        setLayerType(LAYER_TYPE_SOFTWARE, null)

        paint.color = ThemeUtils.colorSurfaceContainer.value!!
        paintFab.color = ThemeUtils.colorSurfaceContainer.value!!
        invalidate()
        

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        bottomNavHeight = h
        bottomNavWidth = w
        itemSelectedWidth = (bottomNavWidth * 5) / (itemCount * 2)

        itemId = itemCount

        previousItemSelectedWidth = itemSelectedWidth

        halfCurveCurrent = curveOffset + (fabRadius / 3f)

    }

    override fun onDraw(canvas: Canvas) {

        setupPoint(previousItemSelectedWidth)
        createCurve()

        canvas.drawPath(path, paint)

        canvas.drawCircle(previousItemSelectedWidth.toFloat(), fabY + 10f, fabRadius, paintFab)

    }

    private fun setupPoint(w: Int) {

        startCurve.apply {
            x = w - curveOffset
            y = curveHeight
        }

        halfCurve.apply {
            x = w.toFloat()
            y = halfCurveCurrent
        }

        controlPoint1.apply {
            x = w - fabRadius
            y = curveHeight
        }

        controlPoint2.apply {
            x = halfCurve.x - fabRadius
            y = halfCurve.y
        }

        controlPoint3.apply {
            x = halfCurve.x + fabRadius
            y = halfCurve.y
        }

        controlPoint4.apply {
            x = w + fabRadius
            y = curveHeight
        }

        endCurve.apply {
            x = w + curveOffset
            y = curveHeight
        }


    }

    private fun createCurve() {

        path.reset()
        path.moveTo(bottomNavigationOffset, curveHeight)
        path.lineTo(startCurve.x, startCurve.y)
        path.cubicTo(
            controlPoint1.x,
            controlPoint1.y,
            controlPoint2.x,
            controlPoint2.y,
            halfCurve.x,
            halfCurve.y
        )
        path.cubicTo(
            controlPoint3.x,
            controlPoint3.y,
            controlPoint4.x,
            controlPoint4.y,
            endCurve.x,
            endCurve.y
        )
        path.lineTo(bottomNavWidth.toFloat(), curveHeight)
        path.lineTo(
            bottomNavWidth.toFloat() - bottomNavigationOffset,
            bottomNavHeight.toFloat() - bottomNavigationOffset
        )
        path.lineTo(bottomNavigationOffset, bottomNavHeight.toFloat() - bottomNavigationOffset)
        path.close()


    }

    override fun setOnItemSelectedListener(listener: OnItemSelectedListener?) {
        if (listener != null) {
            selectedListener = listener
        }
    }

    private fun initItemViews() {
        val iconList = arrayListOf<ImageView>()

        removeAllViews()
        val mainLinearLayout = LinearLayout(context)
        val bottomNavLayoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            Gravity.BOTTOM
        )

        for (item in menu) {

            val icon = ImageView(context)
            val iconParams = LinearLayout.LayoutParams(
                0,
                140, 1f
            )

            icon.layoutParams = iconParams

            if (item.order == itemId) {
                icon.y = (fabY + 10) - (icon.layoutParams.height / 2)
                icon.scaleX = 1.2f
                icon.scaleY = 1.2f
            } else
                icon.y =
                    bottomNavHeight.toFloat() - ((bottomNavHeight - fabRadius) / 2) - (icon.layoutParams.height / 2)

            icon.setImageDrawable(item.icon)


            icon.setOnClickListener {

                if (item.order != itemId && !animating) {
                    animating = true
                    previousItemId = itemId
                    itemId = item.order
                    selectedListener.onNavigationItemSelected(item)
                    itemSelectedWidth =
                        (bottomNavWidth * (1 + ((item.order - 1) * 2))) / (itemCount * 2)

                    mainActivity.lifecycleScope.launch {
                        hideAnimation(previousItemId - 1, iconList).apply {
                            addListener(onEnd = {
                                previousItemSelectedWidth = itemSelectedWidth

                                showAnimation(itemId - 1, iconList).apply {
                                    start()

                                    addListener(onEnd = {
                                        animating = false
                                    })
                                }
                            })
                            start()
                        }
                    }

                }
            }

            icon.setOnLongClickListener {
                if (item.order == 3 && item.order == itemId) {

                }
                false
            }

            icon.setPadding(0, 20, 0, 20)

            iconList.add(icon)
            mainLinearLayout.addView(icon)

        }

        addView(mainLinearLayout, bottomNavLayoutParams)

    }

    private fun hideAnimation(previousItem: Int, iconList: List<ImageView>): AnimatorSet {

        val previousIcon = iconList[previousItem]

        val iconScaleAnimator = ValueAnimator.ofFloat(previousIcon.scaleX, 1f)
        iconScaleAnimator.apply {
            addUpdateListener {
                previousIcon.scaleX = it.animatedValue as Float
                previousIcon.scaleY = it.animatedValue as Float
                //  invalidate()
            }
        }

        val iconAnimator = ValueAnimator.ofFloat(
            previousIcon.y,
            (bottomNavHeight.toFloat() - ((bottomNavHeight - fabRadius) / 2) - (previousIcon.layoutParams.height / 2))
        )
        iconAnimator.apply {
            addUpdateListener {
                previousIcon.translationY = it.animatedValue as Float
            }
        }

        val fabHideAnimator = ValueAnimator.ofFloat(fabRadius, fabRadius * 2)
        fabHideAnimator.apply {
            addUpdateListener {
                fabY = it.animatedValue as Float
            }
        }

        val halfAnimator = ValueAnimator.ofFloat(halfCurve.y, fabRadius + 10)
        halfAnimator.apply {
            addUpdateListener {
                halfCurveCurrent = it.animatedValue as Float

            }
        }
        val fabShadowAnimator =
            ValueAnimator.ofArgb(Color.parseColor("#35000000"), Color.parseColor("#00000000"))
        fabShadowAnimator.apply {
            addUpdateListener {
                paintFab.setShadowLayer(10f, 0f, 0f, it.animatedValue as Int)

            }
        }
        val animatorSet = AnimatorSet()
        animatorSet.duration = animDuration
        animatorSet.interpolator = MotionUtils.resolveThemeInterpolator(
            context,
            com.google.android.material.R.attr.motionEasingEmphasizedAccelerateInterpolator,
            AccelerateInterpolator()
        )
        animatorSet.playTogether(
            iconScaleAnimator,
            halfAnimator,
            fabShadowAnimator,
            fabHideAnimator,
            iconAnimator
        )

        return animatorSet
    }

    private fun showAnimation(item: Int, iconList: List<ImageView>): AnimatorSet {

        val icon: ImageView = iconList[item]
        val iconScaleAnimator = ValueAnimator.ofFloat(icon.scaleX, 1.2f)
        iconScaleAnimator.apply {
            addUpdateListener {
                icon.scaleX = it.animatedValue as Float
                icon.scaleY = it.animatedValue as Float
            }
        }

        val iconAnimator = ValueAnimator.ofFloat(
            icon.y,
            ((fabRadius + 10) - (icon.layoutParams.height / 2))
        )
        iconAnimator.apply {
            addUpdateListener {
                icon.translationY = it.animatedValue as Float
            }
        }


        val fabHideAnimator = ValueAnimator.ofFloat(fabRadius * 2, fabRadius)
        fabHideAnimator.apply {
            addUpdateListener {
                fabY = it.animatedValue as Float
            }
        }

        val halfAnimator = ValueAnimator.ofFloat(fabRadius, curveOffset + (fabRadius / 3f))
        halfAnimator.apply {
            addUpdateListener {
                halfCurveCurrent = it.animatedValue as Float

            }
        }

        val fabShadowAnimator =
            ValueAnimator.ofArgb(Color.parseColor("#00000000"), Color.parseColor("#35000000"))
        fabShadowAnimator.apply {
            addUpdateListener {
                paintFab.setShadowLayer(10f, 0f, 0f, it.animatedValue as Int)
                invalidate()
            }
        }
        val animatorSet = AnimatorSet()
        animatorSet.duration = 350
        animatorSet.interpolator = MotionUtils.resolveThemeInterpolator(
            context,
            com.google.android.material.R.attr.motionEasingEmphasizedDecelerateInterpolator,
            DecelerateInterpolator()
        )

        animatorSet.playTogether(
            fabHideAnimator,
            halfAnimator,
            fabShadowAnimator,
            iconAnimator,
            iconScaleAnimator
        )

        return animatorSet
    }

    fun selectItem(item: Int) {
        selectedListener.onNavigationItemSelected(menu[1])
        // selectedListener.onNavigationItemSelected(menu[item - 1])

    }

}