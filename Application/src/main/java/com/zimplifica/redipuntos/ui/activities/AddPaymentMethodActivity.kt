package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.models.CreditCardExpirationDate
import com.zimplifica.redipuntos.models.CreditCardNumber
import com.zimplifica.redipuntos.viewModels.AddPaymentMethodVM
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_add_payment_method.*

@RequiresActivityViewModel(AddPaymentMethodVM.ViewModel::class)
class AddPaymentMethodActivity : BaseActivity<AddPaymentMethodVM.ViewModel>() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_payment_method)
        supportActionBar?.title = "Método de Pago"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        progressBar10.visibility = View.GONE

        add_payment_method_name.onChange { this.viewModel.inputs.cardHolderChanged(it) }
        add_payment_method_number.onChange {
            val text = it.replace(" ","")
            this.viewModel.inputs.cardNumberChanged(text)
        }
        add_payment_method_exp_date.onChange { this.viewModel.inputs.cardExpirationChanged(it) }
        add_payment_method_exp_date.addTextChangedListener(textWatcher)
        add_payment_method_cvv.onChange { this.viewModel.inputs.cardSecurityCodeChanged(it) }
        add_payment_method_btn.setOnClickListener {
            this.viewModel.inputs.addPaymentMethodButtonPressed()
        }

        viewModel.outputs.cardNumber().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                when(it.issuer){
                    CreditCardNumber.Issuer.AMEX -> {
                        //add_payment_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.amex,0,0,0)
                        add_payment_cvv_layout2.hint = "CID"
                        add_payment_method_cvv.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(4))

                    }
                    CreditCardNumber.Issuer.MASTERCARD -> {
                        //add_payment_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mastercard,0,0,0)
                        add_payment_cvv_layout2.hint = "CVV"
                        add_payment_method_cvv.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
                    }
                    CreditCardNumber.Issuer.VISA -> {
                        ////add_payment_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.visa,0,0,0)
                        add_payment_cvv_layout2.hint = "CVV"
                        add_payment_method_cvv.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
                    }
                    CreditCardNumber.Issuer.DISCOVER -> {
                        //add_payment_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.discover,0,0,0)
                        add_payment_cvv_layout2.hint = "CVV"
                        add_payment_method_cvv.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
                    }
                    CreditCardNumber.Issuer.UNKNOWN -> {
                        //add_payment_number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.creditcard,0,0,0)
                        add_payment_cvv_layout2.hint = "CVV"
                        add_payment_method_cvv.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(3))
                    }
                }

                when(it.status){
                    CreditCardNumber.Status.invalid -> {
                        textInputLayout14.error = "Ingrese un número de tarjeta válido"
                    }
                    CreditCardNumber.Status.valid -> {
                        textInputLayout14.error = null
                        //self?.expirationDataTextField.input.becomeFirstResponder()   Preguntar
                    }
                    CreditCardNumber.Status.unkown -> {
                        textInputLayout14.error = null
                    }
                }
            }

        viewModel.outputs.cardExpirationDate().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                //add_payment_exp_date.setText(it.valueFormatted)
                when(it.isValid){
                    CreditCardExpirationDate.ExpirationDateStatus.invalid -> {
                        input_layout_exp_date2.error = "Ingrese una fecha válida"
                    }
                    else -> {
                        input_layout_exp_date2.error = null
                    }
                }
            }

        viewModel.outputs.isFormValid().observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                add_payment_method_btn.isEnabled = it
            }

        viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                showDialog("Lo sentimos", it)
            }

        viewModel.outputs.loading().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if(it){
                    add_payment_method_btn.isEnabled = false
                    progressBar10.visibility = View.VISIBLE
                }else{
                    add_payment_method_btn.isEnabled = true
                    progressBar10.visibility = View.GONE
                }
            }

        viewModel.outputs.paymentMethodSaved().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Toast.makeText(this,"Método de pago guardado correctamente",Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    private val textWatcher = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            var working = s.toString()
            if (working.length == 2 && before == 0) {
                if (Integer.parseInt(working) < 1 || Integer.parseInt(working) > 12) {
                    add_payment_method_exp_date.setText("")
                } else {
                    working += "/"
                    add_payment_method_exp_date.setText(working)
                    add_payment_method_exp_date.setSelection(working.length)
                }
            }
        }
    }

    private fun showDialog(title : String, message : String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Cerrar",null)
        val dialog = builder.create()
        dialog.show()
    }
}
