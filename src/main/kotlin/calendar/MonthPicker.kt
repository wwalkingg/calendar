package calendar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import java.time.DateTimeException
import java.time.Month
import java.time.Year

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MonthPicker(
    modifier: Modifier = Modifier,
    state: CalendarState,
    contentColor: Color,
    primaryColor: Color,
    hoverColor: Color,
    errorColor: Color,
) {

    var yearValue by remember {
        mutableStateOf("")
    }
    var monthValue by remember {
        mutableStateOf("")
    }
    LaunchedEffect(state.yearOnPanel, state.monthOnPanel) {
        yearValue = state.yearOnPanel.toString()
        monthValue = state.get2DigitMonth()
    }
    val style = TextStyle(
        color = contentColor,
        fontSize = 40.sp,
        fontWeight = FontWeight(700),
        fontFeatureSettings = "tnum"
    )

    var _yearWidth by remember {
        mutableStateOf(0)
    }
    val yearWidth = _yearWidth / LocalDensity.current.density
    var _monthWidth by remember {
        mutableStateOf(0)
    }
    val monthWidth = _monthWidth / LocalDensity.current.density
    // year
    var isYearEditable by remember { mutableStateOf(false) }
    val yearInteractionSource = remember { MutableInteractionSource() }
    val isYearHover by yearInteractionSource.collectIsHoveredAsState()
    var isYearError by remember { mutableStateOf(false) }
    // month
    var isMonthEditable by remember { mutableStateOf(false) }
    val monthInteractionSource = remember { MutableInteractionSource() }
    val isMonthHover by monthInteractionSource.collectIsHoveredAsState()
    var isMonthError by remember { mutableStateOf(false) }

    val yearBackgroundColor by animateColorAsState(
        if (isYearEditable) {
            if (isYearError) errorColor else primaryColor
        } else if (isYearHover) hoverColor else hoverColor.copy(0f)
    )

    val monthBackgroundColor by animateColorAsState(
        if (isMonthEditable) {
            if (isMonthError) errorColor else primaryColor
        } else if (isMonthHover) hoverColor else hoverColor.copy(0f)
    )
    Column {
        Box(
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            Row(
                modifier
            ) {
                BasicTextField(
                    value = yearValue,
                    onValueChange = {
                        // 不能超过4位
                        if (it.length <= 4) yearValue = it
                        isYearError = try {
                            Year.of(it.toInt())
                            false
                        } catch (e: DateTimeException) {
                            true
                        } catch (e: NumberFormatException) {
                            true
                        }
                    },
                    textStyle = style,
                    singleLine = true,
                    cursorBrush = SolidColor(contentColorFor(primaryColor)),
                    modifier = Modifier
                        .hoverable(yearInteractionSource)
                        .onPreviewKeyEvent {
                            if (it.key == Key.Enter) {
                                // change date
                                if (!isMonthError && !isYearError) {
                                    state.yearOnPanel = yearValue.toInt()
                                    state.monthOnPanel = monthValue.toInt()
                                    true
                                } else false
                            } else false
                        }
                        .onFocusChanged {
                            isYearEditable = it.isFocused
                        }
                        .padding(horizontal = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(yearBackgroundColor)
                        .padding(vertical = 10.dp, horizontal = 20.dp)
                        .width(yearWidth.dp)
                        .zIndex(if (isYearEditable) 9f else 0f),

                    )
                //center
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(IntrinsicSize.Max)
                        .padding(vertical = 10.dp)
                        .offset(x = (-30).dp)
                        .zIndex(10f)
                ) {
                    Text(
                        "/", style = style,
                        modifier = Modifier
                            .alpha(if (isYearEditable || isMonthEditable) 0f else 1f)
                    )
                    // icon
                    Column(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight().alpha(
                            if (isYearEditable || isMonthEditable) 1f else 0f
                        )
                    ) {
                        Icon(
                            painterResource("/svg/scroll_vertical.svg"),
                            null, modifier = Modifier.size(20.dp)
                                .weight(1f)
                                .testTag("你可以通过滚轮选择年月")
                        )
                        Icon(
                            painterResource("/svg/enter.svg"),
                            null, modifier = Modifier.size(20.dp)
                                .weight(1f)
                                .testTag("按下回车确定修改")
                        )
                    }
                }

                BasicTextField(
                    value = monthValue.toString(),
                    onValueChange = {
                        // 不能超过2位
                        if (it.length <= 2) monthValue = it
                        isMonthError = try {
                            Month.of(it.toInt())
                            false
                        } catch (e: DateTimeException) {
                            true
                        } catch (e: NumberFormatException) {
                            true
                        }

                    },
                    textStyle = style,
                    singleLine = true,
                    cursorBrush = SolidColor(contentColorFor(primaryColor)),
                    modifier = Modifier
                        .offset(x = (-60).dp)
                        .hoverable(monthInteractionSource)
                        .onPreviewKeyEvent {
                            if (it.key == Key.Enter) {
                                // change date
                                if (!isMonthError && !isYearError) {
                                    state.yearOnPanel = yearValue.toInt()
                                    state.monthOnPanel = monthValue.toInt()
                                    true
                                } else false
                            } else false

                        }
                        .onFocusChanged {
                            isMonthEditable = it.isFocused
                        }
                        .padding(horizontal = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(monthBackgroundColor)
                        .padding(vertical = 10.dp, horizontal = 20.dp)
                        .width(monthWidth.dp)
                        .zIndex(if (isMonthEditable) 9f else 0f)
                )
            }
            Text("8888", style = style, modifier = Modifier.onSizeChanged {
                _yearWidth = it.width
            }.alpha(0f))
            Text("88", style = style, modifier = Modifier.onSizeChanged {
                _monthWidth = it.width
            }.alpha(0f))
        }
    }

}