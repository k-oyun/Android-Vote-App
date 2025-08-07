package com.example.clazzi.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.clazzi.model.Vote
import com.example.clazzi.model.VoteOption
import com.example.clazzi.viewmodel.VoteListViewModel
import java.util.Date
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVoteScreen(
//    onVoteCreate: (Vote) -> Unit
    viewModel: VoteListViewModel,
    navController: NavController

) {
    val (title, setTitle) = remember { mutableStateOf("") }
    val options = remember { mutableStateListOf<String>("", "") }
//    val optionText: List<String> = arrayListOf("항목 1", "항목 2")
    Scaffold (
        topBar =  {
            TopAppBar(
                title = {Text("투표 만들기")},
            )
        },
        modifier = Modifier
            .fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value=title,
                onValueChange = setTitle,
                label = {Text("투표 제목")},
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = "투표 사진",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("투표 항목", style = MaterialTheme.typography.titleMedium)
            options.forEachIndexed { index, option ->
                OutlinedTextField(
                    value=option,
                    onValueChange = {newValue ->
                        options[index] = newValue
                    },
                    label = {Text("항목 ${index + 1}")},
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Button(
                onClick = {
                    options.add("")
                },
                modifier = Modifier
                    .align(Alignment.End)
            ) {
                Text("항목 추가")
            }
            Button(
                onClick = {
                    val newVote = Vote(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        createAt = Date(),
                        voteOptions = options
                            .filter {it.isNotBlank()}
                            .map{
                                VoteOption(id = UUID.randomUUID().toString(), optionText = it)
                            }
                    )
//                    onVoteCreate(newVote)
                    viewModel.addVote(newVote)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("투표 생성")
            }
        }
    }
}