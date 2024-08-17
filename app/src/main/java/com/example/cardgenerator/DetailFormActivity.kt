package com.example.cardgenerator

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cardgenerator.databinding.ActivityDetailFormBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailFormBinding

    private lateinit var brideName: String;
    private lateinit var groomName: String;
    private lateinit var functionDate: String;
    private lateinit var functionTime: String;
    private lateinit var functionVenue: String;
    private lateinit var key : String;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // This will adjust the scroll view when the keyboard appears
        binding.scrollView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            binding.scrollView.getWindowVisibleDisplayFrame(r)
            val screenHeight = binding.scrollView.rootView.height
            val keypadHeight = screenHeight - r.bottom

            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is optimal for most devices
                // Scroll to the focused view
                val focusedView = currentFocus
                focusedView?.let {
                    binding.scrollView.post {
                        binding.scrollView.smoothScrollTo(0, it.bottom)
                    }
                }
            }
        }

        setFocusListeners()


        key = intent.getStringExtra("CARD_KEY").toString()


        binding.previewButton.setOnClickListener {
            getInputData()
        }

        binding.function1Date.setOnClickListener {
            showDatePickerDialog()
        }

        binding.function1Time.setOnClickListener {
            showTimePickerDialog()
        }


    }

    private fun getInputData() {
        val brideFirstName = binding.brideFname.text.toString().trim()
        val brideLastName = binding.brideLname.text.toString().trim()
        val groomFirstName = binding.groomFname.text.toString().trim()
        val groomLastName = binding.groomLname.text.toString().trim()
        val functionDate = binding.function1Date.text.toString().trim()
        val functionTime = binding.function1Time.text.toString().trim()
        val functionVenue = binding.function1Venue.text.toString().trim()
        val functionName = binding.function1Name.text.toString().trim()


        brideName = "$brideFirstName $brideLastName"
        groomName = "$groomFirstName $groomLastName"

        // Create an intent to start the CardPreviewActivity
        val intent = Intent(this, CardPreviewActivity::class.java).apply {
            putExtra("BRIDE_NAME", brideName)
            putExtra("GROOM_NAME", groomName)
            putExtra("FUNCTION_DATE", functionDate)
            putExtra("FUNCTION_TIME", functionTime)
            putExtra("FUNCTION_VENUE", functionVenue)
            putExtra("FUNCTION_NAME", functionName)
            putExtra("CARD_KEY", key)
        }


        // Start the CardPreviewActivity
        startActivity(intent)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.function1Date.setText(dateFormat.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)
                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                binding.function1Time.setText(timeFormat.format(selectedTime.time))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun setFocusListeners() {
        // Add focus listeners to EditTexts
        binding.brideFname.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.scrollView.post {
                    binding.scrollView.smoothScrollTo(0, binding.brideFname.bottom)
                }
            }
        }
        binding.brideLname.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.scrollView.post {
                    binding.scrollView.smoothScrollTo(0, binding.brideLname.bottom)
                }
            }
        }
        // Repeat for other EditTexts
        binding.groomFname.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.scrollView.post {
                    binding.scrollView.smoothScrollTo(0, binding.groomFname.bottom)
                }
            }
        }
        binding.groomLname.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.scrollView.post {
                    binding.scrollView.smoothScrollTo(0, binding.groomLname.bottom)
                }
            }
        }
        binding.function1Name.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.scrollView.post {
                    binding.scrollView.smoothScrollTo(0, binding.function1Name.bottom)
                }
            }
        }
        binding.function1Venue.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.scrollView.post {
                    binding.scrollView.smoothScrollTo(0, binding.function1Venue.bottom)
                }
            }
        }
    }

}