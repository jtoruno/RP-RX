package com.zimplifica.redipuntos.ui.fragments


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseFragment
import com.zimplifica.redipuntos.libs.qualifiers.RequiresFragmentViewModel
import com.zimplifica.redipuntos.ui.activities.MovementDetailActivity
import com.zimplifica.redipuntos.ui.adapters.MovSection
import com.zimplifica.redipuntos.viewModels.MovementsFragmentVM
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_movements.view.*

@RequiresFragmentViewModel(MovementsFragmentVM.ViewModel::class)
class MovementsFragment : BaseFragment<MovementsFragmentVM.ViewModel>() {
    lateinit var recyclerView: RecyclerView
    lateinit var sectionAdapter : SectionedRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_movements, container, false)
        recyclerView = view.mov_fragment_recycler_view
        val manager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        recyclerView.layoutManager = manager
        recyclerView.setHasFixedSize(true)
        sectionAdapter = SectionedRecyclerViewAdapter()
        recyclerView.adapter = sectionAdapter
        // Inflate the layout for this fragment
        return view
    }

    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        this.viewModel.inputs.fetchTransactions(false)

        this.viewModel.outputs.fetchTransactionsAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                sectionAdapter.removeAllSections()
                for(item in it){
                    sectionAdapter.addSection(MovSection(item.first,item.second))
                }
            }

        this.viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Toast.makeText(activity,it,Toast.LENGTH_SHORT).show()
            }

        this.viewModel.outputs.showMovementDetailAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(activity,MovementDetailActivity::class.java)
                intent.putExtra("transaction",it)
                startActivity(intent)
            }



    }


}
