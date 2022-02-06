package calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class CalendarState(
    initialDate:LocalDate
){
    var yearOnPanel:Int
        get() = _yearOnPanel.value
        set(value){
            _yearOnPanel.value = value
        }
    private var _yearOnPanel = mutableStateOf(initialDate.year)

    var monthOnPanel:Int
        get() = _monthOnPanel.value
        set(value){
            if(value in 1..12) _monthOnPanel.value = value
        }
    private var _monthOnPanel = mutableStateOf(initialDate.monthNumber)

    var date:LocalDate
        get() = _date.value
        set(value){
            _date.value = value
        }
    private var _date = mutableStateOf(initialDate)

    fun get2DigitMonth():String = if (monthOnPanel < 10) "0$monthOnPanel" else monthOnPanel.toString()

    fun selectDate(date:LocalDate){
        this.date = date
        this.yearOnPanel = date.year
        this.monthOnPanel = date.monthNumber
    }

    fun showYear(year:Int){
        this.yearOnPanel = year
    }

    fun showMonth(month:Int){
        if(month in 1..12) this.monthOnPanel = month
    }
}

@Composable
fun rememberCalendarState(
    initialDate:LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
):CalendarState{
    return remember {
        CalendarState(initialDate)
    }
}