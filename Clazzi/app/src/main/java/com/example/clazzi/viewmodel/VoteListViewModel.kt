package com.example.clazzi.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clazzi.model.Vote
import com.example.clazzi.model.VoteOption
import com.example.clazzi.repository.FirebaseVoteRepository
import com.example.clazzi.repository.VoteRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class VoteListViewModel(
    val voteRepository: VoteRepository
) : ViewModel() {
    val db = Firebase.firestore

    private val _voteList = MutableStateFlow<List<Vote>>(emptyList())
    val voteList: StateFlow<List<Vote>> = _voteList

    init {
        viewModelScope.launch {
            voteRepository.observeVotes().collect { votes ->
                _voteList.value = votes
            }
        }
    }

    fun addVote(vote: Vote, context: Context, imageUri: Uri) {
        viewModelScope.launch {
            voteRepository.addVote(vote, context, imageUri)
        }
    }

    // 투표 데이터를 업데이트 하는 함수
    fun setVote(vote: Vote) {
        viewModelScope.launch {
            voteRepository.setVote(vote)
        }
    }
}












