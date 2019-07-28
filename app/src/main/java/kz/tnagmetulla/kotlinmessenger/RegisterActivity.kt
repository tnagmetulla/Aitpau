package kz.tnagmetulla.kotlinmessenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        register_button_registration.setOnClickListener {
            performRegister()
        }
        val intent = Intent(this, SignInActivity::class.java)
        login_textView.setOnClickListener {
            startActivity(intent)
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

                }
                .addOnFailureListener{
                    Toast.makeText(this, "Something went wrong:${it.message}!", Toast.LENGTH_SHORT).show()

                }
            Log.d("Register:", "username is: $username")
            Log.d("Register:", "email is: $email")
            Log.d("Register:", "password is: $password")
        }
    }
}
