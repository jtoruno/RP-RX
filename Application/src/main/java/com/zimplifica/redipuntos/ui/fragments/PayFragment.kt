package com.zimplifica.redipuntos.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseFragment
import com.zimplifica.redipuntos.libs.qualifiers.RequiresFragmentViewModel
import com.zimplifica.redipuntos.viewModels.PayFragmentVM
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_pay.*
import android.widget.Toast





@RequiresFragmentViewModel(PayFragmentVM.ViewModel::class)
class PayFragment : BaseFragment<PayFragmentVM.ViewModel>() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pay, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.viewModel.outputs.nextButtonEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe { pay_fragment_btn.isEnabled = it }
        this.viewModel.outputs.changeAmountAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe { pay_fragment_amount.text = it }
    }


    fun keyBoardAction(view: View) {
        Toast.makeText(activity, "Toast Hello", Toast.LENGTH_LONG).show()
    }



}
