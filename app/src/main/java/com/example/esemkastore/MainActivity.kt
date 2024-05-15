package com.example.esemkastore

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esemkastore.model.LoginRequest
import com.example.esemkastore.model.LoginResponse
import com.example.esemkastore.retrofit.RetrofitClient
import com.example.esemkastore.service.LoginApi
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
  private lateinit var etEmail: TextInputEditText
  private lateinit var etPassword: TextInputEditText
  private lateinit var btnLogin: MaterialButton
  private lateinit var sharedPrefences : SharedPreferences
  private fun initComponents() {
    etEmail = findViewById(R.id.et_email)
    etPassword = findViewById(R.id.et_password)
    btnLogin = findViewById(R.id.btn_login)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    sharedPrefences= getSharedPreferences("user_info", MODE_PRIVATE)

    val intent = Intent(this@MainActivity, HomeActivity::class.java)
    if(sharedPrefences.contains("email") && sharedPrefences.contains("password")) {
      intent.putExtra("name", sharedPrefences.getString("name", null))
      intent.putExtra("id", sharedPrefences.getInt("id", 1))
      startActivity(intent)
    }

    initComponents()
    setupListener()
    supportActionBar!!.title = "Login | Esemka Store"
    supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#E67E23")))
  }

  private fun setupListener() {
    btnLogin.setOnClickListener {
      if(TextUtils.isEmpty(etEmail.text.toString()) || TextUtils.isEmpty(etPassword.text.toString())) {
        Toast.makeText(this@MainActivity, "Input kosong, harap diisi!", Toast.LENGTH_SHORT).show()
      } else {
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val loginRequest = LoginRequest(email,password)
        login(loginRequest)
      }
    }
  }

  private fun login(loginReq: LoginRequest) {
    val apiService = RetrofitClient.retrofitInstance.create(LoginApi::class.java)
    val call = apiService.login(loginReq)
    call.enqueue(object: Callback<LoginResponse> {
      override fun onResponse(p0: Call<LoginResponse>, response: Response<LoginResponse>) {
        if(response.isSuccessful) {
          val result = response.body()
          Toast.makeText(this@MainActivity, "Login Success!", Toast.LENGTH_SHORT).show()
          val editor :  SharedPreferences.Editor = sharedPrefences.edit()
          editor.putString("email", loginReq.email)
          editor.putString("password", loginReq.password)
          editor.putString("name", result!!.name)
          editor.putInt("id", result.id)
          editor.apply()
          val intent = Intent(this@MainActivity, HomeActivity::class.java)
          intent.putExtra("login", result)
          intent.putExtra("name", result.name)
          startActivity(intent)
        } else {
          Toast.makeText(this@MainActivity, "Login Failed!", Toast.LENGTH_SHORT).show()
        }
      }

      override fun onFailure(p0: Call<LoginResponse>, p1: Throwable) {
        Toast.makeText(this@MainActivity, "Login Failed!", Toast.LENGTH_SHORT).show()

      }

    })
  }
}