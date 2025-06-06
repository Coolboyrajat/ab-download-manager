package com.abdownloadmanager.desktop.pages.filehash

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.abdownloadmanager.desktop.pages.settings.configurable.FileChecksumConfigurable
import com.abdownloadmanager.desktop.pages.settings.configurable.RenderSpinner
import com.abdownloadmanager.desktop.utils.ClipboardUtil
import com.abdownloadmanager.desktop.utils.configurable.RenderConfigurable
import com.abdownloadmanager.desktop.window.custom.WindowTitle
import com.abdownloadmanager.resources.Res
import com.abdownloadmanager.shared.ui.widget.ActionButton
import com.abdownloadmanager.shared.ui.widget.Help
import com.abdownloadmanager.shared.ui.widget.Text
import com.abdownloadmanager.shared.ui.widget.customtable.*
import com.abdownloadmanager.shared.ui.widget.customtable.styled.MyStyledTableHeader
import com.abdownloadmanager.shared.ui.widget.menu.custom.WithContextMenu
import com.abdownloadmanager.shared.ui.widget.menu.custom.MyDropDown
import com.abdownloadmanager.shared.utils.FileChecksum
import com.abdownloadmanager.shared.utils.FileChecksumAlgorithm
import com.abdownloadmanager.shared.utils.div
import com.abdownloadmanager.shared.utils.rememberDotLoading
import com.abdownloadmanager.shared.utils.ui.WithContentColor
import com.abdownloadmanager.shared.utils.ui.icon.MyIcons
import com.abdownloadmanager.shared.utils.ui.myColors
import com.abdownloadmanager.shared.utils.ui.theme.myTextSizes
import com.abdownloadmanager.shared.utils.ui.widget.MyIcon
import ir.amirab.util.compose.IconSource
import ir.amirab.util.compose.StringSource
import ir.amirab.util.compose.action.MenuItem
import ir.amirab.util.compose.action.buildMenu
import ir.amirab.util.compose.asStringSource
import ir.amirab.util.compose.resources.myStringResource
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun FileChecksumPage(component: FileChecksumComponent) {
    WindowTitle(myStringResource(Res.string.file_checksum_page))
    val horizontalPadding = 16.dp
    Column {
        Table(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            list = component.state.collectAsState().value.items,
            tableState = remember {
                TableState(FileChecksumTableCells.cells)
            },
            wrapHeader = {
                MyStyledTableHeader(
                    itemHorizontalPadding = horizontalPadding,
                    content = it,
                )
            },
            wrapItem = { index, item, content ->
                Box(Modifier.padding(horizontal = horizontalPadding).let {
                    val mutableInteractionSource = remember { MutableInteractionSource() }
                    it.indication(mutableInteractionSource, LocalIndication.current)
                        .hoverable(mutableInteractionSource)
                }) {
                    content()
                }
            },
            renderCell = { cell, item ->
                when (cell) {
                    FileChecksumTableCells.Name -> {
                        FileChecksumTableCellRenderers.RenderName(item)
                    }

                    FileChecksumTableCells.Status -> {
                        FileChecksumTableCellRenderers.RenderStatus(item)
                    }

                    FileChecksumTableCells.Algorithm -> {
                        FileChecksumTableCellRenderers.RenderAlgorithm(item)
                    }

                    FileChecksumTableCells.CalculatedChecksum -> {
                        FileChecksumTableCellRenderers.RenderCalculatedChecksum(item)
                    }

                    FileChecksumTableCells.SavedChecksum -> {
                        FileChecksumTableCellRenderers.RenderSavedChecksum(
                            item = item,
                            onRequestSaveNewChecksum = {
                                component.updateChecksum(item.downloadItem.id, it)
                            }
                        )
                    }
                }
            })
        Actions(
            Modifier,
            component,
        )
    }
}

@Composable
private fun Actions(
    modifier: Modifier,
    component: FileChecksumComponent,
) {
    val uiState by component.state.collectAsState()
    Column(modifier) {
        Spacer(
            Modifier.fillMaxWidth().height(1.dp).background(myColors.onBackground / 0.15f)
        )
        Row(
            Modifier.fillMaxWidth().background(myColors.surface / 0.5f).padding(horizontal = 16.dp)
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = myStringResource(Res.string.file_checksum_page_file_checksum_default_algorithm)
                    )
                    Spacer(Modifier.width(8.dp))
                    Help(
                        myStringResource(Res.string.file_checksum_page_file_checksum_default_algorithm_help)
                    )
                }
                Spacer(Modifier.size(8.dp))
                RenderSpinner(
                    modifier = Modifier,
                    possibleValues = FileChecksumAlgorithm.all(),
                    value = uiState.defaultAlgorithm,
                    enabled = !uiState.isChecking,
                    onSelect = {
                        component.onAlgorithmChange(it)
                    },
                    render = {
                        Text(it.algorithm)
                    })
            }
            Spacer(Modifier.weight(1f))
            Row {
                ActionButton(
                    myStringResource(Res.string.start),
                    onClick = component::onRequestStartCheck,
                    enabled = !uiState.isChecking
                )
                Spacer(Modifier.width(8.dp))
                ActionButton(
                    myStringResource(Res.string.close),
                    onClick = component::onRequestClose,
                )
            }
        }
    }

}


private data object FileChecksumTableCellRenderers {
    private val itemVerticalPadding = 8.dp

    @Composable
    private fun CellContent(
        content: @Composable () -> Unit
    ) {
        Box(
            modifier = Modifier.padding(vertical = itemVerticalPadding)
        ) {
            content()
        }
    }

    @Composable
    fun RenderName(item: DownloadItemWithChecksum) {
        CellContent {
            SimpleText(item.downloadItem.name)
        }
    }

    @Composable
    fun RenderStatus(item: DownloadItemWithChecksum) {
        CellContent {
            when (val status = item.checksumStatus) {
                is ChecksumStatus.Checking -> {
                    RenderCheckingStatus(status.percent)
                }

                ChecksumStatus.Error.DownloadNotFinished -> {
                    RenderErrorStatus(myStringResource(Res.string.download_not_finished))
                }

                is ChecksumStatus.Error.Exception -> {
                    RenderErrorStatus(status.t.localizedMessage ?: status.t::class.simpleName.orEmpty())
                }

                ChecksumStatus.Error.FileNotFound -> {
                    RenderErrorStatus(myStringResource(Res.string.file_not_found))
                }

                is ChecksumStatus.Finished -> {
                    RenderFinishedStatus(
                        status = status,
                    )
                }

                ChecksumStatus.Waiting -> {
                    RenderWaitingStatus()
                }
            }
        }
    }

    @Composable
    fun RenderAlgorithm(item: DownloadItemWithChecksum) {
        CellContent {
            SimpleText(item.algorithm)
        }
    }

    @Composable
    fun RenderCalculatedChecksum(item: DownloadItemWithChecksum) {
        WithContextMenu(
            menuProvider = {
                buildMenu {
                    if (item.calculatedChecksum != null) {
                        item(
                            title = Res.string.copy.asStringSource(),
                            icon = MyIcons.copy,
                            onClick = {
                                ClipboardUtil.copy(item.calculatedChecksum)
                            }
                        )
                    }
                }
            }
        ) {
            if (item.calculatedChecksum != null) {
                SimpleText(item.calculatedChecksum)
            } else if (item.isProcessing) {
                //shimmer
                ShimmerEffect(
                    centerColor = myColors.onBackground / 0.4f,
                    surroundingColor = myColors.onBackground / 0.1f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .height(myTextSizes.base.value.dp)
                )
            } else if (item.isError) {
                SimpleText("!")
            }
        }
    }

    @Composable
    fun RenderSavedChecksum(
        item: DownloadItemWithChecksum,
        onRequestSaveNewChecksum: (FileChecksum?) -> Unit
    ) {
        fun createMenu(
            item: DownloadItemWithChecksum,
            onRequestEdit: (Boolean) -> Unit,
        ): List<MenuItem> {
            return buildMenu {
                if (item.savedChecksum != null) {
                    item(
                        title = Res.string.copy.asStringSource(),
                        icon = MyIcons.copy,
                        onClick = {
                            ClipboardUtil.copy(item.savedChecksum)
                        },
                    )
                }
                item(
                    title = Res.string.edit.asStringSource(),
                    icon = MyIcons.edit,
                    onClick = {
                        onRequestEdit(true)
                    },
                )
            }
        }

        var edit by remember { mutableStateOf(false) }
        WithContextMenu(
            menuProvider = {
                createMenu(
                    item = item,
                    onRequestEdit = { edit = it }
                )
            }
        ) {
            Column(Modifier.fillMaxSize()) {
                CellContent {
                    SimpleText(item.savedChecksum.orEmpty())
                }
                if (edit) {
                    ChecksumEditDropDown(
                        item = item,
                        onRequestSaveNewChecksum = {
                            onRequestSaveNewChecksum(it)
                            edit = false
                        },
                        onCloseRequest = {
                            edit = false
                        },
                    )
                }
            }
        }
    }

    @Composable
    private fun ChecksumEditDropDown(
        item: DownloadItemWithChecksum,
        onCloseRequest: () -> Unit,
        onRequestSaveNewChecksum: (FileChecksum?) -> Unit,
    ) {
        val editChecksumFlow = remember {
            MutableStateFlow<FileChecksum?>(FileChecksum(item.algorithm, item.savedChecksum.orEmpty()))
        }
        val fileChecksumConfigurable = remember {
            FileChecksumConfigurable(
                title = Res.string.download_item_settings_file_checksum.asStringSource(),
                description = Res.string.download_item_settings_file_checksum_description.asStringSource(),
                backedBy = editChecksumFlow,
                describe = {
                    "".asStringSource()
                },
            )
        }
        MyDropDown(
            onDismissRequest = onCloseRequest,
            anchor = Alignment.BottomEnd,
            alignment = Alignment.BottomStart,
        ) {
            val shape = RoundedCornerShape(6.dp)
            Column(
                Modifier
                    .shadow(24.dp)
                    .clip(shape)
                    .width(350.dp)
                    .border(1.dp, myColors.surface, shape)
                    .background(myColors.menuGradientBackground)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                RenderConfigurable(fileChecksumConfigurable, Modifier)
                ActionButton(
                    text = myStringResource(Res.string.update),
                    modifier = Modifier
                        .align(Alignment.End),
                    onClick = {
                        val newChecksum = editChecksumFlow.value
                        onRequestSaveNewChecksum(
                            newChecksum.takeIf { it?.value?.isNotEmpty() ?: false }
                        )
                    }
                )
            }
        }
    }

    @Composable
    private fun ShimmerEffect(
        modifier: Modifier = Modifier,
        centerColor: Color = Color.Gray,
        surroundingColor: Color = Color.Gray,
    ) {
        val transition = rememberInfiniteTransition()
        val translateAnim = transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 3000,
                    easing = LinearEasing
                )
            )
        )

        val brush = Brush.linearGradient(
            colors = listOf(
                surroundingColor,
                centerColor,
                surroundingColor,
            ),
            start = Offset(0f, 0f),
            end = Offset(translateAnim.value, 0f)
        )

        Box(
            modifier = modifier
                .background(brush = brush)
        )
    }

    @Composable
    private fun RenderErrorStatus(message: String) {
        IconWithText(
            icon = MyIcons.info,
            text = message,
            color = myColors.error,
        )
    }

    @Composable
    private fun RenderFinishedStatus(
        status: ChecksumStatus.Finished,
    ) {
        val text: StringSource
        val color: Color
        val icon: IconSource
        when (status) {
            ChecksumStatus.Finished.Done -> {
                text = Res.string.done.asStringSource()
                icon = MyIcons.check
                color = myColors.info
            }

            ChecksumStatus.Finished.Matches -> {
                text = Res.string.matches.asStringSource()
                icon = MyIcons.check
                color = myColors.success
            }

            ChecksumStatus.Finished.NotMatches -> {
                text = Res.string.not_matches.asStringSource()
                icon = MyIcons.info
                color = myColors.warning
            }
        }
        IconWithText(
            icon = icon,
            text = text.rememberString(),
            color = color,
        )
    }

    @Composable
    private fun IconWithText(
        icon: IconSource,
        text: String,
        color: Color,
    ) {
        WithContentColor(color) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MyIcon(
                    icon,
                    modifier = Modifier.size(16.dp),
                    contentDescription = null,
                )
                Spacer(Modifier.width(2.dp))
                SimpleText(text)
            }
        }
    }

    @Composable
    private fun RenderCheckingStatus(percent: Int) {
        Column {
            ProgressStatus(percent, myColors.primaryGradient)
        }
    }

    @Composable
    private fun RenderWaitingStatus() {
        Row {
            SimpleText("${myStringResource(Res.string.waiting)} ${rememberDotLoading()}")
        }
    }

    @Composable
    private fun ProgressStatus(
        percent: Int?,
        background: Brush = myColors.primaryGradient,
    ) {
        Box(
            Modifier.fillMaxWidth().clip(CircleShape).background(myColors.surface)
        ) {
            if (percent != null) {
                val w = (percent / 100f).coerceIn(0f..1f)
                Spacer(
                    Modifier.height(5.dp).fillMaxWidth(
                        animateFloatAsState(
                            w, tween(100)
                        ).value
                    ).background(background)
                )
            }
        }
    }

    @Composable
    private fun SimpleText(string: String, modifier: Modifier = Modifier) {
        Text(
            string,
            modifier = modifier,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private sealed class FileChecksumTableCells : TableCell<DownloadItemWithChecksum> {
    data object Name : FileChecksumTableCells() {
        override val id: String = "name"
        override val name: StringSource = Res.string.name.asStringSource()
        override val size: CellSize = CellSize.Resizeable(100.dp..1000.dp, 300.dp)
    }

    data object Status : FileChecksumTableCells() {
        override val id: String = "status"
        override val name: StringSource = Res.string.status.asStringSource()
        override val size: CellSize = CellSize.Resizeable(100.dp..1000.dp, 150.dp)
    }

    data object Algorithm : FileChecksumTableCells() {
        override val id: String = "algorithm"
        override val name: StringSource = Res.string.checksum_algorithm.asStringSource()
        override val size: CellSize = CellSize.Resizeable(60.dp..300.dp, 60.dp)
    }

    data object SavedChecksum : FileChecksumTableCells() {
        override val id: String = "saved_checksum"
        override val name: StringSource = Res.string.saved_checksum.asStringSource()
        override val size: CellSize = CellSize.Resizeable(100.dp..1000.dp, 150.dp)
    }

    data object CalculatedChecksum : FileChecksumTableCells() {
        override val id: String = "calculated_checksum"
        override val name: StringSource = Res.string.calculated_checksum.asStringSource()
        override val size: CellSize = CellSize.Resizeable(100.dp..1000.dp, 150.dp)
    }

    companion object {
        val cells = listOf(
            Name,
            Status,
            Algorithm,
            CalculatedChecksum,
            SavedChecksum,
        )
    }
}
