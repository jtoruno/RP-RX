package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.extensions.capitalizeWords
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.AccountInfoVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_account_info.*


@RequiresActivityViewModel(AccountInfoVM.ViewModel::class)
class AccountInfoActivity : BaseActivity<AccountInfoVM.ViewModel>() {

    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_info)
        supportActionBar?.title = "Cuenta"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        compositeDisposable.add(this.viewModel.outputs.userInformationAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val firstName = (it?.userFirstName?:"").toLowerCase()
                val lastName = (it?.userLastName?:"").toLowerCase()
                account_info_name.setText(firstName.capitalizeWords())
                account_info_last_name.setText(lastName.capitalizeWords())
                account_info_id_card.setText(it?.citizenId?:"")
                account_info_date.setText(it?.userBirthDate?:"")
                account_info_email.setText(it?.userEmail?:"")
                if (it?.userEmailVerified == true){
                    account_info_state.text = "Verificado"
                    account_info_state.setTextColor(getColor(R.color.customGreen))
                }else{
                    account_info_state.text = "No verificado"
                    account_info_state.setTextColor(getColor(R.color.red))
                }
            })
        account_info_ll.setOnClickListener {
            this.viewModel.inputs.verifyEmailPressed()
        }

        account_info_email.setOnClickListener {
            this.viewModel.inputs.verifyEmailPressed()
        }

        compositeDisposable.add(this.viewModel.outputs.verifyEmailAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this, AccountEditEmailActivity::class.java)
                startActivity(intent)

            })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
