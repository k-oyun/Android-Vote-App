package com.example.clazzi.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clazzi.model.Vote
import com.example.clazzi.model.VoteOption
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class VoteListViewModel : ViewModel() {
    private val _voteList = MutableStateFlow<List<Vote>>(emptyList())
    val voteList: StateFlow<List<Vote>> = _voteList

    init {
        _voteList.value = listOf(
            Vote(
                id = "1", title = "오늘 점심 뭐 먹을까요?", voteOptions = listOf(
                    VoteOption(id = "1", optionText = "삼겹살"),
                    VoteOption(id = "2", optionText = "치킨"),
                    VoteOption(id = "3", optionText = "피자"),
                )
            ), Vote(
                id = "2", title = "우리 반에서 제일 잘생긴 사람은??", voteOptions = listOf(
                    VoteOption(id = "1", optionText = "김한수"),
                    VoteOption(id = "2", optionText = "박명수"),
                    VoteOption(id = "3", optionText = "유재석"),
                )
            ), Vote(
                id = "3", title = "서핑 같이 가고 싶은 사람은?", voteOptions = listOf(
                    VoteOption(id = "1", optionText = "정준하"),
                    VoteOption(id = "2", optionText = "하하"),
                )
            )
        )
    }

    // ID로 특정 투표를 가져오는 메서드
    fun getVoteById(voteId: String): Vote? {
        return _voteList.value.find {it.id == voteId}
    }

    // 새로운 투표를 추가하는 메서드
    fun addVote(vote: Vote) {
//        _voteList.value += vote
        val db = Firebase.firestore
        viewModelScope.launch {
            try {
                db.collection("votes")
                    .document(vote.id)
                    .set(vote)
                    .await()
            } catch(e: Exception) {
                // 에러 처리 : 사용자에게 토스트 메시지 표시
            }

        }

    }

}