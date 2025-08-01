package com.abdownloadmanager.desktop

import ir.amirab.util.desktop.poweraction.PowerActionConfig
import com.abdownloadmanager.desktop.pages.addDownload.AddDownloadComponent
import com.abdownloadmanager.desktop.pages.addDownload.AddDownloadConfig
import com.abdownloadmanager.desktop.pages.addDownload.ImportOptions
import com.abdownloadmanager.desktop.pages.addDownload.multiple.AddMultiDownloadComponent
import com.abdownloadmanager.desktop.pages.addDownload.single.AddSingleDownloadComponent
import com.abdownloadmanager.desktop.pages.batchdownload.BatchDownloadComponent
import com.abdownloadmanager.desktop.pages.category.CategoryComponent
import com.abdownloadmanager.desktop.pages.category.CategoryDialogManager
import com.abdownloadmanager.desktop.pages.editdownload.EditDownloadComponent
import com.abdownloadmanager.desktop.pages.filehash.FileChecksumComponent
import com.abdownloadmanager.desktop.pages.filehash.FileChecksumComponentConfig
import com.abdownloadmanager.desktop.pages.home.HomeComponent
import com.abdownloadmanager.desktop.pages.queue.QueuesComponent
import com.abdownloadmanager.desktop.pages.settings.SettingsComponent
import com.abdownloadmanager.desktop.pages.poweractionalert.PowerActionComponent
import com.abdownloadmanager.desktop.pages.singleDownloadPage.SingleDownloadComponent
import com.abdownloadmanager.desktop.pages.updater.UpdateComponent
import com.abdownloadmanager.desktop.repository.AppRepository
import com.abdownloadmanager.desktop.storage.AppSettingsStorage
import com.abdownloadmanager.desktop.ui.widget.MessageDialogModel
import com.abdownloadmanager.desktop.ui.widget.MessageDialogType
import com.abdownloadmanager.shared.ui.widget.NotificationModel
import com.abdownloadmanager.shared.ui.widget.NotificationType
import com.abdownloadmanager.desktop.utils.*
import com.abdownloadmanager.shared.utils.mvi.ContainsEffects
import com.abdownloadmanager.shared.utils.mvi.supportEffects
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.children.ChildNavState
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.navigate
import com.arkivanov.decompose.router.slot.*
import ir.amirab.downloader.DownloadManagerEvents
import ir.amirab.downloader.downloaditem.DownloadCredentials
import ir.amirab.downloader.downloaditem.DownloadItem
import ir.amirab.downloader.downloaditem.contexts.ResumedBy
import ir.amirab.downloader.downloaditem.contexts.User
import ir.amirab.downloader.queue.DefaultQueueInfo
import ir.amirab.downloader.utils.ExceptionUtils
import ir.amirab.downloader.utils.OnDuplicateStrategy
import com.abdownloadmanager.integration.Integration
import com.abdownloadmanager.integration.IntegrationResult
import com.abdownloadmanager.resources.*
import com.abdownloadmanager.shared.utils.BaseComponent
import com.abdownloadmanager.shared.utils.DownloadItemOpener
import com.abdownloadmanager.shared.utils.DownloadSystem
import com.abdownloadmanager.shared.utils.category.CategoryManager
import com.abdownloadmanager.shared.utils.category.CategorySelectionMode
import com.abdownloadmanager.shared.utils.subscribeAsStateFlow
import com.arkivanov.decompose.childContext
import ir.amirab.downloader.destination.IncompleteFileUtil
import ir.amirab.downloader.downloaditem.DownloadStatus
import ir.amirab.downloader.downloaditem.withCredentials
import ir.amirab.downloader.exception.TooManyErrorException
import ir.amirab.downloader.monitor.isDownloadActiveFlow
import ir.amirab.util.compose.StringSource
import ir.amirab.util.compose.asStringSource
import ir.amirab.util.compose.combineStringSources
import ir.amirab.util.flow.mapStateFlow
import ir.amirab.util.osfileutil.FileUtils
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.Toolkit
import kotlin.system.exitProcess

sealed interface AppEffects {
    data class SimpleNotificationNotification(
        val notificationModel: NotificationModel,
    ) : AppEffects
}

interface NotificationSender {
    fun sendDialogNotification(title: StringSource, description: StringSource, type: MessageDialogType)
    fun sendNotification(tag: Any, title: StringSource, description: StringSource, type: NotificationType)
}

class AppComponent(
    ctx: ComponentContext,
) : BaseComponent(ctx),
    DownloadDialogManager,
    AddDownloadDialogManager,
    CategoryDialogManager,
    EditDownloadDialogManager,
    FileChecksumDialogManager,
    QueuePageManager,
    NotificationSender,
    DownloadItemOpener,
    PowerActionManager,
    ContainsEffects<AppEffects> by supportEffects(),
    KoinComponent {
    val appRepository: AppRepository by inject()
    val appSettings: AppSettingsStorage by inject()
    private val integration: Integration by inject()
    val useSystemTray = appSettings.useSystemTray

    fun openHome() {
        scope.launch {
            showHomeSlot.value.child?.instance.let {
                if (it != null) {
                    it.bringToFront()
                } else {
                    showHome.activate(HomePageConfig())
                }
            }
        }
    }

    fun activateHomeIfNotOpen() {
        scope.launch {
            showHomeSlot.value.child?.instance.let {
                if (it == null) {
                    showHome.activate(HomePageConfig())
                }
            }
        }
    }

    fun closeHome() {
        scope.launch {
            showHome.dismiss()
        }
    }

    @Serializable
    class HomePageConfig

    private val showHome = SlotNavigation<HomePageConfig>()
    val showHomeSlot = childSlot(
        showHome,
        serializer = null,
        key = "home",
        childFactory = { _: HomePageConfig, componentContext: ComponentContext ->
            HomeComponent(
                ctx = componentContext,
                downloadItemOpener = this,
                downloadDialogManager = this,
                addDownloadDialogManager = this,
                fileChecksumDialogManager = this,
                categoryDialogManager = this,
                notificationSender = this,
                editDownloadDialogManager = this,
                queuePageManager = this,
            )
        }
    ).subscribeAsStateFlow()

    class QueuePageConfig(
        val selectedQueue: Long? = null
    )

    private val showQueues = SlotNavigation<QueuePageConfig>()
    val showQueuesSlot = childSlot(
        showQueues,
        serializer = null,
        key = "queues",
        childFactory = { config: QueuePageConfig, componentContext: ComponentContext ->
            QueuesComponent(componentContext, this::closeQueues).apply {
                config.selectedQueue?.let {
                    onQueueSelected(it)
                }
            }
        }
    ).subscribeAsStateFlow()

    class BatchDownloadConfig

    private val batchDownload = SlotNavigation<BatchDownloadConfig>()
    val batchDownloadSlot = childSlot(
        batchDownload,
        serializer = null,
        key = "batchDownload",
        childFactory = { _: BatchDownloadConfig, componentContext: ComponentContext ->
            BatchDownloadComponent(
                ctx = componentContext,
                onClose = this::closeBatchDownload,
                importLinks = {
                    openAddDownloadDialog(it.map {
                        DownloadCredentials(
                            link = it
                        )
                    })
                }
            )
        }
    ).subscribeAsStateFlow()

    private val editDownload = SlotNavigation<Long>()
    val editDownloadSlot = childSlot(
        editDownload,
        serializer = null,
        key = "editDownload",
        childFactory = { editDownloadConfig: Long, componentContext: ComponentContext ->
            EditDownloadComponent(
                ctx = componentContext,
                onRequestClose = {
                    closeEditDownloadDialog()
                },
                onEdited = { updater ->
                    scope.launch {
                        downloadSystem.editDownload(editDownloadConfig, updater)
                        closeEditDownloadDialog()
                    }
                },
                downloadId = editDownloadConfig,
                acceptEdit = downloadSystem.downloadMonitor
                    .isDownloadActiveFlow(editDownloadConfig)
                    .mapStateFlow { !it },
            )
        }
    ).subscribeAsStateFlow()

    override fun openEditDownloadDialog(id: Long) {
        val currentComponent = editDownloadSlot.value.child?.instance
        if (currentComponent != null && currentComponent.downloadId == id) {
            currentComponent.bringToFront()
        } else {
            editDownload.activate(id)
        }
    }

    override fun closeEditDownloadDialog() {
        editDownload.dismiss()
    }

    fun openSettings() {
        scope.launch {
            showSettingSlot.value.child?.instance.let {
                if (it != null) {
                    it.toFront()
                } else {
                    showSettingWindow.activate(AppSettingPageConfig())
                }

            }
        }
    }

    fun closeSettings() {
        scope.launch {
            showSettingWindow.dismiss()
        }
    }

    class AppSettingPageConfig

    val showSettingWindow = SlotNavigation<AppSettingPageConfig>()
    val showSettingSlot = childSlot(
        showSettingWindow,
        serializer = null,
        key = "settings",
        childFactory = { configuration: AppSettingPageConfig, componentContext: ComponentContext ->
            SettingsComponent(componentContext)
        }
    ).subscribeAsStateFlow()


    val downloadSystem: DownloadSystem by inject()
    private val addDownloadPageControl = PagesNavigation<AddDownloadConfig>()
    val _openedAddDownloadDialogs = childPages(
        key = "openedAddDownloadDialogs",
        source = addDownloadPageControl,
        serializer = null,
        initialPages = { Pages() },

        pageStatus = { _, _ ->
            ChildNavState.Status.RESUMED
        },
        childFactory = { config, ctx ->
            val component: AddDownloadComponent = when (config) {
                is AddDownloadConfig.SingleAddConfig -> {
                    AddSingleDownloadComponent(
                        ctx = ctx,
                        onRequestClose = {
                            closeAddDownloadDialog(config.id)
                        },
                        onRequestAddToQueue = { item, queueId, onDuplicate, categoryId ->
                            addDownload(
                                item = item,
                                queueId = queueId,
                                categoryId = categoryId,
                                onDuplicateStrategy = onDuplicate,
                            )
                        },
                        onRequestAddCategory = {
                            openCategoryDialog(-1)
                        },
                        onRequestDownload = { item, onDuplicate, categoryId ->
                            startNewDownload(
                                item = item,
                                onDuplicateStrategy = onDuplicate,
                                categoryId = categoryId,
                            )
                            closeAddDownloadDialog(config.id)
                        },
                        openExistingDownload = {
                            openDownloadDialog(it)
                            closeAddDownloadDialog(config.id)
                        },
                        downloadItemOpener = this,
                        updateExistingDownloadCredentials = { id, newCredentials ->
                            scope.launch {
                                downloadSystem.downloadManager.updateDownloadItem(id) {
                                    it.withCredentials(newCredentials)
                                }
                                closeAddDownloadDialog(config.id)
                                openDownloadDialog(id)
                            }
                        },
                        id = config.id,
                        importOptions = config.importOptions
                    ).also {
                        it.setCredentials(config.credentials)
                    }
                }

                is AddDownloadConfig.MultipleAddConfig -> {
                    AddMultiDownloadComponent(
                        ctx = ctx,
                        id = config.id,
                        onRequestClose = { closeAddDownloadDialog(config.id) },
                        onRequestAdd = { items, strategy, queueId, categorySelectionMode ->
                            addDownloads(
                                items = items,
                                onDuplicateStrategy = strategy,
                                queueId = queueId,
                                categorySelectionMode = categorySelectionMode
                            )
                        },
                        onRequestAddCategory = {
                            openCategoryDialog(-1)
                        },
                    ).apply { addItems(config.links) }
                }

                else -> error("should not happened")
            }
            component
        }
    ).subscribeAsStateFlow()
    override val openedAddDownloadDialogs = _openedAddDownloadDialogs.map {
        it.items.mapNotNull { it.instance }
    }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    private val downloadDialogControl = PagesNavigation<SingleDownloadComponent.Config>()

    private val _openedDownloadDialogs = childPages(
        key = "openedDownloadDialogs",
        source = downloadDialogControl,
        serializer = null,
        initialPages = { Pages() },
        pageStatus = { _, _ ->
            ChildNavState.Status.RESUMED
        },
        childFactory = { cfg, ctx ->
            SingleDownloadComponent(
                ctx = ctx,
                downloadItemOpener = this,
                onDismiss = {
                    closeDownloadDialog(cfg.id)
                },
                downloadId = cfg.id,
            )
        }
    ).subscribeAsStateFlow()

    override val openedDownloadDialogs = _openedDownloadDialogs
        .map { it.items.mapNotNull { it.instance } }
        .stateIn(scope, SharingStarted.Eagerly, emptyList())

    private val categoryManager: CategoryManager by inject()

    private val categoryPageControl = PagesNavigation<Long>()
    private val _openedCategoryDialogs = childPages(
        key = "openedCategoryDialogs",
        source = categoryPageControl,
        serializer = null,
        initialPages = { Pages() },
        pageStatus = { _, _ ->
            ChildNavState.Status.RESUMED
        },
        childFactory = { cfg, ctx ->
            CategoryComponent(
                ctx = ctx,
                close = {
                    closeCategoryDialog(cfg)
                },
                submit = { submittedCategory ->
                    if (submittedCategory.id < 0) {
                        categoryManager.addCustomCategory(submittedCategory)
                    } else {
                        categoryManager.updateCategory(
                            submittedCategory.id
                        ) {
                            submittedCategory.copy(
                                items = it.items
                            )
                        }
                    }
                    closeCategoryDialog(cfg)
                },
                id = cfg
            )
        }
    ).subscribeAsStateFlow()
    override val openedCategoryDialogs: StateFlow<List<CategoryComponent>> = _openedCategoryDialogs
        .map {
            it.items.mapNotNull { it.instance }
        }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    override fun openCategoryDialog(categoryId: Long) {
        scope.launch {
            val component = openedCategoryDialogs.value.find {
                it.id == categoryId
            }
            if (component != null) {
//                component.bringToFront()
            } else {
                categoryPageControl.navigate {
                    val newItems = (it.items.toSet() + categoryId).toList()
                    val copy = it.copy(
                        items = newItems,
                        selectedIndex = newItems.lastIndex
                    )
                    copy
                }
            }
        }
    }

    override fun closeCategoryDialog(categoryId: Long) {
        scope.launch {
            categoryPageControl.navigate {
                val newItems = it.items.filter { config ->
                    config != categoryId
                }
                it.copy(items = newItems, selectedIndex = newItems.lastIndex)
            }
        }
    }

    init {
        downloadSystem.downloadEvents
            .filterIsInstance<DownloadManagerEvents.OnJobRemoved>()
            .onEach {
                closeDownloadDialog(it.downloadItem.id)
            }.launchIn(scope)
    }

    override fun sendNotification(tag: Any, title: StringSource, description: StringSource, type: NotificationType) {
        beep()
        showNotification(tag = tag, title = title, description = description, type = type)
    }

    override fun sendDialogNotification(
        title: StringSource,
        description: StringSource,
        type: MessageDialogType,
    ) {
        beep()
        newDialogMessage(MessageDialogModel(title = title, description = description, type = type))
    }

    private fun beep() {
        if (appSettings.notificationSound.value) {
            Toolkit.getDefaultToolkit().beep()
        }
    }

    private fun showNotification(
        tag: Any,
        title: StringSource,
        description: StringSource,
        type: NotificationType = NotificationType.Info,
    ) {
        sendEffect(
            AppEffects.SimpleNotificationNotification(
                NotificationModel(
                    tag = tag,
                    initialTitle = title,
                    initialDescription = description,
                    initialNotificationType = type
                )
            )
        )
    }

    init {
        downloadSystem
            .downloadEvents
            .onEach {
                onNewDownloadEvent(it)
            }
            .launchIn(scope)
//        IntegrationPortBroadcaster.cleanOnClose()
        integration
            .integrationStatus
            .onEach {
                when (it) {
                    is IntegrationResult.Fail -> {
                        IntegrationPortBroadcaster.setIntegrationPortInFile(null)
                        sendDialogNotification(
                            title = Res.string.cant_run_browser_integration.asStringSource(),
                            type = MessageDialogType.Error,
                            description = it.throwable.localizedMessage.asStringSource()
                        )
                    }

                    IntegrationResult.Inactive -> {
                        IntegrationPortBroadcaster.setIntegrationPortInFile(null)
                    }

                    is IntegrationResult.Success -> {
                        IntegrationPortBroadcaster.setIntegrationPortInFile(it.port)
                    }
                }
            }.launchIn(scope)
    }

    private fun onNewDownloadEvent(it: DownloadManagerEvents) {
        if (it.context[ResumedBy]?.by !is User) {
            //only notify events that is started by user
            return
        }
//                or
//                val qm = downloadSystem.queueManager
//                val queueId = qm.findItemInQueue(it.downloadItem.id)
//                if (queueId != null) {
//                    return@onEach
//                    // skip download events when download is triggered by queue
////                    if (qm.getQueue(queue).isQueueActive){
////                      return@onEach
////                    }
//                }
        if (it is DownloadManagerEvents.OnJobCanceled) {
            val exception = it.e
            if (ExceptionUtils.isNormalCancellation(exception)) {
                return
            }
            var isMaxTryReachedError = false
            val actualCause = if (exception is TooManyErrorException) {
                isMaxTryReachedError = true
                exception.findActualDownloadErrorCause()
            } else exception
            if (ExceptionUtils.isNormalCancellation(actualCause)) {
                return
            }
            val prefix = if (isMaxTryReachedError) {
                "Too Many Error: "
            } else {
                "Error: "
            }.asStringSource()
            val reason = actualCause.message?.asStringSource() ?: Res.string.unknown.asStringSource()
            sendNotification(
                "downloadId=${it.downloadItem.id}",
                title = it.downloadItem.name.asStringSource(),
                description = listOf(prefix, reason).combineStringSources(),
                type = NotificationType.Error,
            )
        }
        if (it is DownloadManagerEvents.OnJobCompleted) {
            sendNotification(
                tag = "downloadId=${it.downloadItem.id}",
                title = it.downloadItem.name.asStringSource(),
                description = Res.string.finished.asStringSource(),
                type = NotificationType.Success,
            )
            if (appSettings.showDownloadCompletionDialog.value) {
                openDownloadDialog(it.downloadItem.id)
            }
        }
        if (it is DownloadManagerEvents.OnJobStarting) {
            if (appSettings.showDownloadProgressDialog.value) {
                openDownloadDialog(it.downloadItem.id)
            }
        }
    }

    override suspend fun openDownloadItem(id: Long) {
        val item = downloadSystem.getDownloadItemById(id)
        if (item == null) {
            sendNotification(
                Res.string.open_file,
                Res.string.cant_open_file.asStringSource(),
                Res.string.download_item_not_found.asStringSource(),
                NotificationType.Error,
            )
            return
        }
        openDownloadItem(item)
    }

    override suspend fun openDownloadItem(downloadItem: DownloadItem) {
        runCatching {
            withContext(Dispatchers.IO) {
                FileUtils.openFile(downloadSystem.getDownloadFile(downloadItem))
            }
        }.onFailure {
            sendNotification(
                Res.string.open_file,
                Res.string.cant_open_file.asStringSource(),
                it.localizedMessage?.asStringSource() ?: Res.string.unknown_error.asStringSource(),
                NotificationType.Error,
            )
            println("Can't open file:${it.message}")
        }
    }

    override suspend fun openDownloadItemFolder(id: Long) {
        val item = downloadSystem.getDownloadItemById(id)
        if (item == null) {
            sendNotification(
                Res.string.open_folder,
                Res.string.cant_open_folder.asStringSource(),
                Res.string.download_item_not_found.asStringSource(),
                NotificationType.Error,
            )
            return
        }
        openDownloadItemFolder(item)
    }

    override suspend fun openDownloadItemFolder(downloadItem: DownloadItem) {
        runCatching {
            withContext(Dispatchers.IO) {
                val file = downloadSystem.getDownloadFile(downloadItem)
                if (file.exists()) {
                    FileUtils.openFolderOfFile(file)
                } else {
                    val incompleteFile = IncompleteFileUtil.addIncompleteIndicator(file, downloadItem.id)
                    if (incompleteFile.exists() && downloadItem.status != DownloadStatus.Completed) {
                        FileUtils.openFolderOfFile(incompleteFile)
                    } else {
                        FileUtils.openFolder(file.parentFile)
                    }
                }
            }
        }.onFailure {
            sendNotification(
                Res.string.open_folder,
                Res.string.cant_open_folder.asStringSource(),
                it.localizedMessage?.asStringSource() ?: Res.string.unknown_error.asStringSource(),
                NotificationType.Error,
            )
            println("Can't open folder:${it.message}")
        }
    }

    fun externalCredentialComingIntoApp(
        list: List<DownloadCredentials>,
        options: ImportOptions
    ) {
        val editDownloadComponent = editDownloadSlot.value.child?.instance
        if (editDownloadComponent != null) {
            list.firstOrNull()?.let {
                editDownloadComponent.importCredential(it)
                editDownloadComponent.bringToFront()
            }
        } else {
            openAddDownloadDialog(list, options)
        }
    }

    override fun openAddDownloadDialog(
        links: List<DownloadCredentials>,
        importOptions: ImportOptions,
    ) {
        scope.launch {
            //remove duplicates
            val links = links.distinct()
            addDownloadPageControl.navigate {
                val newItems = it.items +
                        if (links.size > 1) {
                            AddDownloadConfig.MultipleAddConfig(
                                links,
                                importOptions,
                            )
                        } else {
                            AddDownloadConfig.SingleAddConfig(
                                links.firstOrNull() ?: DownloadCredentials.empty(),
                                importOptions,
                            )
                        }
                val copy = it.copy(
                    items = newItems,
                    selectedIndex = newItems.lastIndex
                )
                copy
            }
        }
    }

    override fun closeAddDownloadDialog(dialogId: String) {
        scope.launch {
            addDownloadPageControl.navigate {
                val newItems = it.items.filter { config ->
                    config.id != dialogId
                }
                it.copy(items = newItems, selectedIndex = newItems.lastIndex)
            }
        }
    }

    override fun openDownloadDialog(id: Long) {
        scope.launch {
            val component = openedDownloadDialogs.value.find {
                it.downloadId == id
            }
            if (component != null) {
                component.bringToFront()
            } else {
                downloadDialogControl.navigate {
                    val newItems = (it.items.toSet() + SingleDownloadComponent.Config(id)).toList()
                    val copy = it.copy(
                        items = newItems,
                        selectedIndex = newItems.lastIndex
                    )
                    copy
                }
            }

        }
    }

    override fun closeDownloadDialog(vararg ids: Long) {
        scope.launch {
            downloadDialogControl.navigate {
                val newItems = it.items.filter { config ->
                    config.id !in ids
                }
                it.copy(items = newItems, selectedIndex = newItems.lastIndex)
            }
        }
    }

    private val fileChecksumPagesControl = SlotNavigation<FileChecksumComponentConfig>()
    val openedFileChecksumDialog = childSlot(
        key = "openedFileChecksumPage",
        source = fileChecksumPagesControl,
        serializer = null,
        childFactory = { config, ctx ->
            FileChecksumComponent(
                ctx = ctx,
                id = config.id,
                itemIds = config.itemIds,
                closeComponent = {
                    closeFileChecksumPage(config.id)
                }
            )
        }
    ).subscribeAsStateFlow()

    override fun openFileChecksumPage(ids: List<Long>) {
        scope.launch {
            val instance = openedFileChecksumDialog.value.child?.instance
            if (instance?.itemIds == ids) {
                instance.bringToFront()
            } else {
                fileChecksumPagesControl.navigate {
                    FileChecksumComponentConfig(itemIds = ids)
                }
            }
        }
    }

    override fun closeFileChecksumPage(dialogId: String) {
        scope.launch {
            fileChecksumPagesControl.dismiss()
        }
    }

    fun addDownloads(
        items: List<DownloadItem>,
        onDuplicateStrategy: (DownloadItem) -> OnDuplicateStrategy,
        categorySelectionMode: CategorySelectionMode?,
        queueId: Long?,
    ): Deferred<List<Long>> {
        return scope.async {
            downloadSystem.addDownload(
                newItemsToAdd = items,
                onDuplicateStrategy = onDuplicateStrategy,
                queueId = queueId,
                categorySelectionMode = categorySelectionMode,
            )
        }
    }

    fun addDownload(
        item: DownloadItem,
        queueId: Long?,
        categoryId: Long?,
        onDuplicateStrategy: OnDuplicateStrategy,
    ): Deferred<Long> {
        return scope.async {
            downloadSystem.addDownload(
                downloadItem = item,
                onDuplicateStrategy = onDuplicateStrategy,
                queueId = queueId,
                categoryId = categoryId,
            )
        }
    }

    fun startNewDownload(
        item: DownloadItem,
        onDuplicateStrategy: OnDuplicateStrategy,
        categoryId: Long?,
    ): Deferred<Long> {
        return scope.async {
            downloadSystem.addDownload(
                downloadItem = item,
                onDuplicateStrategy = onDuplicateStrategy,
                queueId = DefaultQueueInfo.ID,
                categoryId = categoryId,
            ).also {
                downloadSystem.manualResume(it)
            }
        }
    }

    private val _showConfirmExitDialog = MutableStateFlow(false)
    val showConfirmExitDialog = _showConfirmExitDialog.asStateFlow()

    fun exitAppAsync() {
        scope.launch { exitApp() }
    }

    suspend fun exitApp() {
        downloadSystem.stopAnything()
        exitProcess(0)
    }

    fun closeConfirmExit() {
        _showConfirmExitDialog.value = false
    }

    suspend fun requestExitApp() {
        val hasActiveDownloads = downloadSystem.downloadMonitor.activeDownloadCount.value > 0
        if (hasActiveDownloads) {
            _showConfirmExitDialog.value = true
            return
        }
        exitApp()
    }

    fun openAbout() {
        showAboutPage.update { true }
    }

    fun closeAbout() {
        showAboutPage.update { false }
    }

    fun openOpenSourceLibraries() {
        showOpenSourceLibraries.update { true }
    }

    fun closeOpenSourceLibraries() {
        showOpenSourceLibraries.update { false }
    }

    fun openTranslatorsPage() {
        showTranslators.update { true }
    }

    fun closeTranslatorsPage() {
        showTranslators.update { false }
    }

    override fun openQueues(
        openQueueId: Long?,
    ) {
        scope.launch {
            showQueuesSlot.value.child?.instance.let {
                if (it != null) {
                    it.bringToFront()
                    if (openQueueId != null) {
                        it.onQueueSelected(openQueueId)
                    }
                } else {
                    showQueues.activate(
                        QueuePageConfig(
                            selectedQueue = openQueueId
                        )
                    )
                }
            }
        }
    }

    override fun closeQueues() {
        showQueues.dismiss()
    }

    var showCreateQueueDialog = MutableStateFlow(false)
        private set

    override fun closeNewQueueDialog() {
        showCreateQueueDialog.update { false }
    }

    override fun openNewQueueDialog() {
        showCreateQueueDialog.update { true }
    }

    fun createNewQueue(name: String) {
        scope.launch {
            downloadSystem.addQueue(name)
        }
    }

    fun openBatchDownload() {
        scope.launch {

            batchDownloadSlot.value.child?.instance.let {
                if (it != null) {
                    it.bringToFront()
                } else {
                    batchDownload.activate(BatchDownloadConfig())
                }
            }
        }
    }

    fun closeBatchDownload() {
        batchDownload.dismiss()
    }

    val dialogMessages: MutableStateFlow<List<MessageDialogModel>> = MutableStateFlow(emptyList())
    private fun newDialogMessage(msgDialogModel: MessageDialogModel) {
        dialogMessages.update {
            it
                .filter { item -> item.id != msgDialogModel.id }
                .plus(msgDialogModel)
        }
    }

    fun onDismissDialogMessage(msgDialogModel: MessageDialogModel) {
        dialogMessages.update {
            it.filter { item ->
                msgDialogModel.id != item.id
            }
        }
    }

    fun isReady(): Boolean {
        return listOf(
            IntegrationPortBroadcaster.isInitialized(),
        ).all { it }
    }
    val powerActionNavigation = SlotNavigation<PowerActionComponent.Config>()
    val openedPowerAction = childSlot(
        source = powerActionNavigation,
        key = "powerAction",
        serializer = null,
        childFactory = { config, ctx ->
            PowerActionComponent(
                ctx = ctx,
                powerActionConfig = config.powerActionConfig,
                powerActionDelay = config.powerActionDelay,
                powerActionReason = config.powerActionReason,
                close = ::dismissPowerAction,
                onBeforePowerAction = {
                    downloadSystem.stopAnything()
                },
            )
        }
    ).subscribeAsStateFlow()

    override fun initiatePowerAction(
        powerActionConfig: PowerActionConfig,
        reason: PowerActionComponent.PowerActionReason,
    ) {
        scope.launch {
            powerActionNavigation.activate(
                PowerActionComponent.Config(
                    powerActionConfig = powerActionConfig,
                    powerActionReason = reason,
                )
            )
        }
    }

    override fun dismissPowerAction() {
        scope.launch {
            powerActionNavigation.dismiss()
        }
    }

    val updater = UpdateComponent(
        childContext("updater"),
        this,
    )
    val showAboutPage = MutableStateFlow(false)
    val showOpenSourceLibraries = MutableStateFlow(false)
    val showTranslators = MutableStateFlow(false)
    val theme = appRepository.theme
    val uiScale = appRepository.uiScale
}

interface DownloadDialogManager {
    val openedDownloadDialogs: StateFlow<List<SingleDownloadComponent>>
    fun openDownloadDialog(id: Long)
    fun closeDownloadDialog(vararg ids: Long)
}

interface EditDownloadDialogManager {
    fun openEditDownloadDialog(id: Long)
    fun closeEditDownloadDialog()
}

interface AddDownloadDialogManager {
    val openedAddDownloadDialogs: StateFlow<List<AddDownloadComponent>>
    fun openAddDownloadDialog(
        links: List<DownloadCredentials>,
        importOptions: ImportOptions = ImportOptions(),
    )

    fun closeAddDownloadDialog(dialogId: String)
}

interface FileChecksumDialogManager {
    fun openFileChecksumPage(ids: List<Long>)

    fun closeFileChecksumPage(dialogId: String)
}

interface QueuePageManager {
    fun openQueues(
        openQueueId: Long? = null
    )

    fun closeQueues()

    fun openNewQueueDialog()
    fun closeNewQueueDialog()
}
interface PowerActionManager {
    fun initiatePowerAction(
        powerActionConfig: PowerActionConfig,
        reason: PowerActionComponent.PowerActionReason,
    )

    fun dismissPowerAction()
}
