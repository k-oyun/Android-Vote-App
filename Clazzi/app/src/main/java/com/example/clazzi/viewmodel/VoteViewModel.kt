package com.example.clazzi.viewmodel

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clazzi.model.Vote
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VoteViewModel : ViewModel() {
    private  val _vote = MutableStateFlow<Vote?>(null)
    val vote: StateFlow<Vote?> = _vote

    fun loadVote(voteId : String) {
        Firebase.firestore.collection("votes").document(voteId)
            .addSnapshotListener { snapshot, error ->
                if(error != null) {
                    //오류 처리
                    return@addSnapshotListener
                }
                if(snapshot!=null) {
                    _vote.value = snapshot.toObject(Vote::class.java)
                }
            }
    }
}