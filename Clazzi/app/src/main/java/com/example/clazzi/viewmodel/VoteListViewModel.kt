package com.example.clazzi.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clazzi.model.Vote
import com.example.clazzi.model.VoteOption
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class VoteListViewModel: ViewModel() {
    val db = Firebase.firestore
    private val _voteList = MutableStateFlow<List<Vote>>(emptyList())
    val voteList: StateFlow<List<Vote>> = _voteList

    init {
        // 뷰모델 초기화 시 실시간 리스너 설정
        db.collection("votes")
            .addSnapshotListener { snapshot, error ->
                if(error != null) {
                    Log.e("FireStore", "Error getting votes", error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    _voteList.value = snapshot.toObjects(Vote::class.java)
                }
            }
    }

//    init {
//        _voteList.value = listOf (
//            Vote(
//                id = "1", title = "오늘 점심 뭐 먹을까요?", voteOptions = listOf(
//                    VoteOption(id = "1", optionText = "삼겹살"),
//                    VoteOption(id = "2", optionText = "치킨"),
//                    VoteOption(id = "3", optionText = "피자"),
//                )
//            ), Vote(
//                id = "2", title = "우리 반에서 제일 잘생긴 사람은??", voteOptions = listOf(
//                    VoteOption(id = "1", optionText = "김한수"),
//                    VoteOption(id = "2", optionText = "박명수"),
//                    VoteOption(id = "3", optionText = "유재석"),
//                )
//            ), Vote(
//                id = "3", title = "서핑 같이 가고 싶은 사람은?", voteOptions = listOf(
//                    VoteOption(id = "1", optionText = "정준하"),
//                    VoteOption(id = "2", optionText = "하하"),
//                )
//            )
//        )
//    }

    //ID로 특정 투표를 가져오는 메서드
    fun getVoteById(voteId: String): Vote? {
        return _voteList.value.find { it.id == voteId }
    }

    // 새로운 투표를 추가하는 메서드
    fun addVote(vote: Vote) {
//        _voteList.value += vote

        viewModelScope.launch {
            try {
                val voteMap = hashMapOf(
                    "id" to vote.id,
                    "title" to vote.title,
                    "createAt" to vote.createAt, // 서버 타임으로 설정ㄴ
                    "voteOptions" to vote.voteOptions.map {
                        hashMapOf(
                            "id" to it.id,
                            "optionText" to it.optionText,
                        )
                    }
                )
                db.collection("votes")
                    .document(vote.id)
                    .set(vote)
                    .await()
            } catch (e: Exception) {}
            // 에러 처리 (예: 사용자에게 토스트 메시지 표시)
        }
    }

    fun setVote(vote: Vote) {
        viewModelScope.launch {
            try{
                db.collection("votes")
                    .document(vote.id)
                    .set(vote)
                    .await()
                    Log.d("FireStore", "투표 성공")

            }
            catch (e: Exception) {
                Log.e("FireStore", "투표 실패 에러 발생")

            }

        }
    }
}