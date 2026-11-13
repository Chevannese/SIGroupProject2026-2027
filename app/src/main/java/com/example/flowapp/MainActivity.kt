package com.example.flowapp

import android.os.Bundle
import android.widget.ScrollView
import android.util.Log
import android.widget.*
import androidx.activity.*
import androidx.activity.compose.*
import com.example.flowapp.ui.theme.FlowAppTheme
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.security.SecureRandom
import java.util.Locale
import android.graphics.Color
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.graphics.Typeface

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var instance: MainActivity
            private set

        val isInitialized: Boolean
            get() = ::instance.isInitialized
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        enableEdgeToEdge()

        // shows login when the app starts
        showLogin()
    }

    private var generatedCode: String? = null
    private var codeExpiry: Long = 0

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
            val client = Client()

            val response = client.login(trn.text.toString(), password.text.toString())

            when (response) {
                LoginCode.NOT_FOUND -> Toast.makeText(applicationContext, "TRN or password is incorrect", Toast.LENGTH_SHORT).show()
                LoginCode.ERROR -> Toast.makeText(applicationContext, "Unknown error logging in", Toast.LENGTH_SHORT).show()
                LoginCode.OK -> showMain(trn.text.toString())
            }
        }
    }

    fun showMain(trn: String) {
        setContent {
            FlowAppTheme {
                Main(onLogoutClick = { showLogin() }, trn = trn)
            }
        }
    }



    private fun showRegister() {
        setContentView(R.layout.register)
        setupRegistrationScroll()
        val name: EditText = findViewById(R.id.name)
        val trn: EditText = findViewById(R.id.trn)
        val email: EditText = findViewById(R.id.reg_email)
        val phone: EditText = findViewById(R.id.reg_phone)
        val password: TextInputEditText = findViewById(R.id.reg_password)
        val confirmPassword: TextInputEditText = findViewById(R.id.reg_confirmPass)
        val toLogin: TextView = findViewById(R.id.accExist)
        val register: Button = findViewById(R.id.registerBtn)



        toLogin.setOnClickListener {
            showLogin()
        }

        register.setOnClickListener {
            register.isEnabled = false;

            val client = Client()
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
                RegisterCode.PASSWORD_INVALID -> Toast.makeText(applicationContext, "Password should contain letters, numbers and 1 special character", Toast.LENGTH_SHORT).show()
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


    private fun showForgetPassPage() {


        setContentView(R.layout.forget_pass_page)

        val email: EditText = findViewById(R.id.email)
        val sendCode: Button = findViewById(R.id.sendCodeBtn)
        val toLogin: ImageButton = findViewById(R.id.backToLoginBtn)

        sendCode.setOnClickListener {

            val emailString = email.text.toString().trim()

            if (emailString.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please enter your email address",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Run file access and SMTP operations off the UI thread
            sendCode.isEnabled = false
            //val emailServer = EmailSender(applicationContext)

            Thread {
                try {

                    val client = Client()

                    val response = client.checkEmailAddress(emailString)

                    if (response == LoginCode.OK) {

                        val code = generateVerificationCode()

                        val sender = EmailSender()
                        val saved = client.writeVerifyCodeToFile(emailString, code)

                        if (saved) {
                            // Send verification email
                            sender.sendVerificationCode(emailString, code)
                        }

                        generatedCode = code
                        codeExpiry = System.currentTimeMillis() + 5 * 60 * 1000

                        runOnUiThread {
                            Toast.makeText(
                                this,
                                "Verification code sent!",
                                Toast.LENGTH_SHORT
                            ).show()

                            showEnterPassPage(emailString)
                        }

                    } else {
                        runOnUiThread {
                            sendCode.isEnabled = true

                            Toast.makeText(
                                this,
                                "Invalid Email",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } catch (e: Exception) {

                    Log.e("EMAIL_ERROR", "Failed to send verification code", e)

                    runOnUiThread {
                        sendCode.isEnabled = true

                        Toast.makeText(
                            this,
                            "Unable to send verification email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }.start()
        }

        toLogin.setOnClickListener {
            showLogin()
        }
    }

    private fun showEnterPassPage(emailString: String) {
        setContentView(R.layout.enter_code)

        val back: ImageButton = findViewById(R.id.back)
        val password: TextInputEditText = findViewById(R.id.password)
        val confirmPassword: TextInputEditText = findViewById(R.id.confirmPass)
        val verifyCode: EditText = findViewById(R.id.verifyCode)
        val updatePassword: Button = findViewById(R.id.changePassBtn)

        val client = Client()

        // Back to previous page
        back.setOnClickListener {
            showForgetPassPage()
        }

        updatePassword.setOnClickListener {
            updatePassword.isEnabled = false

            val response = client.updatePassword(
                email = emailString,
                verifyCode = verifyCode.text.toString(),
                password = password.text.toString(),
                confirmPassword = confirmPassword.text.toString()
            )

            updatePassword.isEnabled = true

            when (response) {
                UpdatePassword.CODE_SHORT ->
                    Toast.makeText(
                        applicationContext,
                        "Code too short. Verification Code should contain 6 digits",
                        Toast.LENGTH_SHORT
                    ).show()
                UpdatePassword.CODE_LONG ->
                    Toast.makeText(
                        applicationContext,
                        "Code too long. Verification Code should contain 6 digits",
                        Toast.LENGTH_SHORT
                    ).show()
                UpdatePassword.EMPTY_FIELD ->
                    Toast.makeText(
                        applicationContext,
                        "One of the fields is empty",
                        Toast.LENGTH_SHORT
                    ).show()

                UpdatePassword.PASSWORD_LONG ->
                    Toast.makeText(
                        applicationContext,
                        "Password should not be greater than 30 characters",
                        Toast.LENGTH_SHORT
                    ).show()

                UpdatePassword.PASSWORD_SHORT ->
                    Toast.makeText(
                        applicationContext,
                        "Password should not be less than 8 characters",
                        Toast.LENGTH_SHORT
                    ).show()

                UpdatePassword.PASSWORD_MISMATCH ->
                    Toast.makeText(
                        applicationContext,
                        "Passwords do not match",
                        Toast.LENGTH_SHORT
                    ).show()

                UpdatePassword.INVALID_CODE ->
                    Toast.makeText(
                        applicationContext,
                        "Invalid or expired verification code",
                        Toast.LENGTH_SHORT
                    ).show()

                UpdatePassword.ERROR ->
                    Toast.makeText(
                        applicationContext,
                        "Failed to update password",
                        Toast.LENGTH_SHORT
                    ).show()
                UpdatePassword.INVALID_PASSWORD ->
                    Toast.makeText(
                        applicationContext,
                        "Password should contain letters, numbers, and special character",
                        Toast.LENGTH_SHORT
                    ).show()
                UpdatePassword.OK -> {
                    Toast.makeText(
                        applicationContext,
                        "Successfully changed password",
                        Toast.LENGTH_SHORT
                    ).show()

                    showLogin()
                }
            }
        }
    }


    private fun generateVerificationCode(): String {
        return String.format(
            Locale.US,
            "%06d",
            SecureRandom().nextInt(1_000_000)
        )
    }


    fun loadAccountBalance(trn: String) {
        val client = Client()

        // Retrieve the customer's payment card.
        val card = client.getAllCards().find {
            it.trn == trn
        }

        // Retrieve all receipts belonging to the customer.
        val receipts = client.getCustomerReceipts(trn)

        // Find the views.
        val cardNumber = findViewById<TextView>(R.id.cardNumTextField)
        val balance = findViewById<TextView>(R.id.amount)
        val accountStatus = findViewById<TextView>(R.id.accountStatus)

        val paymentList = findViewById<LinearLayout>(
            R.id.paymentListContainer
        )

        val noPayments = findViewById<TextView>(
            R.id.noPaymentsText
        )

        // Display card information.
        if (card != null) {
            cardNumber.text = "Card Number: Ending in ${card.lastFourDigits}"

            balance.text = "$%.2f".format(card.balance)

            accountStatus.text = "Active"
        }
        else {
            cardNumber.text = "No Card Linked"
            balance.text = "$0.00"
            accountStatus.text = "No Card Available"
        }

        // Clear previous receipt information.
        paymentList.removeAllViews()

        // Display an empty message if there are no receipts.
        if (receipts.isEmpty()) {
            paymentList.addView(noPayments)
            noPayments.visibility = View.VISIBLE
            return
        }

        noPayments.visibility = View.GONE

        // Display every customer receipt.
        for (receipt in receipts) {

            val receiptLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
                setBackgroundColor(Color.rgb(240, 243, 248))
            }

            val receiptTitle = TextView(this).apply {
                text = "Receipt #${receipt.receiptNo}"
                textSize = 18f
                setTextColor(Color.rgb(20, 50, 90))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            val receiptDetails = TextView(this).apply {
                text = """
                Ticket Number: ${receipt.ticketNo}
                Payment Method: ${receipt.paymentMethod}
                Payment Status: ${receipt.paymentStatus}

                Service Cost: $${"%.2f".format(receipt.cost)}
                GCT (15%): $${"%.2f".format(receipt.surcharge)}
                Subtotal: $${"%.2f".format(receipt.subTotal)}

                Total Paid: $${"%.2f".format(receipt.totalPaid)}
                Amount Due: $${"%.2f".format(
                    (receipt.subTotal - receipt.totalPaid).coerceAtLeast(0.0)
                )}
            """.trimIndent()

                textSize = 14f
                setTextColor(Color.DKGRAY)
            }

            receiptLayout.addView(receiptTitle)

            val divider = View(this).apply {
                setBackgroundColor(Color.LTGRAY)
            }

            receiptLayout.addView(
                divider,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2
                ).apply {
                    topMargin = 12
                    bottomMargin = 12
                }
            )

            receiptLayout.addView(receiptDetails)

            paymentList.addView(
                receiptLayout,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 12
                }
            )
        }


        fun loadAccountBalance(trn: String) {
            val client = Client()

            // Retrieve the customer's payment card.
            val card = client.getAllCards().find {
                it.trn == trn
            }

            // Retrieve all receipts belonging to the customer.
            val receipts = client.getCustomerReceipts(trn)

            // Find the views.
            val cardNumber = findViewById<TextView>(R.id.cardNumTextField)
            val balance = findViewById<TextView>(R.id.amount)
            val accountStatus = findViewById<TextView>(R.id.accountStatus)

            val paymentList = findViewById<LinearLayout>(
                R.id.paymentListContainer
            )

            val noPayments = findViewById<TextView>(
                R.id.noPaymentsText
            )

            // Display card information.
            if (card != null) {
                cardNumber.text = "**** **** **** ${card.lastFourDigits}"

                balance.text = "$%.2f".format(card.balance)

                accountStatus.text = "Active"
            }
            else {
                cardNumber.text = "No Card Linked"
                balance.text = "$0.00"
                accountStatus.text = "No Card Available"
            }

            // Clear previous receipt information.
            paymentList.removeAllViews()

            // Display an empty message if there are no receipts.
            if (receipts.isEmpty()) {
                paymentList.addView(noPayments)
                noPayments.visibility = View.VISIBLE
                return
            }

            noPayments.visibility = View.GONE

            // Display every customer receipt.
            for (receipt in receipts) {

                val receiptLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(16, 16, 16, 16)
                    setBackgroundColor(Color.rgb(240, 243, 248))
                }

                val receiptTitle = TextView(this).apply {
                    text = "Receipt #${receipt.receiptNo}"
                    textSize = 18f
                    setTextColor(Color.rgb(20, 50, 90))
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }

                val receiptDetails = TextView(this).apply {
                    text =
                        """
                        Ticket Number: ${receipt.ticketNo}
                        Payment Method: ${receipt.paymentMethod}
                        Payment Status: ${receipt.paymentStatus}
        
                        Service Cost: $${"%.2f".format(receipt.cost)}
                        GCT (15%): $${"%.2f".format(receipt.surcharge)}
                        Subtotal: $${"%.2f".format(receipt.subTotal)}
        
                        Total Paid: $${"%.2f".format(receipt.totalPaid)}
                        Amount Due: $${"%.2f".format(
                                (receipt.subTotal - receipt.totalPaid).coerceAtLeast(0.0)
                    )}
            """.trimIndent()

                    textSize = 14f
                    setTextColor(Color.DKGRAY)
                }

                receiptLayout.addView(receiptTitle)

                val divider = View(this).apply {
                    setBackgroundColor(Color.LTGRAY)
                }

                receiptLayout.addView(
                    divider,
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        2
                    ).apply {
                        topMargin = 12
                        bottomMargin = 12
                    }
                )

                receiptLayout.addView(receiptDetails)

                paymentList.addView(
                    receiptLayout,
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 12
                    }
                )
            }
        }


    }

    private fun setupRegistrationScroll() {
        val scrollView = findViewById<ScrollView>(
            R.id.registrationScrollView
        ) ?: return

        ViewCompat.setOnApplyWindowInsetsListener(scrollView) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            val keyboard = insets.getInsets(
                WindowInsetsCompat.Type.ime()
            )

            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                maxOf(systemBars.bottom, keyboard.bottom)
            )

            insets
        }

        ViewCompat.requestApplyInsets(scrollView)
    }



}