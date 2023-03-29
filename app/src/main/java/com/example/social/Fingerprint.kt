package com.example.social

import android.content.Intent
import android.hardware.biometrics.BiometricManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import com.example.social.databinding.ActivityFingerprintBinding
import java.util.concurrent.Executor

class Fingerprint : AppCompatActivity() {
    private lateinit var binding : ActivityFingerprintBinding
    private lateinit var executor : Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo : BiometricPrompt.PromptInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFingerprintBinding.inflate(layoutInflater)
        setContentView(binding.root)
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this@Fingerprint,executor,object:BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Log.e("Biometric Auth Error : " , "$errorCode err String : $errString")
                Toast.makeText(this@Fingerprint,"Authentication Error",Toast.LENGTH_SHORT).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(this@Fingerprint,"Authentication Succeed",Toast.LENGTH_SHORT).show()
                val mainActivityIntent = Intent(this@Fingerprint, MainActivity::class.java)
                startActivity(mainActivityIntent)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(this@Fingerprint,"Authentication Failed",Toast.LENGTH_SHORT).show()
            }
        })
        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric Require")
            .setSubtitle("Login Using Fingeprint")
            .setNegativeButtonText("Use Password Instead")
            .build()
        binding.auth.setOnClickListener{
            biometricPrompt.authenticate(promptInfo)

        }
    }

}