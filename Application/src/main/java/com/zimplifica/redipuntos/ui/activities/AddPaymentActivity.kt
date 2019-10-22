package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.models.CreditCardExpirationDate
import com.zimplifica.redipuntos.models.CreditCardNumber
import com.zimplifica.redipuntos.viewModels.AddPaymentVM
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_add_payment.*
import android.text.InputFilter
import android.view.View
import com.zimplifica.redipuntos.models.ManagerNav
import io.reactivex.disposables.CompositeDisposable


@RequiresActivityViewModel(AddPaymentVM.ViewModel::class)
class AddPaymentActivity : BaseActivity<AddPaymentVM.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_payment)
        supportActionBar?.title = getString(R.string.Payment_method)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        progressBar9.visibility = View.GONE

        add_payment_description_2.text = getString(R.string.Add_payment_method_securiy_description,getString(R.string.app_name))

        add_payment_name.onChange { this.viewModel.inputs.cardHolderChanged(it) }

        add_payment_number.onChange {
            val text = it.replace(" ","")
            this.viewModel.inputs.cardNumberChanged(text)
        }
        //add_payment_number.addTextChangedListener(textWatcher)
        add_payment_exp_date.onChange { this.viewModel.inputs.cardExpirationChanged(it) }
        add_payment_exp_date.addTextChangedListener(textWatcher)
        add_payment_cvv.onChange { this.viewModel.inputs.cardSecurityCodeChanged(it) }
        add_payment_btn.setOnClickListener {
            this.viewModel.inputs.addPaymentMethodButtonPressed()
        }


        compositeDisposable.add(viewModel.outputs.cardNumber().observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                //Log.e("CardNumber", it.valueFormatted)
                //add_payment_number.setText(it.valueFormatted)
                //add_payment_number.setText(it.valueFormatted)
                when(it.issuer){
                    CreditCardNumber.Issuer.AMEX -> {
                        //add_payment_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.amex,0,0,0)
                        add_payment_cvv_layout.hint = "CID"
                        add_payment_cvv.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(4))

                    }
                    CreditCardNumber.Issuer.MASTERCARD -> {
                        //add_payment_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mastercard,0,0,0)
                        add_payment_cvv_layout.hint = "CVV"
                        add_payment_cvv.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
                    }
                    CreditCardNumber.Issuer.VISA -> {
                        ////add_payment_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.visa,0,0,0)
                        add_payment_cvv_layout.hint = "CVV"
                        add_payment_cvv.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
                    }
                    CreditCardNumber.Issuer.DISCOVER -> {
                        //add_payment_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.discover,0,0,0)
                        add_payment_cvv_layout.hint = "CVV"
                        add_payment_cvv.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
                    }
                    CreditCardNumber.Issuer.UNKNOWN -> {
                        //add_payment_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.creditcard,0,0,0)
                        add_payment_cvv_layout.hint = "CVV"
                        add_payment_cvv.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
                    }
                }

                when(it.status){
                    CreditCardNumber.Status.invalid -> {
                        textInputLayout12.error = getString(R.string.Card_number_error)
                    }
                    CreditCardNumber.Status.valid -> {
                        textInputLayout12.error = null
                        //self?.expirationDataTextField.input.becomeFirstResponder()   Preguntar
                    }
                    CreditCardNumber.Status.unkown -> {
                        textInputLayout12.error = null
                    }
                }
            })

        compositeDisposable.add(viewModel.outputs.cardExpirationDate().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                //add_payment_exp_date.setText(it.valueFormatted)
                when(it.isValid){
                    CreditCardExpirationDate.ExpirationDateStatus.invalid -> {
                        input_layout_exp_date.error = getString(R.string.Card_expiration_date_error)
                    }
                    else -> {
                        input_layout_exp_date.error = null
                    }
                }
            })

        compositeDisposable.add(viewModel.outputs.isFormValid().observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                add_payment_btn.isEnabled = it
            })

        compositeDisposable.add(viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showDialog(getString(R.string.Sorry), it)
            })

        compositeDisposable.add(viewModel.outputs.loading().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if(it){
                    add_payment_btn.isEnabled = false
                    progressBar9.visibility = View.VISIBLE
                }else{
                    add_payment_btn.isEnabled = true
                    progressBar9.visibility = View.GONE
                }
            })

        compositeDisposable.add(viewModel.outputs.paymentMethodSaved().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                finish()
                ManagerNav.getInstance(this).initNav()
            })

    }

    private val textWatcher = object : TextWatcher{

        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            var working = s.toString()
            if (working.length == 2 && before == 0) {
                if (Integer.parseInt(working) < 1 || Integer.parseInt(working) > 12) {
                    add_payment_exp_date.setText("")
                } else {
                    working += "/"
                    add_payment_exp_date.setText(working)
                    add_payment_exp_date.setSelection(working.length)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    private fun showDialog(title : String, message : String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.Close),null)
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
