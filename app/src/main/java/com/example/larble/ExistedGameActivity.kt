package com.example.larble

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.larble.requestModel.GameCodeRequestModel
import com.example.larble.responseModel.LabyrinthResponseClass
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExistedGameActivity : AppCompatActivity() {
    private lateinit var duck: GifImageView
    private lateinit var game: Button
    private lateinit var searching: TextView
    private lateinit var code: EditText
    private lateinit var sh: SharedPreferences
    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_existedgame)

        game = findViewById(R.id.play)
        code = findViewById(R.id.Code_number)
        searching = findViewById(R.id.searching)
        duck = findViewById(R.id.duck)
        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        token = sh.getString("token", "").toString()

        game.setOnClickListener {
            if(code.text.toString()!=""){
                game.visibility = View.INVISIBLE
                searching.visibility = View.VISIBLE
                duck.visibility = View.VISIBLE
                intent = Intent(this, MultiPlayerGameActivity::class.java)
                val requestModel = GameCodeRequestModel(code.text.toString().toInt(),token)

                val response = ServiceBuilder.buildService(APIInterface::class.java)
                response.joinGame(requestModel).enqueue(
                    object: Callback<LabyrinthResponseClass> {
                        override fun onResponse(
                            call: Call<LabyrinthResponseClass>,
                            response: Response<LabyrinthResponseClass>
                        ){
                            if(response.body()!!.status=="true"){
                                intent.putExtra("number", code.text.toString())
                                intent.putExtra("labyrinth", response.body()!!.labyrinth)
                                startActivity(intent)
                            }else{
                                Toast.makeText(this@ExistedGameActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                    .show()
                                duck.visibility = View.INVISIBLE
                                searching.visibility = View.INVISIBLE
                                game.visibility = View.VISIBLE
                            }
                        }

                        override fun onFailure(call: Call<LabyrinthResponseClass>, t: Throwable) {
                            duck.visibility = View.INVISIBLE
                            searching.visibility = View.INVISIBLE
                            game.visibility = View.VISIBLE
                            intent = Intent(this@ExistedGameActivity, MainActivity::class.java)
                            startActivity(intent)
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
        duck.visibility = View.INVISIBLE
        searching.visibility = View.INVISIBLE
        game.visibility = View.VISIBLE
    }
}