package com.example.tzapp

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.tzapp.ui.reco.RecommendationsScreen
import com.example.tzapp.ui.reco.RecommendationDetailScreen
import com.example.tzapp.ui.planner.PlannerScreen
import com.example.tzapp.ui.trainers.TrainersScreen
import com.example.tzapp.ui.diary.DiaryScreen
import com.example.tzapp.ui.help.HelpNearbyScreen

enum class Route(val route: String) {
    Home("home"),
    List("list"),
    Detail("detail/{id}"),
    Search("search"),
    Profile("profile"),
    Login("login"),
    Settings("settings"),
    Recommendations("recommendations"),
    RecommendationDetail("recommendations/{id}"),
    Planner("planner"),
    Trainers("trainers"),
    Diary("diary"),
    HelpNearby("help_nearby"),
    About("about")
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
	val navController = rememberNavController()
	val backStackEntry by navController.currentBackStackEntryAsState()
	val currentDestination = backStackEntry?.destination

	Scaffold(
		topBar = {
			val isHome = currentDestination?.route == Route.Home.route
			if (!isHome) {
				TopAppBar(
					title = { Text(stringResource(id = R.string.app_name)) },
					navigationIcon = {
						IconButton(onClick = {
							navController.navigate(Route.Home.route) {
								popUpTo(navController.graph.startDestinationId) { saveState = true }
								launchSingleTop = true
								restoreState = true
							}
						}) {
							Icon(painter = painterResource(id = R.drawable.ic_home), contentDescription = stringResource(id = R.string.nav_home))
						}
					}
				)
			}
		},
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
					onOpenRecommendations = { navController.navigate(Route.Recommendations.route) },
					onOpenTrainers = { navController.navigate(Route.Trainers.route) },
					onOpenPlanner = { navController.navigate(Route.Planner.route) },
					onOpenDiary = { navController.navigate(Route.Diary.route) },
					onOpenHelpNearby = { navController.navigate(Route.HelpNearby.route) },
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
			composable(Route.Recommendations.route) { RecommendationsScreen(onOpen = { id -> navController.navigate("recommendations/$id") }) }
			composable(
				route = Route.RecommendationDetail.route,
				arguments = listOf(navArgument("id") { type = NavType.IntType })
			) { backStackEntry ->
				val id = backStackEntry.arguments?.getInt("id") ?: -1
				RecommendationDetailScreen(id = id)
			}
			composable(Route.Planner.route) { PlannerScreen() }
			composable(Route.Trainers.route) { TrainersScreen() }
			composable(Route.Diary.route) { DiaryScreen() }
			composable(Route.HelpNearby.route) { HelpNearbyScreen() }
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
private fun HomeScreen(
    onOpenRecommendations: () -> Unit,
    onOpenTrainers: () -> Unit,
    onOpenPlanner: () -> Unit,
    onOpenDiary: () -> Unit,
    onOpenHelpNearby: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    var showEmergency by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.home_daily_phrase),
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(
                listOf(
                    Triple(R.drawable.ic_recommendations, R.string.home_recommendations, onOpenRecommendations),
                    Triple(R.drawable.ic_trainer, R.string.home_trainers, onOpenTrainers),
                    Triple(R.drawable.ic_planner, R.string.home_planner, onOpenPlanner),
                    Triple(R.drawable.ic_diary, R.string.home_diary, onOpenDiary),
                    Triple(R.drawable.ic_help_nearby, R.string.home_help_nearby, onOpenHelpNearby)
                )
            ) { (icon, title, action) ->
                androidx.compose.material3.ElevatedCard(
                    onClick = action,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painter = painterResource(id = icon), contentDescription = null)
                        Text(
                            text = stringResource(id = title),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showEmergency = true }, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.home_emergency) + " 🚨")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onOpenProfile, modifier = Modifier.weight(1f)) {
                Text(text = stringResource(id = R.string.action_open_profile))
            }
            OutlinedButton(onClick = onOpenSettings, modifier = Modifier.weight(1f)) {
                Text(text = stringResource(id = R.string.action_open_settings))
            }
        }

        if (showEmergency) {
            AlertDialog(
                onDismissRequest = { showEmergency = false },
                title = { Text(stringResource(id = R.string.emergency_dialog_title)) },
                text = {
                    Column {
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:103"))
                            context.startActivity(intent)
                        }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(id = R.string.emergency_call_ambulance)) }
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:102"))
                            context.startActivity(intent)
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)) { Text(stringResource(id = R.string.emergency_call_police)) }
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:123456789"))
                            context.startActivity(intent)
                            Toast.makeText(context, "Задайте свой номер близких в коде", Toast.LENGTH_SHORT).show()
                        }, modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)) { Text(stringResource(id = R.string.emergency_call_relatives)) }
                    }
                },
                confirmButton = {
                    OutlinedButton(onClick = { showEmergency = false }) { Text("OK") }
                }
            )
        }
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