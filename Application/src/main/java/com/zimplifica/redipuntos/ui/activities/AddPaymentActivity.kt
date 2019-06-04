package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.onChange
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.models.CreditCardExpirationDate
import com.zimplifica.redipuntos.models.CreditCardNumber
import com.zimplifica.redipuntos.viewModels.AddPaymentVM
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_add_payment.*

@RequiresActivityViewModel(AddPaymentVM.ViewModel::class)
class AddPaymentActivity : BaseActivity<AddPaymentVM.ViewModel>() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_payment)
        supportActionBar?.title = "Método de Pago"
        add_payment_name.onChange { this.viewModel.inputs.cardHolderChanged(it) }
        add_payment_number.addTextChangedListener(cardNumberWatcher)
        //add_payment_number.onChange { this.viewModel.inputs.cardNumberChanged(it) }

        add_payment_exp_date.onChange { this.viewModel.inputs.cardExpirationChanged(it) }
        add_payment_cvv.onChange { this.viewModel.inputs.cardSecurityCodeChanged(it) }

        viewModel.outputs.cardHolder().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                add_payment_name.text = Editable.Factory.getInstance().newEditable(it)
            }

        viewModel.outputs.cardNumber().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                //add_payment_number.setText(it.valueFormatted)
                when(it.issuer){
                    CreditCardNumber.Issuer.AMEX -> {
                        add_payment_cvv.hint = "CID"

                    }
                    CreditCardNumber.Issuer.MASTERCARD -> {
                        add_payment_cvv.hint = "CVV"
                    }
                    CreditCardNumber.Issuer.VISA -> {
                        add_payment_cvv.hint = "CVV"
                    }
                    CreditCardNumber.Issuer.DISCOVER -> {
                        add_payment_cvv.hint = "CVV"
                    }
                    CreditCardNumber.Issuer.UNKNOWN -> {
                        add_payment_cvv.hint = "CVV"
                    }
                }

                when(it.status){
                    CreditCardNumber.Status.invalid -> {
                        textInputLayout12.error = "Ingrese un número de tarjeta válido"
                    }
                    CreditCardNumber.Status.valid -> {
                        textInputLayout12.error = null
                        //self?.expirationDataTextField.input.becomeFirstResponder()   Preguntar
                    }
                    CreditCardNumber.Status.unkown -> {
                        textInputLayout12.error = null
                    }
                }
            }

        viewModel.outputs.cardExpirationDate().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                add_payment_exp_date.setText(it.valueFormatted)
                when(it.isValid){
                    CreditCardExpirationDate.ExpirationDateStatus.invalid -> {
                        input_layout_exp_date.error = "Ingrese una fecha válida"
                    }
                    else -> {
                        input_layout_exp_date.error = null
                    }
                }
            }

        viewModel.outputs.cardSecurityCode().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                add_payment_cvv.setText(it.valueFormatted)
            }
    }

    private val cardNumberWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            this@AddPaymentActivity.viewModel.inputs.cardNumberChanged(s?.toString() ?: "")
        }

    }
}
