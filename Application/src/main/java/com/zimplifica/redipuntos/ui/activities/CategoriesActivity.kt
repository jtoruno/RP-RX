package com.zimplifica.redipuntos.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.ui.adapters.CategoryAdapter
import com.zimplifica.redipuntos.ui.fragments.CatalogFragment
import com.zimplifica.redipuntos.viewModels.CategoriesVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable


@RequiresActivityViewModel(CategoriesVM.ViewModel::class)
class CategoriesActivity : BaseActivity<CategoriesVM.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()
    lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    lateinit var adapter : CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)
        supportActionBar?.title = "Categorías"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        recyclerView = findViewById(R.id.categories_recycler_view)
        val manager = androidx.recyclerview.widget.GridLayoutManager(this, 2)
        recyclerView.layoutManager = manager
        adapter = CategoryAdapter{
            viewModel.inputs.categorySelected(it)
        }

        recyclerView.adapter = adapter

        compositeDisposable.add(viewModel.outputs.categoriesResult().observeOn(AndroidSchedulers.mainThread()).subscribe {
            adapter.setCategories(it)
        })

        compositeDisposable.add(viewModel.outputs.categorySelectedAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val returnIntent = Intent()
                returnIntent.putExtra("category",it)
                setResult(Activity.RESULT_OK,returnIntent)
                finish()
            })

        viewModel.inputs.fetchCategories()


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
