package com.example.cardgenerator

import CustomPagerAdapter
import ZoomOutPageTransformer
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.cardgenerator.databinding.ActivityAllCardsBinding

class AllCardsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAllCardsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAllCardsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Sample data for the ViewPager
        val items = listOf(
            R.drawable.card1,
            R.drawable.card2,
            R.drawable.card3,
            R.drawable.card4,
            R.drawable.card5
        )

        // Corresponding image names
        val imageNames = listOf(
            "card1",
            "card2",
            "card3",
            "card4",
            "card5"
        )

        // Set up the adapter with items and image names
        val adapter = CustomPagerAdapter(items, imageNames)
        binding.viewPager.adapter = adapter

        // Set the PageTransformer
        binding.viewPager.setPageTransformer(ZoomOutPageTransformer())
    }
}