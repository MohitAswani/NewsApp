package com.android.news.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.android.news.R
import com.android.news.adapters.NewsPreviewAdapter
import com.android.news.databinding.FragmentSavedNewsBinding
import com.android.news.databinding.FragmentSearchNewsBinding
import com.android.news.ui.NewsActivity
import com.android.news.ui.viewmodels.NewsViewModel
import com.android.news.util.Constants

class SavedNewsFragment : Fragment(R.layout.fragment_saved_news) {

    companion object {
        const val TAG = "SearchNewsFragment"
    }

    private lateinit var viewModel:NewsViewModel
    private lateinit var newsAdapter: NewsPreviewAdapter
    private lateinit var binding: FragmentSavedNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel=(activity as NewsActivity).viewModel

        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_saved_news,container,false)

        viewModel=(activity as NewsActivity).viewModel

        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle=Bundle().apply {
                putSerializable("Article",it)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }

        return binding.root

    }


    private fun setUpRecyclerView(){
        newsAdapter= NewsPreviewAdapter()
        binding.rvSavedNews.apply {
            adapter=newsAdapter
            layoutManager= GridLayoutManager(requireContext(), Constants.GRID_LAYOUT_SPAN_COUNT)
        }

    }



}