package com.zimplifica.redipuntos.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseFragment
import com.zimplifica.redipuntos.libs.qualifiers.RequiresFragmentViewModel
import com.zimplifica.redipuntos.viewModels.PayFragmentVM
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_pay.*
import android.widget.Toast
import kotlinx.android.synthetic.main.number_keyboard.*


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
        button_1.setOnClickListener(clickAction)
        button_2.setOnClickListener(clickAction)
        button_3.setOnClickListener(clickAction)
        button_4.setOnClickListener(clickAction)
        button_5.setOnClickListener(clickAction)
        button_6.setOnClickListener(clickAction)
        button_7.setOnClickListener(clickAction)
        button_8.setOnClickListener(clickAction)
        button_9.setOnClickListener(clickAction)
        button_0.setOnClickListener(clickAction)
        button_delete.setOnClickListener(clickAction)

        super.onActivityCreated(savedInstanceState)
        this.viewModel.outputs.nextButtonEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                pay_fragment_btn.isEnabled = it
                pay_fragment_btn.alpha = if(it){1.0F}else{0.5F}
            }
        this.viewModel.outputs.changeAmountAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe { pay_fragment_amount.text = it }
    }

    private val clickAction = View.OnClickListener {
        val text = (it as Button).text
        this.viewModel.inputs.keyPressed(text.toString())
    }
}
