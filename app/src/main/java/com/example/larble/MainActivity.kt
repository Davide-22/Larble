package com.example.larble

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        verify()
    }

    private fun verify(){
        intent = Intent(this@MainActivity, LoginActivity::class.java)
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val token: String? = sh.getString("token", "")
        val requestModel = token?.let { TokenRequestModel(it) }

        val response = ServiceBuilder.buildService(APIInterface::class.java)
        if (requestModel != null) {
            response.verify(requestModel).enqueue(
                object: Callback<ResponseClass> {
                    override fun onResponse(
                        call: Call<ResponseClass>,
                        response: Response<ResponseClass>
                    ){
                        if(response.body()!!.status=="true"){
                            intent = Intent(this@MainActivity, MenuActivity::class.java)
                        }
                        startActivity(intent)
                    }
                    override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                        Toast.makeText(this@MainActivity, t.toString(), Toast.LENGTH_LONG)
                            .show()
                    }
                }
            )
        }
    }
}