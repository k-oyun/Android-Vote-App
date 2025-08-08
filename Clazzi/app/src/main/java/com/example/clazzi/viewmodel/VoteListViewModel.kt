package com.example.clazzi.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clazzi.model.Vote
import com.example.clazzi.model.VoteOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class VoteListViewModel : ViewModel() {
    val db = Firebase.firestore
    private val _voteList = MutableStateFlow<List<Vote>>(emptyList())
    val voteList: StateFlow<List<Vote>> = _voteList


    init {
        // 뷰 모델 초기화 시 실시간 리스너 설정
        db.collection("votes")
            .orderBy("createAt", Query.Direction.DESCENDING) // 정렬
            .addSnapshotListener {snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Error getting votes", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    _voteList.value = snapshot.toObjects(Vote::class.java)
                }
            }
    }

    // ID로 특정 투표를 가져오는 메서드
    fun getVoteById(voteId: String): Vote? {
        return _voteList.value.find { it.id == voteId }
    }

    // 새로운 투표를 추가하는 메서드
    fun addVote(vote: Vote, context: Context, imageUri: Uri) {
//        _voteList.value += vote
        val db = Firebase.firestore
        viewModelScope.launch {
            try {
                val storageRef = FirebaseStorage.getInstance().reference
                val imageRef = storageRef.child(("images/${UUID.randomUUID()}.jpg"))

                // 이미지 업로드
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val uploadTask = inputStream?.let { imageRef.putStream(it).await() }

                // 다운로드 URL 가져오기
                val downloadUrl = imageRef.downloadUrl.await().toString()

                // Firestore에 업로드 할 데이터 구성
                val voteMap = hashMapOf(
                    "id" to vote.id,
                    "title" to vote.title,
                    "imageUrl" to downloadUrl, // 이미지 URL 추가
                    "createAt" to FieldValue.serverTimestamp(),  // 서버 타입 설정
                    "voteOptions" to vote.voteOptions.map {
                        hashMapOf(
                            "id" to it.id,
                            "optionText" to it.optionText
                        )
                    }
                )
                db.collection("votes")
                    .document(vote.id)
                    .set(voteMap)  // Map으로 저장
                    .await()
            } catch (e: Exception) {
                // 에러 처리 (예: 사용자에게 토스트 메시지 표시)
            }
        }
    }

    // 투표 데이터를 업데이트 하는 함수
    fun setVote(vote: Vote) {
        viewModelScope.launch {
            try {
                db.collection("votes")
                    .document(vote.id)
                    .set(vote)
                    .await()
                Log.d("Firestore", "투표가 성공적으로 되었습니다.")
            } catch (e: Exception) {
                Log.e("Firestore", "투표 업데이트 중 오류가 발생했습니다.", e)
            }
        }
    }
}
