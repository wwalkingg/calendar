package calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import java.time.format.TextStyle
import java.util.*
import kotlin.math.sqrt

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun DayPicker(
    modifier: Modifier = Modifier,
    state: CalendarState,
    fromSunday: Boolean = true,
    contentColor: Color,
    primaryColor: Color,
    hoverColor: Color,
    content: @Composable DayPickerScope.() -> Unit
) {
    LazyVerticalGrid(cells = GridCells.Fixed(7), modifier = modifier) {
        val firstDayOfMonth = LocalDate(state.yearOnPanel, state.monthOnPanel, 1)
        val offset = firstDayOfMonth.dayOfWeek.value
        // week
        val weekDayArray = if (fromSunday) arrayOf(6, 0, 1, 2, 3, 4, 5) else arrayOf(0, 1, 2, 3, 4, 5, 6)
        weekDayArray.forEach {
            item {
                Text(
                    DayOfWeek.values()[it].getDisplayName(
                        TextStyle.FULL,
                        Locale.SIMPLIFIED_CHINESE
                    ),
                    style = androidx.compose.ui.text.TextStyle(
                        color = contentColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight(400),
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }
        repeat(35) {
            val i = it - offset
            println(firstDayOfMonth.plus(DatePeriod(days = i)))
            item {
                val _date =
                    object : DayPickerScope {
                        override val isSelected: Boolean
                            get() = state.date == date
                        override val isHover: Boolean
                            get() = false
                        override val date: LocalDate
                            get() = firstDayOfMonth.plus(DatePeriod(days = i))
                    }.apply {
                        // item

                        val shape = RoundedCornerShape(6.dp)
                        val interactionSource = remember {
                            MutableInteractionSource()
                        }
                        val isHover by interactionSource.collectIsHoveredAsState()
                        val isInThatMonth = date.monthNumber == state.monthOnPanel
                        var maxBorder by remember {
                            mutableStateOf(80)
                        }
                        var maxRadius = (maxBorder / LocalDensity.current.density) * sqrt(2f)
                        var offset by remember {
                            mutableStateOf(Offset.Zero)
                        }
                        val radius by animateFloatAsState(
                            if (isSelected) maxRadius else 0f,
                            spring(
                                if (isSelected) Spring.DampingRatioLowBouncy else Spring.DampingRatioNoBouncy,
                                if (isSelected) 100f else 1000f
                            )
                        )
                        val verticalPadding by animateDpAsState(
                            (maxBorder / LocalDensity.current.density / 14).dp
                        )
                        val contentColor by animateColorAsState(
                            if (isSelected) Color.White else Color.Black
                        )

                        Column(
                            modifier = modifier
                                .onSizeChanged { maxBorder = if (it.height > it.width) it.height else it.width }
                                .padding(verticalPadding)
                                .pointerHoverIcon(PointerIconDefaults.Hand)
                                .hoverable(interactionSource, !isSelected)
                                .clip(shape)
                                .background(hoverColor)
                                .border(
                                    width = if(isSelected) 0.2.dp else 1.2.dp,
                                    color = Color.Black.copy(0.1f),
                                    shape = shape
                                )
                                .drawBehind {
                                    if(isHover){
                                        drawRect(color = Color.Black.copy(0.1f))
                                    }
                                    drawCircle(primaryColor, radius = radius, center = offset)
                                }
                                .onPointerEvent(eventType = PointerEventType.Press, onEvent = { event ->
                                    offset = event.changes[0].position
                                })
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { state.selectDate(date) })
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .alpha(if (isInThatMonth) 1f else .1f),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            CompositionLocalProvider(LocalContentColor provides contentColor) {
                                content()
                            }

                        }
                    }
            }
        }
    }

}

interface DayPickerScope {
    val isSelected: Boolean
    val isHover: Boolean
    val date: LocalDate
}
