package com.zimplifica.redipuntos.ui.fragments


import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseFragment
import com.zimplifica.redipuntos.libs.qualifiers.RequiresFragmentViewModel
import com.zimplifica.redipuntos.services.GlobalState
import com.zimplifica.redipuntos.ui.adapters.CardAdapter
import com.zimplifica.redipuntos.viewModels.PointsFragmentVM
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

@RequiresFragmentViewModel(PointsFragmentVM.ViewModel::class)
class PointsFragment : BaseFragment<PointsFragmentVM.ViewModel>() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter : CardAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_points, container, false)
        recyclerView = view.findViewById(R.id.points_recycler_view)
        adapter = CardAdapter(activity!!)
        val manager = GridLayoutManager(activity,2)
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter



        // Inflate the layout for this fragment
        return view
    }

    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.viewModel.inputs.fetchPaymentMethods()
        this.viewModel.outputs.paymentMethods().observeOn(AndroidSchedulers.mainThread()).subscribe {
            adapter.setPaymentMethods(it)
        }
        this.viewModel.outputs.newData().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                this.viewModel.inputs.fetchPaymentMethods()
            }


    }


}
