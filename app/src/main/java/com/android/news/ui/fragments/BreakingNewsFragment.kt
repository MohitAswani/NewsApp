package com.android.news.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
                        newsAdapter.differ.submitList(it.articles.toList())
                    }
                }
                is Resource.Error ->{
                    hideProgressBar()
                    response.message?.let{
                        Toast.makeText(requireContext(),"An error occurred",Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading->{
                    showProgressBar()
                }
            }
        })


        return binding.root
    }


    private fun setUpRecyclerView() {
        newsAdapter = BreakingNewsAdapter()
        binding.vpBreakingNews.apply {
            adapter = newsAdapter
            orientation = ViewPager2.ORIENTATION_VERTICAL
            registerOnPageChangeCallback(vpPageListener)
        }
    }



    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility=View.INVISIBLE
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility=View.VISIBLE
    }

    var paginated=false
    var isScrolling=false



    private val vpPageListener=object : ViewPager2.OnPageChangeCallback(){
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            if(state==ViewPager2.SCROLL_STATE_DRAGGING){
                isScrolling=true
            }
        }

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            Log.d(TAG,"${viewModel.breakingNewsResponse?.articles?.size},${position}")
            if(position+2==viewModel.breakingNewsResponse?.articles?.size && !paginated) {
                Log.d(TAG,"Loading more articles")
                paginated=true
                viewModel.getBreakingNews("in")
                isScrolling=false
            }
            if(position==(viewModel.breakingNewsResponse?.articles?.size)?.div(2)){
                paginated=false
            }
        }
    }

}