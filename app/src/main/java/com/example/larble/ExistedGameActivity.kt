package com.example.larble

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExistedGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_existedgame)

        val game: Button = findViewById(R.id.play)
        val code: EditText = findViewById(R.id.Code_number)
        val searching: TextView = findViewById(R.id.searching)
        searching.visibility = View.INVISIBLE
        val duck: GifImageView = findViewById(R.id.duck)
        duck.visibility = View.INVISIBLE


        game.setOnClickListener {
            game.visibility = View.INVISIBLE
            searching.visibility = View.VISIBLE
            duck.visibility = View.VISIBLE
            intent = Intent(this, MultiPlayerGameActivity::class.java)
            val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
            val token: String = sh.getString("token", "").toString()
            val requestModel = GameCodeRequestModel(code.text.toString().toInt(),token)

            val response = ServiceBuilder.buildService(APIInterface::class.java)
            response.joinGame(requestModel).enqueue(
                object: Callback<ResponseClass> {
                    override fun onResponse(
                        call: Call<ResponseClass>,
                        response: Response<ResponseClass>
                    ){
                        if(response.body()!!.status=="true"){
                            startActivity(intent)
                        }else{
                            Toast.makeText(this@ExistedGameActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                .show()
                            duck.visibility = View.INVISIBLE
                            searching.visibility = View.INVISIBLE
                            game.visibility = View.VISIBLE
                        }
                    }
                    override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                        Toast.makeText(this@ExistedGameActivity, t.toString(), Toast.LENGTH_LONG)
                            .show()
                        duck.visibility = View.INVISIBLE
                        searching.visibility = View.INVISIBLE
                        game.visibility = View.VISIBLE
                    }
                }
            )
        }

    }
}