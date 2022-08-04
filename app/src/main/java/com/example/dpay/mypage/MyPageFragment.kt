package com.example.dpay.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.dpay.R
import com.example.dpay.databinding.FragmentMypageBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.Executor

class MyPageFragment: Fragment(R.layout.fragment_mypage) {

    private var binding: FragmentMypageBinding? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private var callbackManager: CallbackManager = CallbackManager.Factory.create()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentMypageBinding  = FragmentMypageBinding.bind(view)
        binding = fragmentMypageBinding

        fragmentMypageBinding.signInOutButton.setOnClickListener {
            binding?.let { binding ->
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                println(email)
                println(auth.currentUser)
                if(auth.currentUser == null) {
                    //로그인
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            println(task.isSuccessful)
                            if(task.isSuccessful){
                                successSignIn()
                            }else {
                                Toast.makeText(context, "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        }

                }else {
                    auth.signOut()
                    binding.emailEditText.text.clear()
                    binding.emailEditText.isEnabled = true
                    binding.passwordEditText.text.clear()
                    binding.passwordEditText.isEnabled = true

                    binding.signInOutButton.text = "로그인"
                    binding.signInOutButton.isEnabled = false
                    binding.signUpButton.isEnabled = false
                }
            }
        }

        fragmentMypageBinding.signUpButton.setOnClickListener {
            binding?.let { binding ->
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if(task.isSuccessful) {
                            Toast.makeText(context, "회원가입에 성공하셨습니다. 로그인이 유지됩니다.", Toast.LENGTH_SHORT).show()
                            binding?.emailEditText?.isEnabled = false
                            binding?.passwordEditText?.isEnabled = false
                            binding?.signUpButton?.isEnabled = false
                            binding?.signInOutButton?.text = "로그아웃"

                        }else {
                            Toast.makeText(context, "회원가입 실패. 이미 가입한 이메일일 수 있습니다.", Toast.LENGTH_SHORT).show()
                        }

                    }
            }
        }

        fragmentMypageBinding.emailEditText.addTextChangedListener {
            binding?.let { binding ->
                val enable = binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
                binding.signInOutButton.isEnabled = enable
                binding.signUpButton.isEnabled = enable
            }
        }

        fragmentMypageBinding.passwordEditText.addTextChangedListener {
            binding?.let { binding ->
                val enable = binding.emailEditText.text.isNotEmpty() && binding.passwordEditText.text.isNotEmpty()
                binding.signInOutButton.isEnabled = enable
                binding.signUpButton.isEnabled = enable
            }
        }

        fragmentMypageBinding.facebookLoginButton.setOnClickListener {
            binding?.let { binding->
                binding.facebookLoginButton.setPermissions("email", "public_profile")
                binding.facebookLoginButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult>{
                    override fun onSuccess(result: LoginResult) {
                        //로그인 성공
                        val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if(task.isSuccessful){
                                    binding?.emailEditText?.isEnabled = false
                                    binding?.passwordEditText?.isEnabled = false
                                    binding?.signUpButton?.isEnabled = false
                                    binding?.signInOutButton?.text = "로그아웃"
                                }
                                else {
                                    Toast.makeText(context,"페이스북 로그인이 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }

                    override fun onCancel() {
                        //로그인하다 취소했을 때
                    }


                    override fun onError(error: FacebookException) {
                        Toast.makeText(context,"페이스북 로그인이 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }


                })
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if(auth.currentUser == null){
            binding?.let { binding ->
                binding.emailEditText.text.clear()
                binding.passwordEditText.text.clear()
                binding.emailEditText.isEnabled = true
                binding.passwordEditText.isEnabled = true
                binding.signInOutButton.text = "로그인"
                binding.signInOutButton.isEnabled = false
                binding.signUpButton.isEnabled = false
            }
        }else {
            binding?.let { binding ->
                binding.emailEditText.setText(auth.currentUser!!.email)
                binding.passwordEditText.setText("************")
                binding.emailEditText.isEnabled = false
                binding.passwordEditText.isEnabled = false
                binding.signInOutButton.text = "로그아웃"
                binding.signInOutButton.isEnabled = true
                binding.signUpButton.isEnabled = false
            }
        }
    }

    private fun successSignIn(){
        if(auth.currentUser == null) {
            Toast.makeText(context, "로그인에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        binding?.emailEditText?.isEnabled = false
        binding?.passwordEditText?.isEnabled = false
        binding?.signUpButton?.isEnabled = false
        binding?.signInOutButton?.text = "로그아웃"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

}