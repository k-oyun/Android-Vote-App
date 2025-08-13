package com.example.clazzi.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.clazzi.model.Vote
import com.example.clazzi.model.VoteOption
import com.example.clazzi.ui.components.CameraPickerWithPermission
import com.example.clazzi.ui.components.ImagePickerWithPermission
import com.example.clazzi.viewmodel.VoteListViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class CreateVoteScreenViewModel() {

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateVoteScreen(
//    onVoteCreate: (Vote) -> Unit
    viewModel: VoteListViewModel,
    navController: NavController

) {
    val (title, setTitle) = remember { mutableStateOf("") }
    val options = remember { mutableStateListOf<String>("", "") }
    var showImagePickTypeSheet by remember { mutableStateOf(false) }
    var showImagePicker by remember { mutableStateOf(false) }
    var showCameraPicker by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // 마감일 스테이트
    var deadlineDate by remember { mutableStateOf<Date?>(null) }
//    val createVoteScreenViewModel = CreateVoteScreenViewModel()

//    val optionText: List<String> = arrayListOf("항목 1", "항목 2")

    // 앱의 기본 레이아웃 구조(탑바, 바텀네비게이션) 제공
    // 자동으로 탑바는 위, 바텀 네비게이션은 아래에 위치 시킴
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("투표 만들기") },
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
                value = title,
                onValueChange = setTitle,
                label = { Text("투표 제목") },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = if (imageUri != null)
                    rememberAsyncImagePainter(imageUri)
                else
                    painterResource(id = android.R.drawable.ic_menu_gallery),
                contentDescription = "투표 사진",
                contentScale = ContentScale.Crop,
                // 모디파이어 : UI 요소, 크기, 배경, 정렬 등 속성 저장
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .align(Alignment.CenterHorizontally)
                    // 클릭커블의 역할: 해당 UI 요소가 클릭 이벤트를 받을 수 있다.
                    .clickable {
                        showImagePickTypeSheet = true
                    }
            )

            if (showImagePickTypeSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showImagePickTypeSheet = false }
                ) {
                    ListItem(
                        headlineContent = { Text("카메라로 촬영") },
                        modifier = Modifier
                            .clickable {
                                showImagePickTypeSheet = false
                                showCameraPicker = true
                            }
                    )
                    ListItem(
                        headlineContent = { Text("앨범에서 선택") },
                        modifier = Modifier
                            .clickable {
                                showImagePickTypeSheet = false
                                showImagePicker = true
                            }
                    )
                }
            }

            // 권한 팝업 및 이미지 선택 화면으로 이동
            if (showImagePicker) {
                ImagePickerWithPermission(
                    onImagePicked = { uri ->
                        imageUri = uri
                        showImagePicker = false
                    }
                )
            }

            // 권한 팝업 및 카메라
            if (showCameraPicker) {
                CameraPickerWithPermission(
                    onImageCaptured = { uri ->
                        imageUri = uri
                        showCameraPicker = false
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("투표 항목", style = MaterialTheme.typography.titleMedium)
            options.forEachIndexed { index, option ->
                OutlinedTextField(
                    value = option,
                    onValueChange = { newValue ->
                        options[index] = newValue
                    },
                    label = { Text("항목 ${index + 1}") },
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

            Spacer(modifier = Modifier.height(40.dp))
            DeadlineDateTimePicker(
                deadline = deadlineDate,
                onDeadlineChanged = { newDate ->
                    deadlineDate = newDate
                }
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    // 후에 주석 풀어야함
                    if(imageUri == null) {
                        Toast.makeText(navController.context, "이미지를 선택해주세요.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val newVote = Vote(
                        id = UUID.randomUUID().toString(),
                        title = title,
                        createAt = Date(),
                        voteOptions = options
                            .filter { it.isNotBlank() }
                            .map {
                                VoteOption(id = UUID.randomUUID().toString(), optionText = it)
                            },
                        deadline = deadlineDate
                    )
                    viewModel.addVote(newVote, navController.context, imageUri!!)
//                    viewModel.addVote(newVote, navController.context)
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

@Composable
fun DeadlineDateTimePicker(
    deadline: Date?,
    onDeadlineChanged: (Date) -> Unit
) {
    // 흐름을 관리하는 요소
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // 초기 값이 있으면 calendar에 세팅
    deadline?.let { calendar.time = it }

    // 화면에 보여줄 문자열 포매팅
    val displayText = deadline?.let {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(it)
    } ?: ""

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                TimePickerDialog(
                    context,
                    { _, hourOfDay: Int, minute: Int ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)
                        onDeadlineChanged(calendar.time)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }



    OutlinedTextField(
        value = displayText,
        onValueChange = {},
        label = { Text("투표 마감일") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                datePickerDialog.show()
            },
            enabled = false,
            readOnly = true,
        )
}