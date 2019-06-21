package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.AccountInfoVM
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_account_info.*


@RequiresActivityViewModel(AccountInfoVM.ViewModel::class)
class AccountInfoActivity : BaseActivity<AccountInfoVM.ViewModel>() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_info)
        supportActionBar?.title = "Cuenta"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        this.viewModel.outputs.userInformationAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                account_info_name.setText(it?.userFirstName?:"")
                account_info_last_name.setText(it?.userLastName?:"")
                account_info_id_card.setText(it?.citizenId?:"")
                account_info_date.setText(it?.userBirthDate?:"")
                account_info_email.setText(it?.userEmail?:"")
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }
}
