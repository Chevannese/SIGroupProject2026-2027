package com.example.flowapp

import android.os.Bundle
import android.widget.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.flowapp.ui.theme.FlowAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // shows login when the app starts
        showLogin()
    }

    private fun showLogin() {
        // switch to login page
        setContentView(R.layout.login)

        // yoink register link button
        val toRegister: TextView = findViewById(R.id.registerBtn)
        val login: Button = findViewById(R.id.loginBtn)
        val toForgetPass: TextView = findViewById(R.id.forgetPassBtn)

        toRegister.setOnClickListener {
            showRegister()
        }

        toForgetPass.setOnClickListener {
            showForgetPassPage()
        }

        login.setOnClickListener {
            val trn: EditText = findViewById(R.id.loginTRN)
            val password: EditText = findViewById(R.id.loginPassword)
            val client = Client(applicationContext)

            val response = client.login(trn.text.toString(), password.text.toString())

            if (response == "OK") {
                setContent {
                    FlowAppTheme() {
                        Main(onLogoutClick = { showLogin() })
                    }
                }
            }
            else {
                Toast.makeText(applicationContext, "TRN or password is incorrect", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showRegister() {
        setContentView(R.layout.register)

        val toLogin: TextView = findViewById(R.id.accExist)
        val register: Button = findViewById(R.id.registerBtn)

        toLogin.setOnClickListener {
            showLogin()
        }

        register.setOnClickListener {
            register.isEnabled = false;

            val firstName: EditText = findViewById(R.id.reg_firstName)
            val lastName: EditText = findViewById(R.id.reg_lastName)
            val email: EditText = findViewById(R.id.reg_email)
            val phone: EditText = findViewById(R.id.reg_phone)
            val password: EditText = findViewById(R.id.reg_password)

            val client = Client(applicationContext)
            val response = client.register(firstName.text.toString(), lastName.text.toString(), password.text.toString(), email.text.toString(), phone.text.toString())

            if (response != "OK") {
                Toast.makeText(applicationContext, "Failed to register", Toast.LENGTH_SHORT).show()
            }

            register.isEnabled = true;
        }
    }

    
    private fun showForgetPassPage()
    {
        setContentView(R.layout.forget_pass_page)

        val sendCode: Button = findViewById(R.id.sendCodeBtn)
        val toLogin: Button = findViewById(R.id.backToLoginBtn)

        toLogin.setOnClickListener {
            showLogin()
        }
    }

}
