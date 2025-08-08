package com.example.clazzi.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navController: NavController
) {

    val(email, setEmail) = remember { mutableStateOf("") }
    val(password, setPassword) = remember { mutableStateOf("") }

    var isLogin by remember {mutableStateOf(false)}
    val passwordVisible = remember {mutableStateOf(false)}

    val auth = FirebaseAuth.getInstance()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(if(isLogin)"로그인" else "회원가입") }
            )
        },
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 이메일
            OutlinedTextField(
                value = email,
                onValueChange = setEmail,
                label = {Text("이메일")},
                modifier = Modifier
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                )

            )
            // 비밀번호
            OutlinedTextField(
                value = password,
                onValueChange = setPassword,
                label = {Text("비밀번호")},
                modifier = Modifier
                    .fillMaxWidth(),
                visualTransformation = if(passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            passwordVisible.value = !passwordVisible.value
                        }
                    ) {
                        Icon(
                            imageVector =
                                if(passwordVisible.value) Icons.Default.Visibility
                                else Icons.Default.VisibilityOff,
                                contentDescription = "비밀번호 보이기"
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // 로그인 일때
                    if(isLogin) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    navController.navigate("voteList")
                                } else {
                                    Log.w("AuthScreen","로그인 실패", task.exception)
                                }
                            }
                    }
                    // 회원가입 일때
                    else {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    navController.navigate("voteList")
                                } else {
                                    Log.w("AuthScreen","회원가입 실패", task.exception)
                                }
                            }
                    }
                }
            ) {
                Text("${if(isLogin)"로그인" else "회원가입"}")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text( text = if (isLogin) "회원가입 하시겠습니까?" else "이미 계정이 있으신가요?",
                modifier = Modifier
                    .clickable { isLogin = !isLogin }
            )
        }

    }

}