import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

sealed class Page {
    object RemoteConnection : Page()
    // ... other pages
}

fun createPageContent(page: Page, navController: NavHostController): @Composable () -> Unit {
    return when (page) {
        is Page.RemoteConnection -> {
            { RemoteConnectionPage() }
        }
        // ... other pages
        else -> {
            { } // Handle unknown pages, or throw an exception.
        }
    }
}


@Composable
fun RemoteConnectionPage() {
    //Implementation for RemoteConnectionPage goes here.  This is a placeholder.
}