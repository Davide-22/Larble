package com.example.larble

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
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
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.larble.requestModel.PasswordRequestModel
import com.example.larble.requestModel.ProfileRequestModel
import com.example.larble.requestModel.UsernameRequestModel
import com.example.larble.responseModel.PlayerResponseClass
import com.example.larble.responseModel.ResponseClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class AccountActivity: AppCompatActivity()  {

    private lateinit var picture: ImageView
    private lateinit var sh: SharedPreferences
    private lateinit var token: String
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        token = sh.getString("token", "").toString()

        val changeUsername: ImageView = findViewById(R.id.edit_username)
        val changePassword: Button = findViewById(R.id.edit_password)
        val rootLayout: ConstraintLayout = findViewById(R.id.accountLayout)
        val logout: Button = findViewById(R.id.log_out)
        val photo: TextView = findViewById(R.id.plus)

        val account: PlayerResponseClass = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra("account", PlayerResponseClass::class.java)!!
        else
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("account") as PlayerResponseClass

        var text : TextView = findViewById(R.id.username)
        var username: String = sh.getString("username", "").toString()
        "Username: $username".also { text.text = it }
        text = findViewById(R.id.email)
        "Email: ${account.email}".also { text.text = it }
        text = findViewById(R.id.wins)
        "Wins: ${account.wins}".also { text.text = it }
        text = findViewById(R.id.total_games)
        "TotalGames: ${account.total_games}".also { text.text = it }
        text = findViewById(R.id.score)
        "Score: ${account.score}".also { text.text = it }

        picture= findViewById(R.id.photo)
        if(account.profile_picture != null) {
            val decodeImage: ByteArray = Base64.decode(account.profile_picture, Base64.DEFAULT)
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodeImage, 0, decodeImage.size)
            picture.setImageBitmap(bitmap)
        }

        val inflater: LayoutInflater = getSystemService( Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val nullParent: ViewGroup? = null
        val view = inflater.inflate(R.layout.popup_window,nullParent,false)

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

        val camera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            if(result.resultCode == RESULT_OK){
                val bundle: Bundle? = result.data?.extras
                @Suppress("DEPRECATION")
                val photo1: Bitmap = bundle?.get("data") as Bitmap
                val output = ByteArrayOutputStream()
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    photo1.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, output)
                else
                    @Suppress("DEPRECATION")
                    photo1.compress(Bitmap.CompressFormat.WEBP, 0, output)
                val imageBytes: ByteArray = output.toByteArray()
                val encodedImage: String = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                val requestModel = ProfileRequestModel(token, encodedImage)

                val response = ServiceBuilder.buildService(APIInterface::class.java)
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
                                picture.setImageBitmap(photo1)
                                Toast.makeText(this@AccountActivity, "Profile picture successfully changed", Toast.LENGTH_LONG).show()
                            }
                        }
                        override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                            intent = Intent(this@AccountActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                )
            }
        }

        photo.setOnClickListener{
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            camera.launch(cameraIntent)
        }

        logout.setOnClickListener {
            intent = Intent(this, LoginActivity::class.java)
            sh.edit().clear().apply()
            startActivity(intent)
        }

        changeUsername.setOnClickListener{

            val newUsername: EditText = view.findViewById(R.id.new_username)
            username = sh.getString("username", "").toString()
            newUsername.setText(username)
            changePopUp(view,1)

            val change: Button = view.findViewById(R.id.change_username)
            val checkUsername: ImageView = view.findViewById(R.id.check1)

            change.setOnClickListener {
                if(checkUsername.visibility == View.VISIBLE){
                    val requestModel = UsernameRequestModel(newUsername.text.toString(), token)

                    val response = ServiceBuilder.buildService(APIInterface::class.java)
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
                                    val myEdit = sh.edit()
                                    myEdit.putString("username", newUsername.text.toString())
                                    myEdit.apply()
                                    popupWindow.dismiss()
                                    checkUsername.visibility = View.INVISIBLE
                                    Toast.makeText(this@AccountActivity, "Username successfully changed", Toast.LENGTH_LONG).show()
                                }
                            }
                            override fun onFailure(call: Call<ResponseClass>, t: Throwable) {
                                intent = Intent(this@AccountActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    )
                }else{
                    Toast.makeText(this@AccountActivity, "Username same as before", Toast.LENGTH_LONG)
                        .show()
                }
            }

            TransitionManager.beginDelayedTransition(rootLayout)
            popupWindow.showAtLocation(
                rootLayout,
                Gravity.CENTER,
                0,
                0
            )

            newUsername.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable) {
                    if(s.toString() == username) checkUsername.visibility = View.INVISIBLE
                    else checkUsername.visibility = View.VISIBLE
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
                    val requestModel = PasswordRequestModel(token, oldPassword.text.toString(), newPassword.text.toString())

                    val response = ServiceBuilder.buildService(APIInterface::class.java)
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
                                intent = Intent(this@AccountActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    )
                }else{
                    Toast.makeText(this@AccountActivity, "Invalid password entered", Toast.LENGTH_LONG).show()
                }
            }

            TransitionManager.beginDelayedTransition(rootLayout)
            popupWindow.showAtLocation(
                rootLayout,
                Gravity.CENTER,
                0,
                0
            )

            confirmPassword.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    if(s.toString() == newPassword.text.toString() && newPassword.text.toString() !="") checkPassword.visibility = View.VISIBLE
                    else checkPassword.visibility = View.INVISIBLE
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                }
            })
        }

        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                intent = Intent(this@AccountActivity, MenuActivity::class.java)
                startActivity(intent)
            }
        })
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
}