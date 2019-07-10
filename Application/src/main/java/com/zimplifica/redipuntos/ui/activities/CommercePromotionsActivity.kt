package com.zimplifica.redipuntos.ui.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.ui.adapters.PromotionAdapter
import com.zimplifica.redipuntos.viewModels.CommercePromotionsVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_commerce_promotions.*

@RequiresActivityViewModel(CommercePromotionsVM.ViewModel::class)
class CommercePromotionsActivity : BaseActivity<CommercePromotionsVM.ViewModel>() {
    lateinit var recyclerView : RecyclerView
    lateinit var adapter : PromotionAdapter
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commerce_promotions)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        recyclerView = commerce_promotions_recycler_view
        adapter = PromotionAdapter { viewModel.inputs.promotionSelected(it) }
        recyclerView.adapter = adapter
        val manager = GridLayoutManager(this,2)
        recyclerView.layoutManager = manager
        compositeDisposable.add(viewModel.outputs.commerceResult().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                supportActionBar?.title = it.name
                adapter.setPromotions(it.promotions)
            })

        compositeDisposable.add(viewModel.outputs.promotionSelectedAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this, PromotionDetailActivity::class.java)
                intent.putExtra("promotion",it)
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
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
