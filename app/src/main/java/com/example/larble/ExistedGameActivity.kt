package com.example.larble

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.larble.requestModel.GameCodeRequestModel
import com.example.larble.responseModel.ResponseClass
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExistedGameActivity : AppCompatActivity() {
    private var duck: GifImageView? = null
    private var game: Button? = null
    private var searching: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_existedgame)

        game = findViewById(R.id.play)
        val code: EditText = findViewById(R.id.Code_number)
        searching = findViewById(R.id.searching)
        searching?.visibility = View.INVISIBLE
        duck = findViewById(R.id.duck)
        duck?.visibility = View.INVISIBLE
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val token: String = sh.getString("token", "").toString()

        game?.setOnClickListener {
            if(code.text.toString()!=""){
                game?.visibility = View.INVISIBLE
                searching?.visibility = View.VISIBLE
                duck?.visibility = View.VISIBLE
                intent = Intent(this, MultiPlayerGameActivity::class.java)
                val requestModel = GameCodeRequestModel(code.text.toString().toInt(),token)

                val response = ServiceBuilder.buildService(APIInterface::class.java)
                response.joinGame(requestModel).enqueue(
                    object: Callback<ResponseClass> {
                        override fun onResponse(
                            call: Call<ResponseClass>,
                            response: Response<ResponseClass>
                        ){
                            if(response.body()!!.status=="true"){
                                intent.putExtra("number", code.text.toString())
                                startActivity(intent)
                            }else{
                                Toast.makeText(this@ExistedGameActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                    .show()
                                duck?.visibility = View.INVISIBLE
                                searching?.visibility = View.INVISIBLE
                                game?.visibility = View.VISIBLE
                            }
                        }
                        override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                            Toast.makeText(this@ExistedGameActivity, t.toString(), Toast.LENGTH_LONG)
                                .show()
                            duck?.visibility = View.INVISIBLE
                            searching?.visibility = View.INVISIBLE
                            game?.visibility = View.VISIBLE
                        }
                    }
                )
            }else{
                Toast.makeText(this@ExistedGameActivity, "Insert a code", Toast.LENGTH_LONG)
                    .show()
            }
        }

    }

    override fun onResume(){
        super.onResume()
        duck?.visibility = View.INVISIBLE
        searching?.visibility = View.INVISIBLE
        game?.visibility = View.VISIBLE
    }
}