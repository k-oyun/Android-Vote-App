package com.example.clazzi.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.clazzi.model.Vote
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseVoteRepository : VoteRepository {
    val db = Firebase.firestore

    override fun observeVotes(): Flow<List<Vote>> = callbackFlow {
        val listener = db.collection("votes")
            .orderBy("createAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Error getting votes", error)
                    close(error)
                } else if (snapshot != null) {
                    val votes = snapshot.toObjects(Vote::class.java)
                    trySend(votes)

                }
            }
        awaitClose { listener.remove() }

    }

    override suspend fun addVote(
        vote: Vote,
        context: Context,
        imageUri: Uri
    ) {
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
                },
                "deadline" to vote.deadline,
            )
            // Firestore에 저장
            db.collection("votes")
                .document(vote.id)
                .set(voteMap)  // Map으로 저장
                .await()
        } catch (e: Exception) {
            // 에러 처리 (예: 사용자에게 토스트 메시지 표시)
        }
    }

    override suspend fun setVote(vote: Vote) {
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

    override fun observeVoteById(voteId: String): Flow<Vote?> = callbackFlow {
        val listener = db.collection("votes")
            .document(voteId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.toObject(Vote::class.java))
                } else {
                    trySend(null)
                }

            }
        awaitClose { listener.remove() }
    }
}