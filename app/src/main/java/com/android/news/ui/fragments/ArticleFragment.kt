package com.android.news.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.android.news.R
import com.android.news.adapters.NewsPreviewAdapter
import com.android.news.databinding.FragmentArticleBinding
import com.android.news.ui.NewsActivity
import com.android.news.ui.viewmodels.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class ArticleFragment : Fragment(R.layout.fragment_article) {

    private lateinit var viewModel: NewsViewModel
    private lateinit var binding: FragmentArticleBinding
    private val args: ArticleFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article, container, false)

        viewModel = (activity as NewsActivity).viewModel

        val article = args.article

        binding.webView.apply {
            webViewClient = WebViewClient()
            article.url?.let { loadUrl(it) }
        }

        binding.fab.setOnClickListener {

            if(viewModel.getSavedArticles().value?.contains(article) == true){
                Snackbar.make(requireView(), "Articles already saved", Snackbar.LENGTH_SHORT).show()
            } else {
                viewModel.upsertArticle(article)
                Snackbar.make(requireView(), "Articles saved successfully", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        return binding.root
    }
}