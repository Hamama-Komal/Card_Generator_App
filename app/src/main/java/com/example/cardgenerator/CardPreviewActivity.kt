package com.example.cardgenerator

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cardgenerator.databinding.ActivityCardPreviewBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

class CardPreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.materialToolbar2)


        // Retrieve the card key to determine which layout to inflate
        val cardKey = intent.getStringExtra("CARD_KEY")

        // Get a reference to the CardView in the main layout
        val cardContainer = findViewById<CardView>(R.id.cardContainer)

        // Inflate the appropriate layout based on the card key
        val cardLayout: View = when (cardKey) {
            "card1" -> LayoutInflater.from(this)
                .inflate(R.layout.card1_design, cardContainer, false)

            "card2" -> LayoutInflater.from(this)
                .inflate(R.layout.card2_design, cardContainer, false)

            "card3" -> LayoutInflater.from(this)
                .inflate(R.layout.card3_design, cardContainer, false)

            "card4" -> LayoutInflater.from(this)
                .inflate(R.layout.card4_design, cardContainer, false)

            "card5" -> LayoutInflater.from(this)
                .inflate(R.layout.card5_desgin, cardContainer, false)


            else -> LayoutInflater.from(this)
                .inflate(R.layout.card1_design, cardContainer, false) // default layout
        }

        // Add the inflated layout to the CardView
        binding.cardContainer.addView(cardLayout)


        // Retrieve the data passed from DetailFormActivity
        val brideName = intent.getStringExtra("BRIDE_NAME")
        val groomName = intent.getStringExtra("GROOM_NAME")
        val functionDate = intent.getStringExtra("FUNCTION_DATE")
        val functionTime = intent.getStringExtra("FUNCTION_TIME")
        val functionVenue = intent.getStringExtra("FUNCTION_VENUE")
        val functionName = intent.getStringExtra("FUNCTION_NAME")


        // Find the TextViews in the inflated layout and set the text

        cardLayout.findViewById<TextView>(R.id.textBrideName)?.text = brideName
        cardLayout.findViewById<TextView>(R.id.textGroomName)?.text = groomName
        cardLayout.findViewById<TextView>(R.id.textDate)?.text = functionDate
        cardLayout.findViewById<TextView>(R.id.textTime)?.text = functionTime
        cardLayout.findViewById<TextView>(R.id.textAddress)?.text = functionVenue
        cardLayout.findViewById<TextView>(R.id.textName)?.text = functionName


        // Set up the toolbar menu item click listener
        toolbar.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.options -> {
                    // Handle share action
                    showBottomSheet()
                    true
                }

                else -> {
                    false
                }
            }
        }
    }


    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val bottomSheetView = layoutInflater.inflate(R.layout.option_bottom_sheet, null)

        // Assign distinct IDs to these views in the layout file
        val shareAsImage = bottomSheetView.findViewById<TextView>(R.id.shareImage)
        val shareAsPdf = bottomSheetView.findViewById<TextView>(R.id.sharePDF)
        val saveAsImage = bottomSheetView.findViewById<TextView>(R.id.saveImage)  // Corrected ID
        val saveAsPdf = bottomSheetView.findViewById<TextView>(R.id.savePdf)      // Corrected ID

        shareAsImage.setOnClickListener {
            shareImage()
            bottomSheetDialog.dismiss()
        }

        shareAsPdf.setOnClickListener {
            generatePdfFromView(binding.cardContainer, true)
            bottomSheetDialog.dismiss()
        }

        saveAsImage.setOnClickListener {
            saveImage()
            bottomSheetDialog.dismiss()
        }

        saveAsPdf.setOnClickListener {
            savePdf()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.show()
    }

    private fun saveImage(): Uri? {
        val cardView = findViewById<CardView>(R.id.cardContainer)
        val bitmap = Bitmap.createBitmap(cardView.width, cardView.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        cardView.draw(canvas)

        val uniqueID = UUID.randomUUID().toString()
        val fileName = "wedding_card_$uniqueID.jpg"
        var uri: Uri? = null

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/WeddingCards")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = contentResolver
        uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            try {
                resolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
                Toast.makeText(this, "Image save in gallery", Toast.LENGTH_SHORT).show()

                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error saving image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } ?: runOnUiThread {
            Toast.makeText(this, "Error saving image: Uri is null", Toast.LENGTH_SHORT).show()
        }

        return uri
    }

    private fun shareImage() {
        val uri = saveImage()  // Save the image and get the Uri

        uri?.let {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, it)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)  // Grant read permission
            }
            startActivity(Intent.createChooser(shareIntent, "Share Image"))
        } ?: runOnUiThread {
            Toast.makeText(this, "Error sharing image: Uri is null", Toast.LENGTH_SHORT).show()
        }
    }

    /*

        private fun saveImage(checkShare: Boolean): String {
            val cardView = findViewById<CardView>(R.id.cardContainer)
            val bitmap = Bitmap.createBitmap(cardView.width, cardView.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            cardView.draw(canvas)

            val uniqueID = UUID.randomUUID().toString()
            val fileName = "wedding_card_$uniqueID.jpg"

            if (!checkShare) {
                val progressDialog = ProgressDialog(this).apply {
                    setMessage("Saving Image...")
                    setCancelable(false)
                    show()
                }

                // Saving image via MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/WeddingCards")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val resolver = contentResolver
                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                uri?.let {
                    try {
                        resolver.openOutputStream(uri)?.use { outputStream ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        }

                        contentValues.clear()
                        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                        resolver.update(uri, contentValues, null, null)

                        runOnUiThread {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Image saved in Gallery: $fileName", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        runOnUiThread {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Error saving image: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } ?: runOnUiThread {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error saving image: Uri is null", Toast.LENGTH_SHORT).show()
                }
            }

            return fileName
        }

        private fun shareImage() {
            val fileName = saveImage(true)  // Save the image first and get the filename
            Handler(Looper.getMainLooper()).postDelayed({
                val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val appDir = File(picturesDir, "WeddingCards")
                val file = File(appDir, fileName)

                val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/jpeg"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)  // Grant read permission
                }
                startActivity(Intent.createChooser(shareIntent, "Share Image"))
            }, 3000)  // Delay to ensure image saving is complete
        }

    */

    private fun generatePdfFromView(view: View, shareCheck: Boolean) {
        val progressDialog = ProgressDialog(this).apply {
            setMessage("Generating PDF...")
            setCancelable(false)
            show()
        }

        Thread {
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)

            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(view.width, view.height, 1).create()
            val page = document.startPage(pageInfo)
            page.canvas.drawBitmap(bitmap, 0f, 0f, null)
            document.finishPage(page)

            // Generate a unique filename for the PDF
            val uniqueID = UUID.randomUUID().toString()
            val fileName = "wedding_card_$uniqueID.pdf"
            val downloadsDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val filePath = File(downloadsDir, fileName)

            try {
                document.writeTo(FileOutputStream(filePath))

                runOnUiThread {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this, "PDF saved in Downloads folder: $fileName", Toast.LENGTH_SHORT
                    ).show()

                    if (shareCheck) {
                        sharePdf(filePath)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error saving PDF: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            } finally {
                document.close()
            }
        }.start()
    }

    private fun savePdf() {
        val cardView = findViewById<CardView>(R.id.cardContainer)
        generatePdfFromView(cardView, shareCheck = false)
    }

    private fun sharePdf(pdfFile: File) {
        val uri = FileProvider.getUriForFile(this, "${packageName}.provider", pdfFile)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share PDF"))
    }


    private fun showProgressBar() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Saving file...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Simulate long-running task
        Handler(Looper.getMainLooper()).postDelayed({
            progressDialog.dismiss()
            Toast.makeText(this, "File saved!", Toast.LENGTH_SHORT).show()
        }, 3000)  // Simulate 3 seconds of processing
    }


}
