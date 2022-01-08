package com.android.news.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.android.news.R
import com.android.news.adapters.BreakingNewsAdapter
import com.android.news.adapters.NewsPreviewAdapter
import com.android.news.databinding.FragmentSearchNewsBinding
import com.android.news.ui.NewsActivity
import com.android.news.ui.viewmodels.NewsViewModel
import com.android.news.util.Constants.Companion.GRID_LAYOUT_SPAN_COUNT
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

    private lateinit var viewModel:NewsViewModel
    private lateinit var newsAdapter: NewsPreviewAdapter
    private lateinit var binding:FragmentSearchNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_search_news,container,false)

        viewModel=(activity as NewsActivity).viewModel

        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle=Bundle().apply {
                putSerializable("Article",it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        var job: Job?=null
        binding.etSearch.addTextChangedListener{
            job?.cancel()
            job= MainScope().launch {
                delay(SEARCH_DELAY)
                it?.let {
                   if(it.toString().isNotEmpty())
                   {
                       viewModel.searchNews(it.toString())
                   }
                }
            }
        }


        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response->
            when(response){
                is Resource.Success ->{
                    hideProgressBar()
                    response.data?.let {
                        newsAdapter.differ.submitList(it.articles)
                    }
                }
                is Resource.Error ->{
                    hideProgressBar()
                    response.message?.let{
                        Log.e(BreakingNewsFragment.TAG,"Error : $it")
                    }
                }
                is Resource.Loading->{
                    showProgressBar()
                }
            }
        })

        return binding.root
    }

    private fun setUpRecyclerView(){
        newsAdapter= NewsPreviewAdapter()
        binding.rvSearchNews.apply {
            adapter=newsAdapter
            layoutManager=GridLayoutManager(requireContext(),GRID_LAYOUT_SPAN_COUNT)
        }

    }

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility=View.INVISIBLE
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility=View.VISIBLE
    }
}