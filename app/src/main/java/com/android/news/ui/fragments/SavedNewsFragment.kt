package com.android.news.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.android.news.R
import com.android.news.adapters.NewsPreviewAdapter
import com.android.news.databinding.FragmentSavedNewsBinding
import com.android.news.databinding.FragmentSearchNewsBinding
import com.android.news.ui.NewsActivity
import com.android.news.ui.viewmodels.NewsViewModel
import com.android.news.util.Constants
import com.google.android.material.snackbar.Snackbar

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

        val itemTouchHelperCallback=object:ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition
                val article=newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(requireView(),"Successfully deleted news article",Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.upsertArticle(article)
                    }
                    show()
                }
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }

        viewModel.getSavedArticles().observe(viewLifecycleOwner, Observer {
            newsAdapter.differ.submitList(it)
        })

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