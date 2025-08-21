package com.example.tzapp

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tzapp.data.Repository
import com.example.tzapp.ui.auth.LoginScreen
import com.example.tzapp.ui.profile.ProfileScreen
import com.example.tzapp.ui.search.SearchScreen
import com.example.tzapp.ui.settings.SettingsScreen

enum class Route(val route: String) { Home("home"), List("list"), Detail("detail/{id}"), Search("search"), Profile("profile"), Login("login"), Settings("settings"), About("about") }

data class BottomItem(
	val route: Route,
	@StringRes val titleRes: Int,	@DrawableRes val iconRes: Int
)

private val bottomItems = listOf(
	BottomItem(Route.Home, R.string.nav_home, R.drawable.ic_home),
	BottomItem(Route.List, R.string.nav_list, R.drawable.ic_list),
	BottomItem(Route.Search, R.string.nav_search, R.drawable.ic_search),
	BottomItem(Route.Profile, R.string.nav_profile, R.drawable.ic_person),
	BottomItem(Route.Settings, R.string.nav_settings, R.drawable.ic_settings),
	BottomItem(Route.About, R.string.nav_about, R.drawable.ic_info)
)

@Composable
fun App() {
	val navController = rememberNavController()
	val backStackEntry by navController.currentBackStackEntryAsState()
	val currentDestination = backStackEntry?.destination

	Scaffold(
		bottomBar = {
			NavigationBar {
				bottomItems.forEach { item ->
					val selected = isSelected(currentDestination, item.route.route)
					NavigationBarItem(
						selected = selected,
						onClick = {
							if (!selected) {
								navController.navigate(item.route.route) {
									popUpTo(navController.graph.startDestinationId) { saveState = true }
									launchSingleTop = true
									restoreState = true
								}
							}
						},
						icon = {
							Icon(
								painter = painterResource(id = item.iconRes),
								contentDescription = stringResource(id = item.titleRes)
							)
						},
						label = { Text(text = stringResource(id = item.titleRes)) }
					)
				}
			}
		}
	) { padding ->
		NavHost(
			navController = navController,
			startDestination = Route.Home.route,
			modifier = Modifier.padding(padding)
		) {
			composable(Route.Home.route) {
				HomeScreen(
					onOpenProfile = { navController.navigate(Route.Profile.route) },
					onOpenSettings = { navController.navigate(Route.Settings.route) }
				)
			}
			composable(Route.List.route) { ListScreen(onOpen = { id ->
				navController.navigate("detail/$id")
			}) }
			composable(
				route = Route.Detail.route,
				arguments = listOf(navArgument("id") { type = NavType.IntType })
			) { backStackEntry ->
				val id = backStackEntry.arguments?.getInt("id") ?: -1
				DetailScreen(id = id)
			}
			composable(Route.Search.route) { SearchScreen(onOpen = { id -> navController.navigate("detail/$id") }) }
			composable(Route.Profile.route) { ProfileScreen(onRequestLogin = { navController.navigate(Route.Login.route) }, onLogout = { navController.popBackStack() }) }
			composable(Route.Login.route) { LoginScreen(onSuccess = { navController.popBackStack(); navController.navigate(Route.Profile.route) }) }
			composable(Route.Settings.route) { SettingsScreen() }
			composable(Route.About.route) { AboutScreen(padding) }
		}
	}
}

private fun isSelected(destination: NavDestination?, route: String): Boolean {
	return destination?.hierarchy?.any { it.route == route } == true
}

@Composable
private fun HomeScreen(onOpenProfile: () -> Unit, onOpenSettings: () -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Text(
			text = stringResource(id = R.string.screen_home),
			style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
		)
		androidx.compose.material3.Button(
			onClick = onOpenProfile,
			modifier = Modifier.padding(top = 16.dp)
		) { Text(text = stringResource(id = R.string.action_open_profile)) }
		androidx.compose.material3.OutlinedButton(
			onClick = onOpenSettings,
			modifier = Modifier.padding(top = 8.dp)
		) { Text(text = stringResource(id = R.string.action_open_settings)) }
	}
}

@Composable
private fun ListScreen(onOpen: (Int) -> Unit) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(Repository.items.size) { index ->
            val item = Repository.items[index]
            androidx.compose.material3.ElevatedCard(
                onClick = { onOpen(item.id) },
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = item.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AboutScreen(padding: PaddingValues) {
	Centered(text = stringResource(id = R.string.screen_about))
}

@Composable
private fun Centered(text: String) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(24.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Text(
			text = text,
			style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
		)
		Text(
			text = "Здесь будет ваш функционал по ТЗ.",
			modifier = Modifier.padding(top = 12.dp),
			style = MaterialTheme.typography.bodyLarge
		)
	}
}

@Composable
private fun DetailScreen(id: Int) {
    val item = Repository.getItemById(id)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = stringResource(id = R.string.screen_detail_title, id),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        if (item != null) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            Text(
                text = stringResource(id = R.string.screen_detail_not_found),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}