package com.example.clazzi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clazzi.model.Vote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VoteViewModel : ViewModel() {
    private  val _vote = MutableStateFlow<Vote?>(null)
    val vote: StateFlow<Vote?> = _vote

    fun loadVote(voteId : String, voteListViewModel: VoteListViewModel) {
        viewModelScope.launch {
            _vote.value  = voteListViewModel.getVoteById(voteId)
        }
    }
}