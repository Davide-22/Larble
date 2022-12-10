package com.example.larble

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Base64
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.larble.requestModel.PasswordRequestModel
import com.example.larble.requestModel.ProfileRequestModel
import com.example.larble.requestModel.UsernameRequestModel
import com.example.larble.responseModel.ResponseClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class AccountActivity: AppCompatActivity()  {

    private var picture: ImageView? = null
    private var sh: SharedPreferences? = null
    private var token: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        token = sh!!.getString("token", "")

        val changeUsername: ImageButton = findViewById(R.id.edit_username)
        val changePassword: Button = findViewById(R.id.edit_password)
        val rootLayout = findViewById<ConstraintLayout>(R.id.accountLayout)
        val logout: Button = findViewById(R.id.log_out)
        val photo: TextView = findViewById(R.id.plus)

        var text : TextView = findViewById(R.id.username)
        var username: String? = sh!!.getString("username", "")
        "Username: $username".also { text.text = it }
        text = findViewById(R.id.email)
        var insert: String? = intent.getStringExtra("email")
        "Email: $insert".also { text.text = it }
        text = findViewById(R.id.wins)
        insert = intent.getStringExtra("wins")
        "Wins: $insert".also { text.text = it }
        text = findViewById(R.id.total_games)
        insert= intent.getStringExtra("total_games")
        "TotalGames: $insert".also { text.text = it }
        text = findViewById(R.id.score)
        insert= intent.getStringExtra("score")
        "Score: $insert".also { text.text = it }

        insert= intent.getStringExtra("profile_picture")
        picture= findViewById(R.id.photo)
        if(insert != null) {
            val decodeImage: ByteArray = Base64.decode(insert, Base64.DEFAULT)
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodeImage, 0, decodeImage.size)
            picture?.setImageBitmap(bitmap)
        }

        val inflater: LayoutInflater = getSystemService( Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.popup_window,null)

        val popupWindow = PopupWindow(
            view,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
        popupWindow.isFocusable = true
        popupWindow.elevation = 10.0F

        val slideIn = Slide()
        slideIn.slideEdge = Gravity.TOP
        popupWindow.enterTransition = slideIn

        val slideOut = Slide()
        slideOut.slideEdge = Gravity.END
        popupWindow.exitTransition = slideOut

        photo.setOnClickListener{
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent,1)
        }

        logout.setOnClickListener {
            intent = Intent(this, LoginActivity::class.java)
            val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()
            startActivity(intent)
        }

        changeUsername.setOnClickListener{

            val newUsername: EditText = view.findViewById(R.id.new_username)
            username = sh!!.getString("username", "")
            newUsername.setText(username)
            changePopUp(view,1)

            val change: Button = view.findViewById(R.id.change_username)
            val checkUsername: ImageView = view.findViewById(R.id.check1)

            change.setOnClickListener {
                if(checkUsername.visibility == View.VISIBLE){
                    val requestModel = token?.let { it1 -> UsernameRequestModel(newUsername.text.toString(),it1) }

                    val response = ServiceBuilder.buildService(APIInterface::class.java)
                    if (requestModel != null) {
                        response.changeUsername(requestModel).enqueue(
                            object: Callback<ResponseClass> {
                                override fun onResponse(
                                    call: Call<ResponseClass>,
                                    response: Response<ResponseClass>
                                ){
                                    if(response.body()!!.status == "false"){
                                        Toast.makeText(this@AccountActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                            .show()
                                    }else{
                                        text = findViewById(R.id.username)
                                        "Username: ${newUsername.text}".also { text.text = it }
                                        val myEdit = sh!!.edit()
                                        myEdit.putString("username", newUsername.text.toString())
                                        myEdit.apply()
                                        popupWindow.dismiss()
                                        checkUsername.visibility = View.INVISIBLE
                                        Toast.makeText(this@AccountActivity, "Username successfully changed", Toast.LENGTH_LONG).show()
                                    }
                                }
                                override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                                    Toast.makeText(this@AccountActivity, t.toString(), Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                        )
                    }
                }else{
                    Toast.makeText(this@AccountActivity, "Username same as before", Toast.LENGTH_LONG)
                        .show()
                }
            }

            TransitionManager.beginDelayedTransition(rootLayout)
            popupWindow.showAtLocation(
                rootLayout, // Location to display popup window
                Gravity.CENTER, // Layout position to display popup
                0, // X offset
                0 // Y offset
            )

            newUsername.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    if(s.toString() == username) checkUsername.visibility = View.INVISIBLE
                    else checkUsername.visibility = View.VISIBLE
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                }
            })
        }

        changePassword.setOnClickListener {

            changePopUp(view,2)

            val oldPassword: EditText= view.findViewById(R.id.old_password)
            val newPassword: EditText= view.findViewById(R.id.new_password)
            val confirmPassword: EditText= view.findViewById(R.id.confirm_password)
            val change: Button = view.findViewById(R.id.change_password)
            val checkPassword: ImageView = view.findViewById(R.id.check2)

            change.setOnClickListener {
                if(checkPassword.visibility == View.VISIBLE){
                    val requestModel = token?.let { it1 -> PasswordRequestModel(it1, oldPassword.text.toString(), newPassword.text.toString()) }

                    val response = ServiceBuilder.buildService(APIInterface::class.java)
                    if (requestModel != null) {
                        response.changePassword(requestModel).enqueue(
                            object: Callback<ResponseClass> {
                                override fun onResponse(
                                    call: Call<ResponseClass>,
                                    response: Response<ResponseClass>
                                ){
                                    if(response.body()!!.status == "false"){
                                        Toast.makeText(this@AccountActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                            .show()
                                    }else{
                                        popupWindow.dismiss()
                                        checkPassword.visibility = View.INVISIBLE
                                        oldPassword.setText("")
                                        newPassword.setText("")
                                        confirmPassword.setText("")
                                        Toast.makeText(this@AccountActivity, "Password successfully changed", Toast.LENGTH_LONG).show()
                                    }
                                }
                                override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                                    Toast.makeText(this@AccountActivity, t.toString(), Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                        )
                    }
                }else{
                    Toast.makeText(this@AccountActivity, "Invalid password entered", Toast.LENGTH_LONG).show()
                }
            }

            TransitionManager.beginDelayedTransition(rootLayout)
            popupWindow.showAtLocation(
                rootLayout, // Location to display popup window
                Gravity.CENTER, // Layout position to display popup
                0, // X offset
                0 // Y offset
            )

            confirmPassword.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    if(s.toString() == newPassword.text.toString() && newPassword.text.toString() !="") checkPassword.visibility = View.VISIBLE
                    else checkPassword.visibility = View.INVISIBLE
                }

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                }
            })
        }
    }

    private fun changePopUp(view: View, type: Int){

        val newUsername: EditText = view.findViewById(R.id.new_username)
        val changeUsername: Button = view.findViewById(R.id.change_username)

        val oldPassword: EditText= view.findViewById(R.id.old_password)
        val newPassword: EditText= view.findViewById(R.id.new_password)
        val confirmPassword: EditText= view.findViewById(R.id.confirm_password)
        val changePassword: Button = view.findViewById(R.id.change_password)

        if(type == 1){
            newUsername.visibility = View.VISIBLE
            changeUsername.visibility = View.VISIBLE
            oldPassword.visibility = View.INVISIBLE
            newPassword.visibility = View.INVISIBLE
            confirmPassword.visibility = View.INVISIBLE
            changePassword.visibility = View.INVISIBLE
        }else{
            newUsername.visibility = View.INVISIBLE
            changeUsername.visibility = View.INVISIBLE
            oldPassword.visibility = View.VISIBLE
            newPassword.visibility = View.VISIBLE
            confirmPassword.visibility = View.VISIBLE
            changePassword.visibility = View.VISIBLE
        }

    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && resultCode == RESULT_OK){
            val bundle: Bundle? = data?.extras
            val photo: Bitmap = bundle?.get("data") as Bitmap
            val output = ByteArrayOutputStream()
            photo.compress(Bitmap.CompressFormat.WEBP_LOSSY, 100, output)
            val imageBytes: ByteArray = output.toByteArray()
            val encodedImage: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            val requestModel = token?.let { ProfileRequestModel(it, encodedImage) }

            val response = ServiceBuilder.buildService(APIInterface::class.java)
            if (requestModel != null) {
                response.insertPicture(requestModel).enqueue(
                    object: Callback<ResponseClass> {
                        override fun onResponse(
                            call: Call<ResponseClass>,
                            response: Response<ResponseClass>
                        ){
                            if(response.body()!!.status == "false"){
                                Toast.makeText(this@AccountActivity, response.body()!!.msg, Toast.LENGTH_LONG)
                                    .show()
                            }else{
                                picture?.setImageBitmap(photo)
                                Toast.makeText(this@AccountActivity, "Profile picture successfully changed", Toast.LENGTH_LONG).show()
                            }
                        }
                        override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                            Toast.makeText(this@AccountActivity, t.toString(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                )
            }
        }
    }

}