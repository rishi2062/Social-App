package com.example.social

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.social.databinding.ActivityEncryptDecryptBinding
import org.checkerframework.checker.units.qual.Length
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class EncryptDecrypt : AppCompatActivity() {
    var SECRET_KEY = "2043202ae3fb2420ase23fj21d83er82"
    var SECRET_IV =  "2043202ae3fb2420"
    private lateinit var binding : ActivityEncryptDecryptBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEncryptDecryptBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.encrypt.setOnClickListener{
            val strEncrypt = binding!!.giveText.text.toString().encryptCBC()
            binding!!.outText.setText(strEncrypt)

        }

        binding.decrypt.setOnClickListener{
            val strDecrypt = binding.giveText.text.toString().decryptCBC()
            binding.outText.setText(strDecrypt)
        }

        binding.copy.setOnClickListener {
           try {
               val clipboard: ClipboardManager =
                   getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
               val clip = ClipData.newPlainText("label", binding.outText.text)
               clipboard.setPrimaryClip(clip)
               Toast.makeText(this, "Copied", Toast.LENGTH_LONG).show()
               binding.giveText.text = null
           }catch(ex: Exception){
               Log.e("Error : " ,ex.toString())
           }
        }
    }
    @OptIn(ExperimentalEncodingApi::class)
    private fun String.encryptCBC() : String{
        try {
            val iv = IvParameterSpec(SECRET_IV.toByteArray())
            val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv)
            val crypted = cipher.doFinal(this.toByteArray())
            val encodedByte = Base64.encode(crypted)
           // val encodedByte = android.util.Base64.encode(crypted, android.util.Base64.DEFAULT)
            return encodedByte
        }catch (e : Exception){
            Log.e("Not Encoded Error : ",e.toString())
            return ""}
    }
    @OptIn(ExperimentalEncodingApi::class)
    private fun String.decryptCBC() : String{
        try{
//            val decodedByte : ByteArray =  android.util.Base64.decode(this,android.util.Base64.DEFAULT)
            val decodedByte : ByteArray = Base64.decode(this)
            val iv = IvParameterSpec(SECRET_IV.toByteArray())
            val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(),"AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE,keySpec,iv)
            val decrypted = cipher.doFinal(decodedByte)
            return String(decrypted)
        }catch (e : Exception){
            Log.e("Not Decoded Error : ",e.toString())
            return ""
        }
    }
}