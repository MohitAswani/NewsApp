package com.android.news.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.GridView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.android.news.R
import com.android.news.adapters.BreakingNewsAdapter
import com.android.news.adapters.NewsPreviewAdapter
import com.android.news.databinding.FragmentSearchNewsBinding
import com.android.news.ui.NewsActivity
import com.android.news.ui.viewmodels.NewsViewModel
import com.android.news.util.Constants.Companion.GRID_LAYOUT_SPAN_COUNT
import com.android.news.util.Constants.Companion.QUERY_PAGE_SIZE
import com.android.news.util.Constants.Companion.SEARCH_DELAY
import com.android.news.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment(R.layout.fragment_search_news) {

    companion object {
        const val TAG = "SearchNewsFragment"
    }

    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsPreviewAdapter
    private lateinit var binding: FragmentSearchNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_news, container, false)

        viewModel = (activity as NewsActivity).viewModel

        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("Article", it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        var job: Job? = null
        binding.etSearch.addTextChangedListener {
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_DELAY)
                it?.let {
                    if (it.toString().isNotEmpty()) {
                        binding.searchImage.visibility = View.INVISIBLE
                        binding.rvSearchNews.visibility = View.VISIBLE
                        viewModel.searchNews(it.toString())
                    }
                }
            }
        }


        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles.toList())
                        val totalPage=it.totalResults/ QUERY_PAGE_SIZE + 2
                        isLastPage=viewModel.searchNewsPage==totalPage
                        if(isLastPage)
                        {
                            binding.rvSearchNews.setPadding(0,0,0,0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {
                        Toast.makeText(requireContext(),"An error occurred", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        return binding.root
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsPreviewAdapter()
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = GridLayoutManager(requireContext(), GRID_LAYOUT_SPAN_COUNT)
            addOnScrollListener(this@SearchNewsFragment.rvScrollListener)
        }

    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading=false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading=true
    }


    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val rvScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as GridLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLastPage && !isLoading
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning=firstVisibleItemPosition>=0
            val isTotalMoreThanVisible=totalItemCount>=QUERY_PAGE_SIZE
            val shouldPaginate=isNotLoadingAndNotLastPage&&isAtLastItem&&isNotAtBeginning&&
                    isTotalMoreThanVisible&&isScrolling

            if(shouldPaginate){
                viewModel.searchNews(binding.etSearch.text.toString())
                isScrolling=false
            }
        }
    }
}