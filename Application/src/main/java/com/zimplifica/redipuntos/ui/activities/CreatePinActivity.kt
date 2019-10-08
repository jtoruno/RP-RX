package com.zimplifica.redipuntos.ui.activities

import android.os.Bundle
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.CreatePinVM


@RequiresActivityViewModel(CreatePinVM.ViewModel::class)
class CreatePinActivity : BaseActivity<CreatePinVM.ViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_pin)
    }
}
