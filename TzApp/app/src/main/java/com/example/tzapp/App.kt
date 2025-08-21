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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

enum class Route(val route: String) { Home("home"), List("list"), About("about") }

data class BottomItem(
	val route: Route,
	@StringRes val titleRes: Int,
	@DrawableRes val iconRes: Int
)

private val bottomItems = listOf(
	BottomItem(Route.Home, R.string.nav_home, R.drawable.ic_home),
	BottomItem(Route.List, R.string.nav_list, R.drawable.ic_list),
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
			composable(Route.Home.route) { HomeScreen(padding) }
			composable(Route.List.route) { ListScreen(padding) }
			composable(Route.About.route) { AboutScreen(padding) }
		}
	}
}

private fun isSelected(destination: NavDestination?, route: String): Boolean {
	return destination?.hierarchy?.any { it.route == route } == true
}

@Composable
private fun HomeScreen(padding: PaddingValues) {
	Centered(text = stringResource(id = R.string.screen_home))
}

@Composable
private fun ListScreen(padding: PaddingValues) {
	Centered(text = stringResource(id = R.string.screen_list))
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

