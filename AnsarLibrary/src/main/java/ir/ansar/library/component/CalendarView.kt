package ir.ansar.library.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnLayout
import com.google.android.material.button.MaterialButton
import ir.ansar.library.R
import ir.ansar.library.utils.JalaliCalendar
import java.util.Calendar

class CalendarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var calendarMiladi = Calendar.getInstance()
    private var calendarShamsi = JalaliCalendar(
        true,
        calendarMiladi.get(Calendar.YEAR),
        calendarMiladi.get(Calendar.MONTH) + 1,
        calendarMiladi.get(Calendar.DAY_OF_MONTH)
    )

    val typedValue = TypedValue()

    private val daySelectedCircle = PointF(0f, 0f)
    private val todayCircle = PointF(0f, 0f)

    private val textColorVariant: Int
    private val selectedBackgroundPaint: Paint = Paint()
    private val todayCirclePaint: Paint = Paint()

    private var currentYear = 0
    private var currentMonth = 0
    private var currentDay = 0
    private var todayMonth = calendarShamsi.getMonthShamsi()
    private var today = calendarShamsi.getDayShamsi()
    private var todayYear = calendarShamsi.getYearShamsi()
    private var selected = true
    private var monthSelected = true


    init {

        setBackgroundColor(Color.TRANSPARENT)

        context.theme.obtainStyledAttributes(attrs, R.styleable.CalendarView, 0, 0)
            .apply {
                try {
                    textColorVariant =
                        getColor(R.styleable.CalendarView_text_color_variant, Color.WHITE)
                } finally {
                    recycle()
                }
            }

        context.theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)


        selectedBackgroundPaint.apply {
            style = Paint.Style.FILL
            color = typedValue.data
            setShadowLayer(12f, 0f, 0f, typedValue.data)
        }

        todayCirclePaint.apply {
            style = Paint.Style.STROKE
            color = typedValue.data
            strokeWidth = 4f
        }

        createLayouts()

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (selected)
            canvas.drawCircle(
                daySelectedCircle.x,
                daySelectedCircle.y,
                55f,
                selectedBackgroundPaint
            )

        if (todayCircle.x != 0f)
            canvas.drawCircle(todayCircle.x, todayCircle.y, 55f, todayCirclePaint)

        // Toast.makeText(context, "draw", Toast.LENGTH_SHORT).show()
    }

    private fun createLayouts() {

        currentYear = calendarShamsi.getYearShamsi()
        currentMonth = calendarShamsi.getMonthShamsi()
        currentDay = today

        removeAllViews()
        layoutDirection = LAYOUT_DIRECTION_LTR

        val rootLinearLayout = LinearLayout(context)
        val rootLayoutParams =
            LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        rootLinearLayout.apply {
            orientation = LinearLayout.VERTICAL
        }


        val headerLinearLayout = LinearLayout(context)
        val headerLayoutParams =
            LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        headerLinearLayout.apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = headerLayoutParams
            setPadding(0, 0, 0, 20)
        }


        val insideHeaderLayoutParams =
            LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f)
        /*  val nextMonthTextView = TextView(context)
          nextMonthTextView.apply {
              layoutParams = insideHeaderLayoutParams
              setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_HeadlineMedium)
              gravity = Gravity.CENTER
              text = context.getString(R.string.next_month_symbol)
              typeface = ResourcesCompat.getFont(context, R.font.vazir_regular)
              setTextColor(textColorVariant)
          }*/
        val nextMonthButton = MaterialButton(
            context,
            null,
            com.google.android.material.R.attr.materialButtonOutlinedStyle
        )
        val nmlp = LinearLayout.LayoutParams(150, LayoutParams.WRAP_CONTENT)
        nmlp.leftMargin = 50
        nextMonthButton.apply {
            layoutParams = nmlp
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_HeadlineSmall)
            text = context.getString(R.string.next_month_symbol)
            // typeface = ResourcesCompat.getFont(context, R.font.vazir_regular)
            setTextColor(typedValue.data) //textColorVariant
            setStrokeColorResource(typedValue.resourceId)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 0)
        }
        val monthTextView = TextView(context)
        val monthLayoutParams =
            LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f)
        //   monthLayoutParams.topMargin = 10
        monthTextView.apply {
            layoutParams = monthLayoutParams
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleLarge)
            gravity = Gravity.RIGHT
            text = calendarShamsi.getYearShamsi().toString()
            typeface = ResourcesCompat.getFont(context, R.font.vazir_regular)
            setTextColor(textColorVariant)
        }

        val mTextView = TextView(context)
        val mLayoutParams =
            LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.5f)
        mLayoutParams.topMargin = 10
        mTextView.apply {
            layoutParams = mLayoutParams
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleLarge)
            gravity = Gravity.CENTER
            text = ","
            typeface = ResourcesCompat.getFont(context, R.font.vazir_regular)
            setTextColor(textColorVariant)
        }

        val yearTextView = TextView(context)
        yearTextView.apply {
            layoutParams = insideHeaderLayoutParams
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_HeadlineMedium)
            gravity = Gravity.LEFT
            maxLines = 1
            text = calendarShamsi.getTitleMonth()
            typeface = ResourcesCompat.getFont(context, R.font.vazir_medium)

        }
        /* val previousTextView = TextView(context)
         previousTextView.apply {
             layoutParams = insideHeaderLayoutParams
             setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_HeadlineMedium)
             gravity = Gravity.CENTER
             text = context.getString(R.string.previous_month_symbol)
             typeface = ResourcesCompat.getFont(context, R.font.vazir_regular)
             setTextColor(textColorVariant)
         }*/
        val previousMonthButton = MaterialButton(
            context,
            null,
            com.google.android.material.R.attr.materialButtonOutlinedStyle
        )
        val pmlp = LinearLayout.LayoutParams(150, LayoutParams.WRAP_CONTENT)
        pmlp.rightMargin = 50
        previousMonthButton.apply {
            layoutParams = pmlp
            setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_HeadlineSmall)
            text = context.getString(R.string.previous_month_symbol)
            // typeface = ResourcesCompat.getFont(context, R.font.vazir_regular)
            setTextColor(typedValue.data) //textColorVariant
            setStrokeColorResource(typedValue.resourceId)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 0)
        }
        headerLinearLayout.addView(nextMonthButton)
        headerLinearLayout.addView(monthTextView)
        headerLinearLayout.addView(mTextView)
        headerLinearLayout.addView(yearTextView)
        headerLinearLayout.addView(previousMonthButton)


        val dateLinearLayout = LinearLayout(context)
        val dateLayoutParams =
            LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        dateLinearLayout.apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = dateLayoutParams
            layoutDirection = LAYOUT_DIRECTION_RTL
        }

        addDate(calendarShamsi, headerLinearLayout, dateLinearLayout, yearTextView, monthTextView)

        /*  val fab = FloatingActionButton(
              context,
              null,
              com.google.android.material.R.style.Widget_MaterialComponents_FloatingActionButton
          )
          val fablp = LinearLayout.LayoutParams(
              125,
             125
          )
          fablp.gravity = Gravity.LEFT
          fablp.leftMargin = 30
          fab.apply {
              layoutParams = fablp
              isClickable = true
              isFocusable = true
              backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.primary))
              setImageResource(R.drawable.baseline_refresh_24)
          }*/


        nextMonthButton.setOnClickListener {

            if (monthSelected) {

                selected = false
                removeDate(dateLinearLayout)
                if (calendarShamsi.getMonthShamsi() == 12) {
                    currentYear += 1
                    currentMonth = 1
                } else {
                    currentMonth += 1
                }

                if (calendarShamsi.getMonthShamsi() + 1 == todayMonth)
                    currentDay = today

                calendarShamsi.setShamsiDate(currentYear, currentMonth, currentDay)

                addDate(
                    calendarShamsi,
                    headerLinearLayout,
                    dateLinearLayout,
                    yearTextView,
                    monthTextView
                )
            } else {
                removeDate(dateLinearLayout)
                currentYear += 1
                calendarShamsi.setShamsiDate(currentYear, currentMonth, currentDay)
                addDate(
                    calendarShamsi,
                    headerLinearLayout,
                    dateLinearLayout,
                    yearTextView,
                    monthTextView
                )

            }
        }

        previousMonthButton.setOnClickListener {

            if (monthSelected) {
                selected = false
                removeDate(dateLinearLayout)
                if (calendarShamsi.getMonthShamsi() == 1) {
                    currentYear -= 1
                    currentMonth = 12

                } else {
                    currentMonth -= 1
                    currentDay = today
                }
                if (calendarShamsi.getMonthShamsi() - 1 == todayMonth)
                    currentDay = today

                calendarShamsi.setShamsiDate(currentYear, currentMonth, currentDay)

                addDate(
                    calendarShamsi,
                    headerLinearLayout,
                    dateLinearLayout,
                    yearTextView,
                    monthTextView
                )
            } else {
                removeDate(dateLinearLayout)
                currentYear -= 1
                calendarShamsi.setShamsiDate(currentYear, currentMonth, currentDay)
                addDate(
                    calendarShamsi,
                    headerLinearLayout,
                    dateLinearLayout,
                    yearTextView,
                    monthTextView
                )
            }
        }

        monthTextView.setOnClickListener {

            if (monthSelected) {
                monthSelected = false

                monthTextView.apply {
                    setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_HeadlineMedium)
                    typeface = ResourcesCompat.getFont(context, R.font.vazir_medium)
                }
                yearTextView.apply {
                    setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleLarge)
                    typeface = ResourcesCompat.getFont(context, R.font.vazir_regular)
                    setTextColor(textColorVariant)
                }
            }
        }

        yearTextView.setOnClickListener {

            if (!monthSelected) {
                monthSelected = true

                monthTextView.apply {
                    setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleLarge)
                    typeface = ResourcesCompat.getFont(context, R.font.vazir_regular)
                    setTextColor(textColorVariant)
                }
                yearTextView.apply {
                    setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_HeadlineMedium)
                    typeface = ResourcesCompat.getFont(context, R.font.vazir_medium)

                }
            }
        }

        rootLinearLayout.addView(headerLinearLayout)
        rootLinearLayout.addView(dateLinearLayout)
        //  rootLinearLayout.addView(fab)


        addView(rootLinearLayout, rootLayoutParams)

    }

    private fun addDate(
        calendar: JalaliCalendar,
        headerLinearLayout: LinearLayout,
        dateLinearLayout: LinearLayout,
        monthTextView: TextView,
        yearTextView: TextView
    ) {

        monthTextView.text = calendar.getTitleMonth()
        yearTextView.text = calendar.getYearShamsi().toString()

        for (i in 1..7) {

            val dateLinearLayout2 = LinearLayout(context)
            val dateLayoutParams2 =
                LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
            dateLinearLayout2.apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = dateLayoutParams2
                gravity = Gravity.CENTER

            }

            val weekText = TextView(context)
            val weekTextLayoutParams =
                LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            weekText.apply {
                layoutParams = weekTextLayoutParams
                setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodySmall)
                gravity = Gravity.CENTER
                typeface = ResourcesCompat.getFont(context, R.font.vazir_medium)
                setTextColor(textColorVariant)
                text = when (i) {
                    1 -> "شنبه"
                    2 -> "یک‌شنبه"
                    3 -> "دوشنبه"
                    4 -> "سه‌شنبه"
                    5 -> "چهارشنبه"
                    6 -> "پنج‌شنبه"
                    7 -> "جمعه"
                    else -> ""
                }
            }
            dateLinearLayout2.addView(weekText)

            var isContinue = 1
            var temp = 8 - calendar.firstDay() + i
            var temp2 = i - calendar.firstDay() + 1

            while (isContinue <= 5) {

                val dayText = TextView(context)
                val dayTextLayoutParams =
                    LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                dayText.setPadding(25, 25, 25, 25)
                dayText.apply {
                    layoutParams = dayTextLayoutParams
                    setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleMedium)
                    gravity = Gravity.CENTER
                    typeface = ResourcesCompat.getFont(context, R.font.vazir_medium)

                    if (i == 7)
                        setTextColor(Color.RED)

                    setOnClickListener {
                        if ((it as TextView).text != "") {

                            val s = IntArray(2)
                            it.getLocationOnScreen(s)

                            daySelectedCircle.x =
                                s[0].toFloat() + (it.width / 2)
                            daySelectedCircle.y =
                                it.y + headerLinearLayout.height + (it.height / 2) - 4

                            currentDay = it.tag as Int
                            calendarShamsi.setShamsiDate(currentYear, currentMonth, currentDay)
                            selected = true
                            this@CalendarView.invalidate()
                        }
                    }

                }
                dateLinearLayout2.addView(dayText)

                if (calendar.firstDay() > i) {
                    if (dateLinearLayout2.indexOfChild(dayText) == 1) {
                        continue
                    } else {
                        dayText.tag = temp
                        dayText.text = temp.toString()
                        temp += 7
                        if (temp > calendar.lastDay()) {
                            break
                        }
                    }
                } else {
                    dayText.tag = temp2
                    dayText.text = temp2.toString()
                    temp2 += 7
                    if (temp2 > calendar.lastDay())
                        break
                }

                isContinue++
            }

            dateLinearLayout.addView(dateLinearLayout2)

        }

        for (j in 0..<((dateLinearLayout.getChildAt(calendar.getDayOfWeek() - 1) as LinearLayout)).childCount) {
            if (((dateLinearLayout.getChildAt(calendar.getDayOfWeek() - 1) as LinearLayout).getChildAt(j) as TextView).text == calendar.getDayShamsi()
                    .toString()
            ) {
                doOnLayout {
                    val s = IntArray(2)
                    ((dateLinearLayout.getChildAt(calendar.getDayOfWeek() - 1) as LinearLayout).getChildAt(
                        j
                    ) as TextView).getLocationOnScreen(s)
                    daySelectedCircle.x =
                        s[0].toFloat() + (((dateLinearLayout.getChildAt(
                            calendar.getDayOfWeek() - 1
                        ) as LinearLayout).getChildAt(j) as TextView).width / 2)
                    daySelectedCircle.y =
                        ((dateLinearLayout.getChildAt(calendar.getDayOfWeek() - 1) as LinearLayout).getChildAt(
                            j
                        ) as TextView).y + headerLinearLayout.height + (((dateLinearLayout.getChildAt(
                            calendar.getDayOfWeek() - 1
                        ) as LinearLayout).getChildAt(j) as TextView).height / 2) - 4

                    if (calendar.getMonthShamsi() == todayMonth) {
                        todayCircle.x = s[0].toFloat() + (((dateLinearLayout.getChildAt(
                            calendar.getDayOfWeek() - 1
                        ) as LinearLayout).getChildAt(j) as TextView).width / 2)
                        todayCircle.y =
                            ((dateLinearLayout.getChildAt(calendar.getDayOfWeek() - 1) as LinearLayout).getChildAt(
                                j
                            ) as TextView).y + headerLinearLayout.height + (((dateLinearLayout.getChildAt(
                                calendar.getDayOfWeek() - 1
                            ) as LinearLayout).getChildAt(j) as TextView).height / 2) - 4
                    } else {
                        todayCircle.x = 0f
                        todayCircle.y = 0f
                    }

                    if (calendar.getYearShamsi() != todayYear)
                        todayCircle.x = 0f

                }
            }

        }
    }

    private fun removeDate(dateLinearLayout: LinearLayout) {
        dateLinearLayout.removeAllViews()
    }

    fun getCalendar(): JalaliCalendar {
        return calendarShamsi
    }

    fun setCalendar(year: Int, month: Int, day: Int) {
        calendarShamsi =
            JalaliCalendar(
                true, year, month, day
            )
        createLayouts()
    }

}