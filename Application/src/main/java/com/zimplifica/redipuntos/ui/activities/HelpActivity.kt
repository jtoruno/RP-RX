package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.zimplifica.redipuntos.BuildConfig
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.HelpViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_help.*
import java.util.*

@RequiresActivityViewModel(HelpViewModel.ViewModel::class)
class HelpActivity : BaseActivity<HelpViewModel.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        this.supportActionBar?.title = getString(R.string.Help)
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

        help_app_version.text = getString(R.string.App_version,BuildConfig.VERSION_NAME)
        val date = Calendar.getInstance()
        val year = date.get(Calendar.YEAR)
        help_app_copyright.text = getString(R.string.App_copyright,getString(R.string.app_name),year)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
