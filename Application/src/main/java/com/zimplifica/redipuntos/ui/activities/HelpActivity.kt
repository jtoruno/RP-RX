package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.HelpViewModel
import io.reactivex.disposables.CompositeDisposable

@RequiresActivityViewModel(HelpViewModel.ViewModel::class)
class HelpActivity : BaseActivity<HelpViewModel.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        this.supportActionBar?.title = "Ayuda"
        val termnsBtn = findViewById<Button>(R.id.termsHelpBtn)
        val privacyBtn : Button = findViewById(R.id.privacityHelpBtn)

        termnsBtn.setOnClickListener { this.viewModel.inputs.termsButtonClicked() }
        privacyBtn.setOnClickListener { this.viewModel.inputs.privacyButtonClicked() }

        compositeDisposable.add(this.viewModel.outputs.startTermsWebView().subscribe {
            val intent = Intent(this, TermsActivity::class.java)
            startActivity(intent)
        })

        compositeDisposable.add(this.viewModel.outputs.startPrivacyWebView().subscribe {
            val intent = Intent(this, PrivacyActivity::class.java)
            startActivity(intent)
        })
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
