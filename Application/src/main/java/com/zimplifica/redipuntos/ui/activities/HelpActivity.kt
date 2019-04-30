package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.HelpViewModel

@RequiresActivityViewModel(HelpViewModel.ViewModel::class)
class HelpActivity : BaseActivity<HelpViewModel.ViewModel>() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        this.supportActionBar?.title = "Ayuda"
        val termnsBtn = findViewById<Button>(R.id.termsHelpBtn)
        val privacyBtn : Button = findViewById(R.id.privacityHelpBtn)

        termnsBtn.setOnClickListener { this.viewModel.inputs.termsButtonClicked() }
        privacyBtn.setOnClickListener { this.viewModel.inputs.privacyButtonClicked() }

        this.viewModel.outputs.startTermsWebView().subscribe {
            val intent = Intent(this, TermsActivity::class.java)
            startActivity(intent)
        }

        this.viewModel.outputs.startPrivacyWebView().subscribe {
            val intent = Intent(this, PrivacyActivity::class.java)
            startActivity(intent)
        }
    }
}
