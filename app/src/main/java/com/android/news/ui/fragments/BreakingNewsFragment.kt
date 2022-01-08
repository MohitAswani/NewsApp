package com.android.news.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.android.news.R
import com.android.news.adapters.BreakingNewsAdapter
import com.android.news.databinding.FragmentBreakingNewsBinding
import com.android.news.ui.NewsActivity
import com.android.news.ui.viewmodels.NewsViewModel
import com.android.news.util.Resource

class BreakingNewsFragment : Fragment(R.layout.fragment_breaking_news) {

    companion object {
        const val TAG = "BreakingNewsFragment"
    }
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: BreakingNewsAdapter
    private lateinit var binding: FragmentBreakingNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_breaking_news,container,false)

        viewModel=(activity as NewsActivity).viewModel

        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle=Bundle().apply {
                putSerializable("Article",it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response->
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
                        Log.e(TAG,"Error : $it")
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
        newsAdapter= BreakingNewsAdapter()
        binding.vpBreakingNews.apply {
            adapter=newsAdapter
            orientation=ViewPager2.ORIENTATION_VERTICAL
        }

    }

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility=View.INVISIBLE
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility=View.VISIBLE
    }

}