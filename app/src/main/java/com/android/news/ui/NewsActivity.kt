package com.android.news.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.android.news.R
import com.android.news.database.ArticleDatabase
import com.android.news.databinding.ActivityNewsBinding
import com.android.news.repository.NewsRepository
import com.android.news.ui.viewmodels.NewsViewModel
import com.android.news.ui.viewmodels.NewsViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_news.*

class NewsActivity : AppCompatActivity() {

    lateinit var viewModel:NewsViewModel
    private lateinit var binding:ActivityNewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository=NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory=NewsViewModelProviderFactory(application,repository)
        viewModel= ViewModelProvider(this,viewModelProviderFactory)[NewsViewModel::class.java]

        binding= ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        bottomNavigationView.setupWithNavController(newsNavHostFragment.findNavController())
    }
}
