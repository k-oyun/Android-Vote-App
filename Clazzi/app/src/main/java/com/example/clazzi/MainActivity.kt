package com.example.clazzi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.clazzi.repository.FirebaseVoteRepository
import com.example.clazzi.repository.RestApiVoteRepository
import com.example.clazzi.repository.network.ApiClient
import com.example.clazzi.ui.screens.CreateVoteScreen
import com.example.clazzi.ui.screens.VoteListScreen
import com.example.clazzi.ui.screens.VoteScreen
import com.example.clazzi.ui.theme.ClazziTheme
import com.example.clazzi.viewmodel.VoteListViewModel
import com.example.clazzi.ui.screens.AuthScreen
import com.example.clazzi.ui.screens.MyPageScreen
import com.example.clazzi.viewmodel.VoteListViewModelFactory
import com.example.clazzi.viewmodel.VoteViewModel
import com.example.clazzi.viewmodel.VoteViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var name = remember { mutableStateOf("김한수") }
            ClazziTheme {
                val navController = rememberNavController()

//                val repo = FirebaseVoteRepository() // 파이어베이스 연동
                val repo = RestApiVoteRepository(ApiClient.voteApiService) // restapi 연동
                val voteListViewModel : VoteListViewModel = viewModel(
                    factory = VoteListViewModelFactory(repo)
                )
//                val voteListViewModel : VoteListViewModel = viewModel()

                val voteViewModel : VoteViewModel = viewModel(
                    factory = VoteViewModelFactory(repo)
                )

                val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
                NavHost(
                    navController = navController,
                    startDestination = if (isLoggedIn) "voteList" else "auth"
                ) {
                    composable("auth") {
                        AuthScreen(
                            navController = navController
                        )
                    }
                    composable("myPage") {
                        MyPageScreen(
                            navController = navController
                        )
                    }
                    composable("voteList") {
                        VoteListScreen(
                            navController = navController,
                            viewModel = voteListViewModel,
                            onVoteClicked = { voteId ->
                                navController.navigate("vote/$voteId")
                            }
                        )
                    }
                    composable(
                        "vote/{voteId}",
                        deepLinks = listOf(
                            navDeepLink { uriPattern = "clazzi://vote/{voteId}" },
                            navDeepLink { uriPattern = "https://clazzi-c27ac.web.app/vote/{voteId}" },
                        )
                    ) { backStackEntry ->
                        val voteId = backStackEntry.arguments?.getString("voteId") ?: "1"
//                        val vote = voteListViewModel.getVoteById(voteId)
                        VoteScreen(
                            voteId = voteId,
                            navController = navController,
                            voteListViewModel = voteListViewModel,
                            voteViewModel = voteViewModel
                        )
                        /*if (vote != null) {
                            VoteScreen(
                                voteId = voteId,
                                navController = navController,
                                voteListViewModel = voteListViewModel
                            )
                        } else {
                            // 에러처리: 특정 ID의 투표가 없을 때의
                            val context = LocalContext.current
                            Toast.makeText(context, "해당 투표가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                        }*/
                    }
                    /*composable("createVote") {
                        CreateVoteScreen(
                            onVoteCreate = { vote ->
                                navController.popBackStack() // 뒤로가기
                                voteListViewModel.addVote(vote)
                            }
                        )
                    }*/
                    composable("createVote") {
                        CreateVoteScreen(
                            navController = navController,
                            viewModel = voteListViewModel,
                        )
                    }
                }
            }
        }
    }
}

