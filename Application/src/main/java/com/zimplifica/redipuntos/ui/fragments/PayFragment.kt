package com.zimplifica.redipuntos.ui.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
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
import com.zimplifica.redipuntos.models.ManagerNav
import com.zimplifica.redipuntos.ui.activities.SPScanQRActivity
import com.zimplifica.redipuntos.ui.activities.SPSelectionActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.number_keyboard.*


@RequiresFragmentViewModel(PayFragmentVM.ViewModel::class)
class PayFragment : BaseFragment<PayFragmentVM.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

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

        pay_fragment_btn.setOnClickListener { this.viewModel.inputs.nextButtonPressed() }

        super.onActivityCreated(savedInstanceState)
        compositeDisposable.add(this.viewModel.outputs.nextButtonEnabled().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                pay_fragment_btn.isEnabled = it
                pay_fragment_btn.alpha = if(it){1.0F}else{0.5F}
            })
        compositeDisposable.add(this.viewModel.outputs.changeAmountAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe { pay_fragment_amount.text = it })

        compositeDisposable.add(this.viewModel.outputs.nextButtonAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                //val intent = Intent(activity!!,SPSelectionActivity::class.java)
                val intent = Intent(activity!!,SPScanQRActivity::class.java)
                intent.putExtra("amount", it)
                startActivity(intent)
            })

        compositeDisposable.add(this.viewModel.outputs.showCompletePersonalInfoAlert().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                class MyDialogFragment : androidx.fragment.app.DialogFragment() {
                    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                        return AlertDialog.Builder(activity!!)
                            .setTitle("Completar Información Personal")
                            .setMessage("RediPuntos requiere saber un poco mas de ti, ¿deseas completar tu información?")
                            .setPositiveButton("Completar Información") { dialog, which ->
                                this@PayFragment.viewModel.inputs.completePersonalInfoButtonPressed()
                            }
                            .setNegativeButton("Luego",null)
                            .create()
                    }
                }
                MyDialogFragment().show(fragmentManager!!,"personalInfo")

            })

        compositeDisposable.add(this.viewModel.outputs.goToCompletePersonalInfoScreen().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                /*
                val intent = Intent(this,CompletePaymentActivity::class.java)
                startActivity(intent)*/
                ManagerNav.getInstance(activity!!).initNav()
            })
    }

    private val clickAction = View.OnClickListener {
        val text = (it as Button).text
        this.viewModel.inputs.keyPressed(text.toString())
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    override fun onResume() {
        viewModel.inputs.resetAmount()
        super.onResume()
    }
}
