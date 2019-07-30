package com.zimplifica.redipuntos.ui.fragments


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseFragment
import com.zimplifica.redipuntos.libs.qualifiers.RequiresFragmentViewModel
import com.zimplifica.redipuntos.ui.activities.MovementDetailActivity
import com.zimplifica.redipuntos.ui.adapters.MovSection
import com.zimplifica.redipuntos.ui.paging.PaginationScrollListener
import com.zimplifica.redipuntos.ui.paging.TransactionAdapter
import com.zimplifica.redipuntos.viewModels.MovementsFragmentVM
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_movements.*
import kotlinx.android.synthetic.main.fragment_movements.view.*

@RequiresFragmentViewModel(MovementsFragmentVM.ViewModel::class)
class MovementsFragment : BaseFragment<MovementsFragmentVM.ViewModel>() {
    lateinit var recyclerView: RecyclerView
    lateinit var sectionAdapter : SectionedRecyclerViewAdapter
    private val compositeDisposable = CompositeDisposable()

    lateinit var adapter : TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_movements, container, false)
        recyclerView = view.mov_fragment_recycler_view
        val manager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = manager
        recyclerView.setHasFixedSize(true)
        val divider = androidx.recyclerview.widget.DividerItemDecoration(activity, manager.orientation)
        recyclerView.addItemDecoration(divider)
        sectionAdapter = SectionedRecyclerViewAdapter()

        adapter = TransactionAdapter{transaction ->
            viewModel.inputs.showMovementDetail(transaction)
        }
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : PaginationScrollListener(manager){
            override fun loadMoreItems() {
                viewModel.isLoading = true
                viewModel.currentPage++
                viewModel.preparedItems(true)
            }

            override fun isLastPage(): Boolean {
                 return viewModel.isLastPage
            }

            override fun isLoading(): Boolean {
                return viewModel.isLoading
            }

        })

        //recyclerView.adapter = sectionAdapter
        // Inflate the layout for this fragment
        return view
    }

    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mov_fragment_swipe.setOnRefreshListener {
            //viewModel.inputs.fetchTransactions(false)
            viewModel.onRefresh()
            adapter.clear()
        }

        compositeDisposable.add(viewModel.outputs.transactionList().observeOn(AndroidSchedulers.mainThread()).subscribe {
            Log.e("VMOutput",viewModel.isLastPage.toString())
            if (it.isEmpty()){
                mov_fragment_no_transaction.visibility = View.VISIBLE
            }else{
                mov_fragment_no_transaction.visibility = View.GONE
            }
            mov_fragment_swipe.isRefreshing = false
            if(viewModel.currentPage != viewModel.PAGE_START) adapter.removeLoading()
            adapter.addAll(it)
            if (viewModel.token != null) {
                adapter.addLoading()
            }
            else {
                viewModel.isLastPage = true
            }
            viewModel.isLoading = false
        })

        //this.viewModel.inputs.fetchTransactions(true)

        //mov_fragment_no_transaction.visibility = View.GONE

        compositeDisposable.add(this.viewModel.outputs.fetchTransactionsAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isEmpty()){
                    mov_fragment_no_transaction.visibility = View.VISIBLE
                }else{
                    mov_fragment_no_transaction.visibility = View.GONE
                }
                mov_fragment_swipe.isRefreshing = false
                sectionAdapter.removeAllSections()
                for(item in it){
                    sectionAdapter.addSection(MovSection(item.first,item.second){ transaction ->
                        viewModel.inputs.showMovementDetail(transaction)
                    })
                }
                sectionAdapter.notifyDataSetChanged()
            })

        compositeDisposable.add(this.viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                mov_fragment_swipe.isRefreshing = false
                Toast.makeText(activity,it,Toast.LENGTH_SHORT).show()
            })

        compositeDisposable.add(this.viewModel.outputs.showMovementDetailAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(activity,MovementDetailActivity::class.java)
                intent.putExtra("transaction",it)
                startActivity(intent)
            })
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        //this.viewModel.inputs.fetchTransactions(true)
    }
}
