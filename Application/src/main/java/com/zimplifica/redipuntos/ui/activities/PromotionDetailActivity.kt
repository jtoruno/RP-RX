package com.zimplifica.redipuntos.ui.activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.squareup.picasso.Picasso
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.models.SocialNetworkObject
import com.zimplifica.redipuntos.ui.adapters.LocationAdapter
import com.zimplifica.redipuntos.viewModels.PromotionDetailVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_promotion_detail.*
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.ViewGroup
import android.widget.LinearLayout
import com.zimplifica.domain.entities.Schedule
import com.zimplifica.domain.entities.ShoppingHour


@RequiresActivityViewModel(PromotionDetailVM.ViewModel::class)
class PromotionDetailActivity : BaseActivity<PromotionDetailVM.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()
    lateinit var recyclerView : RecyclerView
    lateinit var adapter : LocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promotion_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        promotion_detail_fb.visibility = View.GONE
        promotion_detail_web.visibility = View.GONE
        promotion_detail_wtapp.visibility = View.GONE
        promotion_detail_instagram.visibility = View.GONE

        recyclerView = promotion_detail_recycler_view
        adapter = LocationAdapter{
            viewModel.inputs.locationPressed(it)
        }
        recyclerView.adapter = adapter
        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        recyclerView.layoutManager = manager
        recyclerView.setHasFixedSize(true)
        val divider = DividerItemDecoration(this,manager.orientation)
        recyclerView.addItemDecoration(divider)

        compositeDisposable.add(viewModel.outputs.promotionAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                supportActionBar?.title = it.commerceName
                Picasso.get().load(it.promotionImage).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(promotion_detail_img)
                promotion_detail_title.text = it.title
                promotion_detail_description.text = it.description
                promotion_detail_date1.text = "De : "+it.validFrom
                promotion_detail_date2.text = "Hasta : "+it.validTo
                promotion_detail_restrictions.text = it.restrictions
                promotion_detail_forms_to_use.text = it.waysToUse
                adapter.setStores(it.stores)
                if(it.facebook.isNotEmpty()){
                    showSocialNetwork(SocialNetworkObject(it.facebook,"facebook"))
                }
                if(it.instagram.isNotEmpty()){
                    showSocialNetwork(SocialNetworkObject(it.instagram,"instagram"))
                }
                if(it.whatsapp.isNotEmpty()){
                    showSocialNetwork(SocialNetworkObject(it.whatsapp,"whatsapp"))
                }
                if(it.website.isNotEmpty()){
                    showSocialNetwork(SocialNetworkObject(it.website,"website"))
                }
            })

        compositeDisposable.add(viewModel.outputs.openUrlAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                //Log.e("Example",it.image)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(it.url)
                startActivity(intent)
            })

        compositeDisposable.add(viewModel.outputs.locationAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val uri = Uri.parse("geo:"+it.latitude+","+it.longitude)
                val intent = Intent(Intent.ACTION_VIEW,uri)
                //intent.`package` = "com.google.android.apps.maps"
                startActivity(intent)
            })
    }

    private fun showSocialNetwork(obj : SocialNetworkObject){
        when(obj.image){
            "instagram" ->{
                promotion_detail_instagram.visibility = View.VISIBLE
                promotion_detail_instagram.setOnClickListener {
                    viewModel.inputs.openUrl(obj)
                }
            }
            "facebook" ->{
                promotion_detail_fb.visibility = View.VISIBLE
                promotion_detail_fb.setOnClickListener {
                    viewModel.inputs.openUrl(obj)
                }
            }
            "whatsapp" ->{
                promotion_detail_wtapp.visibility = View.VISIBLE
                promotion_detail_wtapp.setOnClickListener {
                    viewModel.inputs.openUrl(obj)
                }
            }
            "website" ->{
                promotion_detail_web.visibility = View.VISIBLE
                promotion_detail_web.setOnClickListener {
                    viewModel.inputs.openUrl(obj)
                }
            }
        }
    }

    private fun showDialog(schedule : Schedule){
        val dialog = Dialog(this)
        dialog.setTitle("Horario")
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        linearLayout.layoutParams = params
    }

    private fun showSchedule(view : LinearLayout, obj : ShoppingHour){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.schedule_row_view, null)
        // Add the new row before the add field button.
       view.addView(rowView, view.childCount - 1)
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
