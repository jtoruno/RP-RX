package com.zimplifica.redipuntos.ui.fragments


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast

import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseFragment
import com.zimplifica.redipuntos.libs.qualifiers.RequiresFragmentViewModel
import com.zimplifica.redipuntos.services.GlobalState
import com.zimplifica.redipuntos.ui.adapters.CardAdapter
import com.zimplifica.redipuntos.viewModels.PointsFragmentVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

@RequiresFragmentViewModel(PointsFragmentVM.ViewModel::class)
class PointsFragment : BaseFragment<PointsFragmentVM.ViewModel>() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter : CardAdapter
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_points, container, false)
        recyclerView = view.findViewById(R.id.points_recycler_view)

        adapter = CardAdapter(activity!!){
            class MyDialogFragment : DialogFragment() {
                override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                    return AlertDialog.Builder(activity!!)
                        .setTitle("¡Alerta!")
                        .setMessage("¿Deseas realmente eliminar este método de pago?")
                        .setPositiveButton("Eliminar") { dialog, which ->
                            this@PointsFragment.viewModel.inputs.disablePaymentMethodPressed(it)

                        }
                        .setNegativeButton("Mejor no",null)
                        .create()
                }
            }
            MyDialogFragment().show(fragmentManager!!,"disablePayment")

            //Toast.makeText(activity!!,"Click",Toast.LENGTH_SHORT).show()
        }
        val manager = GridLayoutManager(activity,2)
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter

        compositeDisposable.add(this.viewModel.outputs.showDisablePaymentMethodAlertAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {

            })

        compositeDisposable.add(this.viewModel.outputs.loading().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it){
                    view.findViewById<LinearLayout>(R.id.loading_linear_ll).visibility = View.VISIBLE
                }else{
                    view.findViewById<LinearLayout>(R.id.loading_linear_ll).visibility = View.GONE
                }
            })





        // Inflate the layout for this fragment
        return view
    }

    @SuppressLint("CheckResult")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        this.viewModel.inputs.fetchPaymentMethods()
        compositeDisposable.add(this.viewModel.outputs.paymentMethods().observeOn(AndroidSchedulers.mainThread()).subscribe {
            adapter.setPaymentMethods(it)
            adapter.notifyDataSetChanged()
        })
        compositeDisposable.add(this.viewModel.outputs.newData().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                this.viewModel.inputs.fetchPaymentMethods()
            })

        compositeDisposable.add(this.viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showError("Alerta",it)
            })

        compositeDisposable.add(this.viewModel.outputs.disablePaymentMethodAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showError("Alerta","Método de pago eliminado correctamente.")
            })
    }

    private fun showError(title : String, message: String){
        class MyDialogFragment2 : DialogFragment() {
            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                return android.support.v7.app.AlertDialog.Builder(activity!!)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Cerrar",null)
                    .create()
            }
        }
        MyDialogFragment2().show(fragmentManager!!,"showError")
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }


}
