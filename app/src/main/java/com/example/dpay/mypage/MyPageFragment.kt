package com.example.dpay.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.dpay.R
import com.example.dpay.databinding.FragmentMypageBinding
import com.example.dpay.home.AddArticleActivity

class MyPageFragment: Fragment(R.layout.fragment_mypage) {

    private var binding: FragmentMypageBinding?= null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //생성된 뷰에 bind를 걸어주는 과정
        val fragmentMypageBinding = FragmentMypageBinding.bind(view)
        binding = fragmentMypageBinding

        //  버튼 클릭 시
        fragmentMypageBinding.profileupdateView.setOnClickListener {
            val intent = Intent(context, UpdateProfileActivity::class.java)
            startActivity(intent)
        }
    }
}