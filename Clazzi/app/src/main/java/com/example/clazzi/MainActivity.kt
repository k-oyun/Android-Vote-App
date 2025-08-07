package com.example.clazzi

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clazzi.model.Vote
import com.example.clazzi.model.VoteOption
import com.example.clazzi.ui.screens.CreateVoteScreen
import com.example.clazzi.ui.screens.VoteListScreen
import com.example.clazzi.ui.screens.VoteScreen
import com.example.clazzi.ui.theme.ClazziTheme
import com.example.clazzi.viewmodel.VoteListViewModel

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClazziTheme {
                val navController = rememberNavController()
                val voteListViewModel = viewModel<VoteListViewModel>()
                NavHost(
                    navController = navController, startDestination = "voteList"
//                    navController = navController, startDestination = "createVote"
                ) {
                    composable("voteList") {
                        VoteListScreen(
                            navController = navController,
                            viewModel = voteListViewModel,
                            onVoteClicked = { voteId ->
                                navController.navigate("vote/$voteId")
                            })
                    }
                    composable("vote/{voteId}") { backStackEntry ->
                        val voteId = backStackEntry.arguments?.getString("voteId") ?: "1"
                        val vote = voteListViewModel.getVoteById(voteId)
                        if (vote != null) {
                            VoteScreen(
                                vote = vote,
                                navController = navController
                            )
                        }
                        else {
                            val context = LocalContext.current
                            Toast.makeText(context, "투표를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }

                    }

                    // 복잡한 플젝 시
//                    composable("createVote") {
//                        CreateVoteScreen(
//                            onVoteCreate = { vote ->
//                                navController.popBackStack()
//                                voteListViewModel.addVote(vote)
//                            }
//                        )
//                    }

                    // 간단한 플젝 시
                    // 플젝할때 뷰 모델의 인터페이스도 만들어라 오윤아
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


