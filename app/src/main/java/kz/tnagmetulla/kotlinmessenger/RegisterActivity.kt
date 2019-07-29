package kz.tnagmetulla.kotlinmessenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_sign_up.*
import models.User
import java.net.URI
import java.util.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val intent = Intent(this, SignInActivity::class.java)
        login_textView.setOnClickListener {
            startActivity(intent)
        }

        register_button_registration.setOnClickListener {
            performRegister()
        }

        choose_photo_button_registration.setOnClickListener {
            selectAndShowPhoto()
        }
    }

    var selectedPhotoUri: Uri? = null

    fun selectAndShowPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            avatar_imageView_registration.setImageBitmap(bitmap)
            choose_photo_button_registration.alpha = 0F
        }
    }

    fun performRegister() {
        val username = username_editText_registration.text
        val email = email_editText_registration.text.toString()
        val password = password_editText_registration.text.toString()

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Please, fill all fields to Sign up", Toast.LENGTH_SHORT).show()
        } else {
            val auth = FirebaseAuth.getInstance()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener
                    Toast.makeText(this, "Congratulations! You are in Aitpau!", Toast.LENGTH_SHORT).show()
                    Log.d("Register:", "succesfully created user: ${it.result?.user?.uid}")
                    uploadImageToFirebaseDB()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Something went wrong:${it.message}!", Toast.LENGTH_SHORT).show()

                }
        }
    }

    fun uploadImageToFirebaseDB() {
        if (selectedPhotoUri == null) {
            Log.d("Register", "Something wrong with uri")
            return
        }
        val filename = UUID.randomUUID().toString()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReference("images/$filename")

        storageRef.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Register:", "Uploaded a photo successfully")
                storageRef.downloadUrl.addOnSuccessListener {

                    saveUserToFirebaseDB(it)
                }
            }
            .addOnFailureListener {
                Log.d("Register:", it.message)
            }


    }

    fun saveUserToFirebaseDB(avatar: Uri){
        val uid = FirebaseAuth.getInstance().uid
        val database = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid!!,username_editText_registration.text.toString(), avatar.toString())

        database.setValue(user)
            .addOnSuccessListener {
                Log.d("Register","All user data saved successfully")
            }
            .addOnFailureListener{
                Log.d("Register",it.message)
            }
    }
}
