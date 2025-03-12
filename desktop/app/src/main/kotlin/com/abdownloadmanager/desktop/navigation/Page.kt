sealed class Page(
    override val route: String,
    override val icon: @Composable () -> Unit = {},
    open val title: String = "",
    val needAuth: Boolean = false,
) : NavigationNode {

    object RemoteConnection : Page(
        route = "remote",
        icon = { Icon(Icons.Default.DeviceHub, contentDescription = null) },
        title = "Remote Connection"
    )
}