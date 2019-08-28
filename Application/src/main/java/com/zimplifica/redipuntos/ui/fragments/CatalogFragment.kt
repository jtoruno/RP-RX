package com.zimplifica.redipuntos.ui.fragments


import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.zimplifica.domain.entities.Category

import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseFragment
import com.zimplifica.redipuntos.libs.qualifiers.RequiresFragmentViewModel
import com.zimplifica.redipuntos.ui.activities.CategoriesActivity
import com.zimplifica.redipuntos.ui.activities.CommercePromotionsActivity
import com.zimplifica.redipuntos.ui.activities.PromotionDetailActivity
import com.zimplifica.redipuntos.ui.adapters.CommerceAdapter
import com.zimplifica.redipuntos.viewModels.CommercesFragmentVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_catalog.view.*

@RequiresFragmentViewModel(CommercesFragmentVM.ViewModel::class)
class CatalogFragment : BaseFragment<CommercesFragmentVM.ViewModel>() {
    lateinit var recyclerView : androidx.recyclerview.widget.RecyclerView
    lateinit var group : ChipGroup
    lateinit var adapter : CommerceAdapter
    private val compositeDisposable = CompositeDisposable()
    lateinit var chip : Chip
    companion object {
        val REQUEST_CATEGORY = 1023
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_catalog, container, false)
        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchView = view.commerce_search_view
        chip = view.commerce_chip
        chip.visibility = View.GONE
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.queryHint = "Buscar comercio..."
        val autoComplete = searchView.findViewById<SearchView.SearchAutoComplete>(R.id.search_src_text)
        autoComplete.setHintTextColor(activity!!.getColor(R.color.grayIconTint))
        autoComplete.setTextColor(activity!!.getColor(android.R.color.white))

        val icon = searchView.findViewById<ImageView>(R.id.search_button)
        icon.setColorFilter(activity!!.getColor(R.color.grayIconTint))
        val iconClose = searchView.findViewById<ImageView>(R.id.search_close_btn)
        iconClose.setColorFilter(activity!!.getColor(R.color.grayIconTint))
        val icon2 = searchView.findViewById<ImageView>(R.id.search_mag_icon)
        icon2.setColorFilter(activity!!.getColor(R.color.grayIconTint))
        val plate = searchView.findViewById<View>(R.id.search_plate)
        //plate.setBackgroundColor(activity!!.getColor(R.color.searchPlateColor))

        val frame = searchView.findViewById<View>(R.id.search_edit_frame)
        frame.setBackgroundColor(activity!!.getColor(R.color.searchPlateColor))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                viewModel.inputs.searchButtonPressed(p0?:"")
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0!= null && p0.isEmpty()){
                    //viewModel.inputs.fetchCommerces()
                    viewModel.inputs.searchButtonPressed("")
                }
                return true
            }
        })

        searchView.setOnCloseListener(object : SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                Log.e("Hello","close")
                //viewModel.inputs.fetchCommerces()
                return true
            }

        })

        chip.setOnCloseIconClickListener {
            chip.visibility = View.GONE
            val category = Category("","","")
            //searchView.visibility = View.VISIBLE
            viewModel.inputs.filterByCategory(category)
        }

        chip.setOnClickListener {
            viewModel.inputs.filterButtonPressed()
        }

        recyclerView = view.commerce_recycler_view
        val manager = GridLayoutManager(activity, 2)
        recyclerView.layoutManager = manager
        adapter = CommerceAdapter{
            viewModel.inputs.commerceSelected(it)
        }
        recyclerView.adapter = adapter
        viewModel.inputs.fetchCommerces()

        view.commerce_swipe_refresh.setOnRefreshListener {
            viewModel.inputs.fetchCommerces()
        }

        //VM Section
        compositeDisposable.add(viewModel.outputs.commerces().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.commerces.isEmpty()){
                    view.commerce_no_commerce_description.visibility = View.VISIBLE
                }else{
                    view.commerce_no_commerce_description.visibility = View.GONE
                }
                view.commerce_swipe_refresh.isRefreshing = false
                adapter.setCommerces(it.commerces)
            })

        compositeDisposable.add(viewModel.outputs.showError().observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                view.commerce_swipe_refresh.isRefreshing = false
                Toast.makeText(activity,it,Toast.LENGTH_SHORT).show()
            })

        compositeDisposable.add(viewModel.outputs.filterButtonAction().observeOn(AndroidSchedulers.mainThread()).subscribe {
            val intent = Intent(activity,CategoriesActivity::class.java)
            startActivityForResult(intent, REQUEST_CATEGORY)
        })

        compositeDisposable.add(viewModel.outputs.categorySelectedAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                chip.text = it.name
                chip.visibility = View.VISIBLE
                //searchView.visibility = View.GONE
            })

        compositeDisposable.add(viewModel.outputs.commerceSelectedAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                val intent = Intent(activity,PromotionDetailActivity::class.java)
                intent.putExtra("commerce", it)
                startActivity(intent)
            })
    }

    fun filterAction(){
        viewModel.inputs.filterButtonPressed()
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CATEGORY){
            if(resultCode == Activity.RESULT_OK){
                val category = data?.getSerializableExtra("category") as? Category
                if(category != null){
                    viewModel.inputs.categorySelected(category)
                    viewModel.inputs.filterByCategory(category)
                }
            }
        }
    }


}
