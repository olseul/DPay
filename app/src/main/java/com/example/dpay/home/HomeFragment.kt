package com.example.dpay.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dpay.R
import com.example.dpay.chatdetail.ChatItem
import com.example.dpay.chatlist.ChatListItem
import com.example.dpay.databinding.FragmentHomeBinding
import com.example.dpay.mypage.DBkey.Companion.CHILD_CHAT
import com.example.dpay.mypage.DBkey.Companion.DB_ARTICLES
import com.example.dpay.mypage.DBkey.Companion.DB_USERS
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var articleDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
    private lateinit var articleAdapter: ArticleAdapter

    private val articleList = mutableListOf<ArticleModel>()
    private val listener = object: ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return

            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}

    }

    private var binding: FragmentHomeBinding?= null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    // 프래그먼트 뷰가 생성될 시
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //이름 입력
        userDB = Firebase.database.reference.child("Users")
        val currentUserDB = userDB.child(getCurrentUserID())
        currentUserDB.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("name").value == null){
                    showNameInputPopup()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        //생성된 뷰에 bind를 걸어주는 과정
        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        //디비 가져오기
        articleList.clear()
        userDB = Firebase.database.reference.child(DB_USERS)
        articleDB = Firebase.database.reference.child(DB_ARTICLES)
        articleAdapter = ArticleAdapter(onItemClicked = { articleModel ->
            if (auth.currentUser!!.uid != articleModel.writerId) {
                val chatRoom = ChatListItem(
                    readerId = auth.currentUser!!.uid,
                    writerId = articleModel.writerId,
                    title = articleModel.title,
                    key = auth.currentUser!!.uid + articleModel.writerId + articleModel.articleId
                )

                userDB.child(auth.currentUser!!.uid)
                        //uid를 키?
                    .child(CHILD_CHAT)
                    .child(auth.currentUser!!.uid + articleModel.writerId + articleModel.articleId)
                    .setValue(chatRoom)

                userDB.child(articleModel.writerId)
                    .child(CHILD_CHAT)
                    .child(auth.currentUser!!.uid + articleModel.writerId + articleModel.articleId)
                    .setValue(chatRoom)

                Snackbar.make(view, "채팅방이 생성되었습니다. 채팅탭에서 확인해주세요.", Snackbar.LENGTH_LONG).show()

            } else {
                //내가 올린 글일 때
                Snackbar.make(view, "본인이 올린 글입니다.", Snackbar.LENGTH_LONG).show()
            }
        })

        // articleRecyclerView을 걸어주면, 스크롤 되어 화면에서 벗어나도 뷰를 제거하지 않고 재사용한다.
        fragmentHomeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerView.adapter = articleAdapter

        // 글 등록 버튼 클릭 시
        fragmentHomeBinding.addFloatingButton.setOnClickListener {
            val intent = Intent(context, AddArticleActivity::class.java)
            startActivity(intent)
        }
        articleDB.addChildEventListener(listener)
    }

    private fun showNameInputPopup() {
        val editText = EditText(context)

        AlertDialog.Builder(context)
            .setTitle("이름을 입력해주세요")
            .setView(editText)
            .setPositiveButton("저장"){ _, _ ->
                if(editText.text.isEmpty()){
                    showNameInputPopup()
                } else {
                    saveUserName(editText.text.toString())
                }
            }
            .setCancelable(false)
            .show()
    }

    private fun saveUserName(name: String){
        val userId = getCurrentUserID()
        val currentUserDB = userDB.child(userId)
        val user = mutableMapOf<String, Any>()
        user["userId"] = userId
        user["name"] = name
        currentUserDB.updateChildren(user)
    }

    private fun  getCurrentUserID(): String {
        if(auth.currentUser == null){
            Toast.makeText(context, "로그인을 먼저 해주세요", Toast.LENGTH_SHORT).show()
        }

        return auth.currentUser?.uid.orEmpty()
    }

    override fun onResume() {
        super.onResume()

        articleAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        articleDB.removeEventListener(listener)
    }
}