package com.abdownloadmanager.desktop.pages.settings.configurable

import com.abdownloadmanager.shared.utils.ui.widget.MyIcon
import com.abdownloadmanager.shared.utils.ui.icon.MyIcons
import com.abdownloadmanager.shared.utils.ui.myColors
import com.abdownloadmanager.shared.utils.ui.theme.myTextSizes
import ir.amirab.util.ifThen
import com.abdownloadmanager.shared.utils.div
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import com.abdownloadmanager.shared.ui.widget.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.abdownloadmanager.desktop.utils.configurable.Configurable
import com.abdownloadmanager.resources.Res
import ir.amirab.util.compose.StringSource
import ir.amirab.util.compose.asStringSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.DayOfWeek

class DayOfWeekConfigurable(
    title: StringSource,
    description: StringSource,
    backedBy: MutableStateFlow<Set<DayOfWeek>>,
    describe: (Set<DayOfWeek>) -> StringSource,
    validate: (Set<DayOfWeek>) -> Boolean,
    enabled: StateFlow<Boolean> = DefaultEnabledValue,
    visible: StateFlow<Boolean> = DefaultVisibleValue,
) : Configurable<Set<DayOfWeek>>(
    title = title,
    description = description,
    backedBy = backedBy,
    describe = describe,
    validate = validate,
    enabled = enabled,
    visible = visible,
) {
    @Composable
    override fun render(modifier: Modifier) {
        RenderDayOfWeekConfigurable(this, modifier)
    }
}

@Composable
private fun RenderDayOfWeekConfigurable(cfg: DayOfWeekConfigurable, modifier: Modifier) {
    val value by cfg.stateFlow.collectAsState()
    val setValue = cfg::set
    val allDays = DayOfWeek.entries.toSet()
    val enabled = isConfigEnabled()
    fun isSelected(dayOfWeek: DayOfWeek): Boolean {
        return dayOfWeek in value
    }

    fun selectDay(dayOfWeek: DayOfWeek, select: Boolean) {
        if (!enabled) return
        if (select) {
            setValue(
                value.plus(dayOfWeek).sorted().toSet()
            )
        } else {
            setValue(
                value.minus(dayOfWeek).sorted().toSet()
            )
        }
    }
    ConfigTemplate(
        modifier = modifier,
        title = {
            TitleAndDescription(cfg, true)
        },
        value = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    Modifier.ifThen(!enabled) {
                        alpha(0.5f)
                    }
                ) {
                    FlowRow(Modifier.fillMaxWidth()) {
                        allDays.forEach { dayOfWeek ->
                            RenderDayOfWeek(
                                modifier = Modifier,
                                enabled = enabled,
                                dayOfWeek = dayOfWeek,
                                selected = isSelected(dayOfWeek),
                                onSelect = { s, isSelected ->
                                    selectDay(dayOfWeek, isSelected)
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun RenderDayOfWeek(
    modifier: Modifier,
    dayOfWeek: DayOfWeek,
    selected: Boolean,
    onSelect: (DayOfWeek, Boolean) -> Unit,
    enabled: Boolean = true,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(2.dp)
            .clip(CircleShape)
            .ifThen(selected) {
                background(myColors.onBackground / 10)
            }
            .clickable(enabled = enabled) {
                onSelect(dayOfWeek, !selected)
            }
            .padding(vertical = 4.dp)
            .padding(horizontal = 8.dp)

    ) {
        MyIcon(
            MyIcons.check,
            null,
            Modifier.size(10.dp)
                .alpha(if (selected) 1f else 0f),
        )
        Spacer(Modifier.width(2.dp))
        Text(
            text = dayOfWeek.asStringSource().rememberString(),
            modifier = Modifier.alpha(
                if (selected) 1f
                else 0.5f
            ),
            softWrap = false,
            fontSize = myTextSizes.base,
        )
    }
}

private fun DayOfWeek.asStringSource() = when (this) {
    DayOfWeek.MONDAY -> Res.string.monday
    DayOfWeek.TUESDAY -> Res.string.tuesday
    DayOfWeek.WEDNESDAY -> Res.string.wednesday
    DayOfWeek.THURSDAY -> Res.string.thursday
    DayOfWeek.FRIDAY -> Res.string.friday
    DayOfWeek.SATURDAY -> Res.string.saturday
    DayOfWeek.SUNDAY -> Res.string.sunday
}.asStringSource()
