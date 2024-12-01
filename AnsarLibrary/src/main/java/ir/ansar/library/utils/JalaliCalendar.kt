package ir.ansar.library.utils

import java.util.Calendar

class JalaliCalendar {

    private val calendar: Calendar = Calendar.getInstance()
    private var kabisehList = listOf(1403, 1408, 1412, 1416, 1420)
    var isShamsiKabiseh: Boolean = false

    private var yearShamsi = 0
    private var monthShamsi = 0
    private var dayShamsi = 0
    private var shamsiArray = IntArray(3)

    private var yearMiladi = 0
    private var monthMiladi = 0
    private var dayMiladi = 0
    private var miladiArray = IntArray(3)

    companion object {

        val today = JalaliCalendar()
        private val tempCal = Calendar.getInstance()

        fun gregorianToJalali(gy: Int, gm: Int, gd: Int): IntArray {
            val g_d_m: IntArray = intArrayOf(0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334)
            val gy2: Int = if (gm > 2) (gy + 1) else gy
            var days: Int =
                355666 + (365 * gy) + ((gy2 + 3) / 4).toInt() - ((gy2 + 99) / 100).toInt() + ((gy2 + 399) / 400).toInt() + gd + g_d_m[gm - 1]
            var jy: Int = -1595 + (33 * (days / 12053).toInt())
            days %= 12053
            jy += 4 * (days / 1461).toInt()
            days %= 1461
            if (days > 365) {
                jy += ((days - 1) / 365).toInt()
                days = (days - 1) % 365
            }
            var jm: Int;
            var jd: Int;
            if (days < 186) {
                jm = 1 + (days / 31).toInt()
                jd = 1 + (days % 31)
            } else {
                jm = 7 + ((days - 186) / 30).toInt()
                jd = 1 + ((days - 186) % 30)
            }
            return intArrayOf(jy, jm, jd)
        }

        fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): IntArray {
            var jy1: Int = jy + 1595
            var days: Int =
                -355668 + (365 * jy1) + ((jy1 / 33).toInt() * 8) + (((jy1 % 33) + 3) / 4).toInt() + jd + (if (jm < 7) ((jm - 1) * 31) else (((jm - 7) * 30) + 186))
            var gy: Int = 400 * (days / 146097).toInt()
            days %= 146097
            if (days > 36524) {
                gy += 100 * (--days / 36524).toInt()
                days %= 36524
                if (days >= 365) days++
            }
            gy += 4 * (days / 1461).toInt()
            days %= 1461
            if (days > 365) {
                gy += ((days - 1) / 365).toInt()
                days = (days - 1) % 365
            }
            var gd: Int = days + 1
            var sal_a: IntArray = intArrayOf(
                0,
                31,
                if ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)) 29 else 28,
                31,
                30,
                31,
                30,
                31,
                31,
                30,
                31,
                30,
                31
            )
            var gm: Int = 0
            while (gm < 13 && gd > sal_a[gm]) gd -= sal_a[gm++]

            return intArrayOf(gy, gm, gd)
        }

        fun distanceAsDay(
            yearMiladi: Int,
            monthMiladi: Int,
            dayMiladi: Int,
        ): Long {

            val t1 = today.calendar.timeInMillis
            tempCal.set(yearMiladi, monthMiladi - 1, dayMiladi)
            val t2 = tempCal.timeInMillis

            return ((t2 - t1) / 86400000)
        }

        fun distanceAsDay(calendar: JalaliCalendar): Long {

            val t1 = today.calendar.timeInMillis
            tempCal.set(
                calendar.getYearMiladi(),
                calendar.getMonthMiladi() - 1,
                calendar.getDayMiladi()
            )
            val t2 = tempCal.timeInMillis

            return ((t2 - t1) / 86400000)
        }

        fun increaseToCalendar(date: JalaliCalendar, increaseMonth: Int, increaseDay: Int) {

            date.setShamsiDate(
                date.getYearShamsi(),
                date.getMonthShamsi() + increaseMonth,
                date.getDayShamsi() + increaseDay
            )

        }

        fun decreaseToCalendar(date: JalaliCalendar, decreaseMonth: Int, decreaseDay: Int) {

            date.setShamsiDate(
                date.getYearShamsi(),
                date.getMonthShamsi() - decreaseMonth,
                date.getDayShamsi() - decreaseDay
            )

        }

        fun dateToCode(j: JalaliCalendar): String {

            val month: String = if (j.getMonthShamsi() > 9)
                j.getMonthShamsi().toString()
            else
                "0${j.getMonthShamsi()}"

            val day: String = if (j.getDayShamsi() > 9)
                j.getDayShamsi().toString()
            else
                "0${j.getDayShamsi()}"

            val hour: String = if (j.getHour() > 9)
                j.getHour().toString()
            else
                "0${j.getHour()}"

            val minute: String = if (j.getMinute() > 9)
                j.getMinute().toString()
            else
                "0${j.getMinute()}"

            return (j.getYearShamsi().toString() +
                    month + day + hour + minute)
        }

        fun dateToCodeUntilDay(j: JalaliCalendar): String {

            val month: String = if (j.getMonthShamsi() > 9)
                j.getMonthShamsi().toString()
            else
                "0${j.getMonthShamsi()}"

            val day: String = if (j.getDayShamsi() > 9)
                j.getDayShamsi().toString()
            else
                "0${j.getDayShamsi()}"

            return (j.getYearShamsi().toString() +
                    month + day)
        }

        fun codeToDateUntilDay(code: String): JalaliCalendar {
            val year = code.substring(0, 4).toInt()
            val month = code.substring(4, 6).toInt()
            val day = code.substring(6, 8).toInt()
            return JalaliCalendar(false, year, month, day)
        }

        fun weekTitle(dayOfWeek: Int): String {

            return when (dayOfWeek) {
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

        fun isShamsiKabiseh(year: Int): Boolean {

            val kabisehList = listOf(1403, 1408, 1412, 1416, 1420)

            for (i in kabisehList)
                if (year == i)
                    return true

            return false
        }

        fun numberForString(number: Int): String {
            return if (number < 10)
                "0$number"
            else number.toString()
        }

    }

    constructor() {

        shamsiArray = gregorianToJalali(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        yearShamsi = shamsiArray[0]
        monthShamsi = shamsiArray[1]
        dayShamsi = shamsiArray[2]

        yearMiladi = calendar.get(Calendar.YEAR)
        monthMiladi = calendar.get(Calendar.MONTH) + 1
        dayMiladi = calendar.get(Calendar.DAY_OF_MONTH)

        for (i in kabisehList) {
            if (yearShamsi == i) {
                isShamsiKabiseh = true
                break
            } else
                isShamsiKabiseh = false
        }

    }

    constructor(miladiToShamsi: Boolean, year: Int, month: Int, day: Int) {

        if (miladiToShamsi)
            setMiladiDate(year, month, day)
        else
            setShamsiDate(year, month, day)
    }


    fun setShamsiDate(year: Int, month: Int, day: Int) {

        yearShamsi = year
        monthShamsi = month
        dayShamsi = day

        for (i in kabisehList) {
            if (yearShamsi == i) {
                isShamsiKabiseh = true
                break
            } else
                isShamsiKabiseh = false
        }

        var repeat = true

        while (repeat)
            if (monthShamsi <= 6) {

                if (dayShamsi < 1) {

                    if (monthShamsi == 1) {
                        yearShamsi -= 1
                        monthShamsi = 12
                        dayShamsi += 29
                        for (i in kabisehList)
                            if (yearShamsi == i) {
                                dayShamsi += 1
                                isShamsiKabiseh = true
                                break
                            } else
                                isShamsiKabiseh = false

                    } else {
                        monthShamsi -= 1
                        dayShamsi += 31
                    }

                } else if (dayShamsi > 31) {
                    dayShamsi -= 31
                    monthShamsi += 1
                } else
                    repeat = false

            } else if (monthShamsi <= 11) {

                if (dayShamsi < 1) {

                    dayShamsi += if (monthShamsi == 7)
                        31
                    else
                        30
                    monthShamsi -= 1

                } else if (dayShamsi > 30) {
                    dayShamsi -= 30
                    monthShamsi += 1
                } else
                    repeat = false

            } else if (monthShamsi == 12) {

                if (dayShamsi < 1) {

                    dayShamsi += 30
                    monthShamsi -= 1

                } else if (isShamsiKabiseh) {
                    if (dayShamsi > 30) {
                        dayShamsi -= 30
                        monthShamsi = 1
                        yearShamsi += 1
                    } else
                        repeat = false
                } else {
                    if (dayShamsi > 29) {
                        dayShamsi -= 29
                        monthShamsi = 1
                        yearShamsi += 1
                    } else
                        repeat = false
                }

            } else {
                monthShamsi -= 12
                yearShamsi += 1
            }

        miladiArray = jalaliToGregorian(yearShamsi, monthShamsi, dayShamsi)
        yearMiladi = miladiArray[0]
        monthMiladi = miladiArray[1]
        dayMiladi = miladiArray[2]

        calendar.set(yearMiladi, monthMiladi - 1, dayMiladi)
    }

    fun setMiladiDate(year: Int, month: Int, day: Int) {

        calendar.set(year, month - 1, day)

        yearMiladi = calendar.get(Calendar.YEAR)
        monthMiladi = calendar.get(Calendar.MONTH) + 1
        dayMiladi = calendar.get(Calendar.DAY_OF_MONTH)

        shamsiArray = gregorianToJalali(
            yearMiladi,
            monthMiladi,
            dayMiladi
        )
        yearShamsi = shamsiArray[0]
        monthShamsi = shamsiArray[1]
        dayShamsi = shamsiArray[2]

        for (i in kabisehList) {
            if (yearShamsi == i)
                isShamsiKabiseh = true
        }

    }

    fun getTitleMonth(): String {
        return when (monthShamsi) {
            1 -> "فروردین"
            2 -> "اردیبهشت"
            3 -> "خرداد"
            4 -> "تیر"
            5 -> "مرداد"
            6 -> "شهریور"
            7 -> "مهر"
            8 -> "آبان"
            9 -> "آذر"
            10 -> "دی"
            11 -> "بهمن"
            12 -> "اسفند"
            else -> "خطا"
        }
    }

    fun getTitleWeek(): String {
        if (calendar.get(Calendar.DAY_OF_WEEK) == 7)
            return "شنبه"
        else if (calendar.get(Calendar.DAY_OF_WEEK) == 1)
            return "یک‌شنبه"
        else if (calendar.get(Calendar.DAY_OF_WEEK) == 2)
            return "دوشنبه"
        else if (calendar.get(Calendar.DAY_OF_WEEK) == 3)
            return "سه‌شنبه"
        else if (calendar.get(Calendar.DAY_OF_WEEK) == 4)
            return "چهارشنبه"
        else if (calendar.get(Calendar.DAY_OF_WEEK) == 5)
            return "پنج‌شنبه"
        else if (calendar.get(Calendar.DAY_OF_WEEK) == 6)
            return "جمعه"
        return "خطا"
    }

    fun getDayOfWeek(): Int {

        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            7 -> 1
            1 -> 2
            2 -> 3
            3 -> 4
            4 -> 5
            5 -> 6
            6 -> 7
            else -> -1
        }

    }

    fun getYearShamsi(): Int {
        return yearShamsi
    }

    fun getMonthShamsi(): Int {
        return monthShamsi
    }

    fun getDayShamsi(): Int {
        return dayShamsi
    }

    fun getYearMiladi(): Int {
        return yearMiladi
    }

    fun getMonthMiladi(): Int {
        return monthMiladi
    }

    fun getDayMiladi(): Int {
        return dayMiladi
    }

    fun getHour(): Int {
        return calendar[Calendar.HOUR_OF_DAY]
    }

    fun getMinute(): Int {
        return calendar[Calendar.MINUTE]
    }

    fun firstDay(): Int {
        return if (getDayOfWeek() - (dayShamsi % 7) == 0)
            1
        else if (getDayOfWeek() - (dayShamsi % 7) > 0)
            (getDayOfWeek() - (dayShamsi % 7) + 1)
        else
            (7 - (dayShamsi % 7) + getDayOfWeek() + 1)
    }

    fun lastDay(): Int {

        if (isShamsiKabiseh && monthShamsi == 12)
            return 30
        else if (monthShamsi == 12)
            return 29
        else if (monthShamsi <= 6)
            return 31
        else if (monthShamsi > 6)
            return 30
        return -1
    }

    override fun toString(): String {
        return getYearShamsi().toString() + " " + getMonthShamsi().toString() + " " + getDayShamsi().toString()
    }

}