package com.example.social

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.social.databinding.ActivitySignInBinding
import com.example.social.model.User
import com.example.social.model.daos.UserDao
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class SignInActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignInBinding
//    private val binding get() = _binding!!
    // [START declare_auth]
//    private lateinit var auth: FirebaseAuth
//    // [END declare_auth]

        private val RC_SIGN_IN: Int = 123
        private val TAG = "SignInActivity Tag"
        private lateinit var googleSignInClient: GoogleSignInClient
        private lateinit var auth: FirebaseAuth

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivitySignInBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Configure Google Sign In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(this, gso)
            auth = Firebase.auth

            binding.signInButton.setOnClickListener {
                signIn()
            }

        }

        override fun onStart() {
            super.onStart()
            val currentUser = auth.currentUser
            updateUI(currentUser)
        }

        private fun signIn() {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
            Log.d(TAG, "firebaseAuthWithGoogle:")
        }

        @Deprecated("Deprecated in Java")
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == RC_SIGN_IN) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
                Log.d(TAG, "firebaseAuthWithGoogleaayaaaya:")
            }
        }

        private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
            try {
                val account = completedTask.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
                binding.google.visibility  = View.GONE
            } catch (e: ApiException) {
                Log.w(TAG, "signInResult:failed code=" + e.statusCode)

            }
        }

        @OptIn(DelicateCoroutinesApi::class)
        private fun firebaseAuthWithGoogle(idToken: String) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            binding.signInButton.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            GlobalScope.launch(Dispatchers.IO) {
                val auth = auth.signInWithCredential(credential).await()
                val user = auth.user
                withContext(Dispatchers.Main) {
                    updateUI(user)
                }
            }

        }

        private fun updateUI(user: FirebaseUser?) {
            if(user != null) {
                val user = User(user.uid, user.displayName, user.photoUrl.toString())
                val usersDao = UserDao()
                usersDao.addUser(user)

                val mainActivityIntent = Intent(this, MainActivity::class.java)
                startActivity(mainActivityIntent)
                finish()
            } else {
                binding.signInButton.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }