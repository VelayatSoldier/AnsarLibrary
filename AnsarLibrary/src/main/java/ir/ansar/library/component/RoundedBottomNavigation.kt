package ir.ansar.library.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.get
import com.google.android.material.bottomnavigation.BottomNavigationView
import ir.ansar.library.R
import ir.ansar.library.model.BottomNavigationItemModel
import ir.ansar.library.utils.FragmentUtils
import ir.ansar.library.utils.ThemeUtils
import ir.ansar.library.utils.UnitConverter

class RoundedBottomNavigation @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : BottomNavigationView(context, attrs) {

    private var bottomNavHeight: Int = 0
    private var bottomNavWidth: Int = 0
    private var bottomNavHeightOffset: Float = UnitConverter.toPx(5f, context)

    private var context: AppCompatActivity
    private var path: Path
    private var paint: Paint

    private lateinit var longClickListener: () -> Unit

    private var selectedItem = 1

    private val rootLinearLayout = LinearLayout(context)
    private val items = mutableListOf<BottomNavigationItemModel>()
    private val activeIcons = mutableListOf<ImageView>()
    private val inactiveIcons = mutableListOf<ImageView>()
    private val titles = mutableListOf<TextView>()

    init {

        setBackgroundColor(Color.TRANSPARENT)
        layoutDirection = LAYOUT_DIRECTION_RTL

        path = Path()

        paint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE

            setShadowLayer(8f, 0f, -UnitConverter.toPx(1f, context), Color.parseColor("#20000000"))
        }

        this.context = context as AppCompatActivity
        ThemeUtils.themeName.observe(context as AppCompatActivity) {
            paint.color = ThemeUtils.colorSurfaceContainer.value!!
            invalidate()

            activeIcons.forEach {
                it.setColorFilter(ThemeUtils.colorOnSurface.value!!)
            }
            inactiveIcons.forEach {
                it.setColorFilter(ThemeUtils.colorOnSurface.value!!)
            }

        }

        setLayerType(LAYER_TYPE_SOFTWARE, null)

        rootLinearLayout.layoutParams = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        (rootLinearLayout.layoutParams as RelativeLayout.LayoutParams).topMargin =
            bottomNavHeightOffset.toInt()
        rootLinearLayout.orientation = LinearLayout.HORIZONTAL
        addView(rootLinearLayout)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        bottomNavHeight = h
        bottomNavWidth = w

    }

    override fun onDraw(canvas: Canvas) {

        createCurve()
        canvas.drawPath(path, paint)

    }

    private fun createCurve() {

        path.reset()
        val rect =
            RectF(0f, bottomNavHeightOffset, bottomNavWidth.toFloat(), bottomNavHeight.toFloat())
        val ar = FloatArray(8)
        ar[0] = UnitConverter.toPx(20f, context)
        ar[1] = UnitConverter.toPx(15f, context)
        ar[2] = UnitConverter.toPx(20f, context)
        ar[3] = UnitConverter.toPx(15f, context)
        ar[4] = 0f
        ar[5] = 0f
        ar[6] = 0f
        ar[7] = 0f
        path.addRoundRect(rect, ar, Path.Direction.CCW)
        path.close()
    }

    fun setItems(vararg items: BottomNavigationItemModel) {
        this.items.clear()
        this.items.addAll(items)
        initItemViews()

        ThemeUtils.themeName.observe(context) {
            activeIcons.forEach {
                it.setColorFilter(ThemeUtils.colorPrimary.value!!)
            }
            inactiveIcons.forEach {
                it.setColorFilter(ThemeUtils.colorOnSurfaceVariant.value!!)
            }
            titles.forEach {
                if (it.tag == selectedItem)
                    it.setTextColor(ThemeUtils.colorPrimary.value!!)
                else
                    it.setTextColor(ThemeUtils.colorOnSurfaceVariant.value!!)
            }
        }
    }

    fun setOnHomeLongListener(longClickListener: () -> Unit) {
        this.longClickListener = longClickListener
    }

    private fun initItemViews() {

        rootLinearLayout.removeAllViews()

        for (i in 0..<items.size) {

            val item = items[i]
            val rootLayoutItem = LinearLayout(context)
            rootLayoutItem.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f
            )
            rootLayoutItem.orientation = LinearLayout.VERTICAL
            rootLayoutItem.setOnClickListener {

                if (item.fragmentId != selectedItem) {

                    (rootLinearLayout.findViewWithTag<LinearLayout>(selectedItem)[0] as RelativeLayout)[1].alpha =
                        0f
                    (rootLinearLayout.findViewWithTag<LinearLayout>(selectedItem)[1] as TextView).setTextColor(
                        ThemeUtils.colorOnSurfaceVariant.value!!
                    )
                    ((it as LinearLayout)[0] as RelativeLayout)[1].alpha = 1f
                    (it[1] as TextView).setTextColor(ThemeUtils.colorPrimary.value!!)
                    selectedItem = item.fragmentId

                    FragmentUtils.fragmentChanged.value = item.fragmentId

                }

            }
            /*    rootLayoutItem.setOnLongClickListener {
                    longClickListener.invoke()
                    true
                }*/
              val typedValue = TypedValue()
              context.theme.resolveAttribute(
                  android.R.attr.selectableItemBackground,
                  typedValue,
                  true
              )
              rootLayoutItem.setBackgroundResource(typedValue.resourceId)
            rootLayoutItem.tag = item.fragmentId
            rootLinearLayout.addView(rootLayoutItem)
            if (i == 0) selectedItem = item.fragmentId

            val iconContainer = RelativeLayout(context)
            iconContainer.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            (iconContainer.layoutParams as LinearLayout.LayoutParams).gravity =
                Gravity.CENTER_HORIZONTAL
            (iconContainer.layoutParams as LinearLayout.LayoutParams).topMargin =
                UnitConverter.toPx(8f, context).toInt()
            rootLayoutItem.addView(iconContainer)

            val iconInactive = ImageView(context)
            iconInactive.layoutParams = RelativeLayout.LayoutParams(
                UnitConverter.toPx(25f, context).toInt(),
                UnitConverter.toPx(25f, context).toInt()
            )
            iconInactive.setImageResource(item.inactiveIcon)
            iconContainer.addView(iconInactive)
            inactiveIcons.add(iconInactive)

            val iconActive = ImageView(context)
            val lp = RelativeLayout.LayoutParams(
                UnitConverter.toPx(25f, context).toInt(),
                UnitConverter.toPx(25f, context).toInt()
            )
            iconActive.layoutParams = lp
            if ((i + 1) != 1) iconActive.alpha = 0f
            iconActive.setImageResource(item.activeIcon)
            iconContainer.addView(iconActive)
            activeIcons.add(iconActive)

            val title = TextView(context)
            title.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            (title.layoutParams as LinearLayout.LayoutParams).gravity = Gravity.CENTER_HORIZONTAL
            (title.layoutParams as LinearLayout.LayoutParams).topMargin =
                UnitConverter.toPx(4f, context).toInt()
            title.typeface = ResourcesCompat.getFont(context, R.font.vazir_regular)
            title.textSize = 12f
            title.tag = item.fragmentId
            title.text = item.title
            rootLayoutItem.addView(title)
            titles.add(title)

        }

    }


}