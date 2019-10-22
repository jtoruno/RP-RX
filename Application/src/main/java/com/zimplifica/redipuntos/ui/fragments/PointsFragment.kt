package com.zimplifica.redipuntos.ui.fragments


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseFragment
import com.zimplifica.redipuntos.libs.qualifiers.RequiresFragmentViewModel
import com.zimplifica.redipuntos.ui.adapters.CardAdapter
import com.zimplifica.redipuntos.viewModels.PointsFragmentVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

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
            class MyDialogFragment : androidx.fragment.app.DialogFragment() {
                override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                    return AlertDialog.Builder(activity!!)
                        .setTitle(getString(R.string.Alert))
                        .setMessage(getString(R.string.Delete_payment_method_message))
                        .setPositiveButton(getString(R.string.Delete)) { _, _ ->
                            this@PointsFragment.viewModel.inputs.disablePaymentMethodPressed(it)

                        }
                        .setNegativeButton(getString(R.string.Cancel),null)
                        .create()
                }
            }
            MyDialogFragment().show(fragmentManager!!,"disablePayment")

            //Toast.makeText(activity!!,"Click",Toast.LENGTH_SHORT).show()
        }
        val manager = GridLayoutManager(activity, 2)
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
                showError(getString(R.string.Alert),it)
            })

        compositeDisposable.add(this.viewModel.outputs.disablePaymentMethodAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showError(getString(R.string.Alert),getString(R.string.Delete_payment_method_sucess))
            })
    }

    private fun showError(title : String, message: String){
        class MyDialogFragment2 : androidx.fragment.app.DialogFragment() {
            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                return androidx.appcompat.app.AlertDialog.Builder(activity!!)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.Close),null)
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
