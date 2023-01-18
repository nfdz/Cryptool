package io.github.nfdz.cryptool.ui.extension

import java.util.*

fun Long.isToday(): Boolean {
    val calendar = Calendar.getInstance()
    val todayYear = calendar.get(Calendar.YEAR)
    val todayDay = calendar.get(Calendar.DAY_OF_YEAR)

    calendar.time = Date(this)

    return todayYear == calendar.get(Calendar.YEAR) &&
            todayDay == calendar.get(Calendar.DAY_OF_YEAR)
}