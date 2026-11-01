package com.example.flowapp

import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.core.widget.doAfterTextChanged
import com.example.flowapp.ui.theme.FlowAppTheme
import java.io.File

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
        val file = File(applicationContext.filesDir, "users.txt")

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

            when (response) {
                LoginCode.NOT_FOUND -> Toast.makeText(applicationContext, "TRN or password is incorrect", Toast.LENGTH_SHORT).show()
                LoginCode.ERROR -> Toast.makeText(applicationContext, "Unknown error logging in", Toast.LENGTH_SHORT).show()
                LoginCode.OK -> {
                    setContent {
                        FlowAppTheme() {
                            Main(onLogoutClick = { showLogin() })
                        }
                    }
                }
            }
        }
    }

    private fun showRegister() {
        setContentView(R.layout.register)

        val name: EditText = findViewById(R.id.name)
        val trn: EditText = findViewById(R.id.trn)
        val email: EditText = findViewById(R.id.reg_email)
        val phone: EditText = findViewById(R.id.reg_phone)
        val password: EditText = findViewById(R.id.reg_password)
        val confirmPassword: EditText = findViewById(R.id.reg_confirmPass)
        val toLogin: TextView = findViewById(R.id.accExist)
        val register: Button = findViewById(R.id.registerBtn)

        //phone.addTextChangedListener(PhoneDashWatcher())

        toLogin.setOnClickListener {
            showLogin()
        }

        register.setOnClickListener {
            register.isEnabled = false;

            val client = Client(applicationContext)
            val response = client.register(trn = trn.text.toString(), name = name.text.toString(), password = password.text.toString(), email = email.text.toString(), phone = phone.text.toString(), confirmPassword = confirmPassword.text.toString())
            register.isEnabled = true;

            when (response) {
                RegisterCode.EMPTY_FIELD ->Toast.makeText(applicationContext, "One of the fields is empty", Toast.LENGTH_SHORT).show()
                RegisterCode.TRN_INVALID -> Toast.makeText(applicationContext, "TRN must be 9 digits", Toast.LENGTH_SHORT).show()
                RegisterCode.NAME_INVALID -> Toast.makeText(applicationContext, "Name should not contain special symbols", Toast.LENGTH_SHORT).show()
                RegisterCode.PHONE_INVALID -> Toast.makeText(applicationContext, "Phone should be in format: 87612345678", Toast.LENGTH_SHORT).show()
                RegisterCode.PASSWORD_LONG -> Toast.makeText(applicationContext, "Password should not be greater than 30 characters", Toast.LENGTH_SHORT).show()
                RegisterCode.PASSWORD_SHORT -> Toast.makeText(applicationContext, "Password should not be less than 8 characters", Toast.LENGTH_SHORT).show()
                RegisterCode.PASSWORD_MISMATCH -> Toast.makeText(applicationContext, "Passwords do not match", Toast.LENGTH_SHORT).show()
                RegisterCode.EMAIL_INVALID -> Toast.makeText(applicationContext, "Email should be in format: example@yahoo.com", Toast.LENGTH_SHORT).show()
                RegisterCode.TRN_EXISTS -> Toast.makeText(applicationContext, "TRN already exists", Toast.LENGTH_SHORT).show()
                RegisterCode.EMAIL_EXISTS -> Toast.makeText(applicationContext, "Email already exists", Toast.LENGTH_SHORT).show()
                RegisterCode.PHONE_EXISTS -> Toast.makeText(applicationContext, "Phone already exists", Toast.LENGTH_SHORT).show()
                RegisterCode.ERROR -> Toast.makeText(applicationContext, "Failed to register", Toast.LENGTH_SHORT).show()
                RegisterCode.OK -> {
                    Toast.makeText(applicationContext, "Successfully registered", Toast.LENGTH_SHORT).show()
                    showLogin()
                }
            }
        }
    }

    
    private fun showForgetPassPage()
    {
        setContentView(R.layout.forget_pass_page)

        val sendCode: Button = findViewById(R.id.sendCodeBtn)
        val toLogin: Button = findViewById(R.id.backToLoginBtn)

        sendCode.setOnClickListener
        {

        }
        toLogin.setOnClickListener {
            showLogin()
        }
    }
}

class PhoneDashWatcher : TextWatcher {
    private var isFormatting = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isFormatting || s == null) return
        isFormatting = true

        val digits = s.toString().filter { it.isDigit() }.take(10)
        val formatted = when {
            digits.length > 6 -> "${digits.substring(0, 3)}-${digits.substring(3, 6)}-${digits.substring(6)}"
            digits.length > 3 -> "${digits.substring(0, 3)}-${digits.substring(3)}"
            else -> digits
        }

        s.replace(0, s.length, formatted)
        isFormatting = false
    }
}