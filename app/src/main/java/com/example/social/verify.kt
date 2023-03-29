package com.example.social

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.social.databinding.ActivityPhoneBinding
import com.example.social.databinding.ActivityVerifyBinding
import com.example.social.model.User
import com.example.social.model.daos.UserDao
import com.google.firebase.auth.*

class verify : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private lateinit var user : FirebaseUser
    private lateinit var binding: ActivityVerifyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()

        val storedVerificationId=intent.getStringExtra("storedVerificationId")

//        Reference
        val verify=binding.verifyBtn
        val otpGiven=binding.idOtp
      //  val name = binding.idName



        verify.setOnClickListener{
            var otp=otpGiven.text.toString().trim()
            if(!otp.isEmpty()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp)
                signInWithPhoneAuthCredential(credential)
            }else{
                Toast.makeText(this,"Enter OTP",Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    fun updateUI(user: FirebaseUser?) {
                        if(user != null) {
                            val user = User(user.uid, user.displayName, user.photoUrl.toString())
                            val usersDao = UserDao()
                            usersDao.addUser(user)}}
                    startActivity(Intent(applicationContext, Fingerprint::class.java))
                    finish()

                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                        Toast.makeText(this,"Invalid OTP",Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}