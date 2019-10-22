package com.zimplifica.redipuntos.ui.activities

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.zimplifica.domain.entities.Schedule
import com.zimplifica.domain.entities.ShoppingHour
import java.util.*


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
        adapter = LocationAdapter({viewModel.inputs.locationPressed(it)}){
            showDialog(it)
        }
        recyclerView.adapter = adapter
        val manager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )
        recyclerView.layoutManager = manager
        recyclerView.setHasFixedSize(true)
        val divider = DividerItemDecoration(this, manager.orientation)
        recyclerView.addItemDecoration(divider)

        compositeDisposable.add(viewModel.outputs.commerceAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                supportActionBar?.title = it.name
                Picasso.get().load(it.posterImage).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(promotion_detail_img)
                promotion_detail_title.text = getString(R.string.RediCash)
                promotion_detail_description.text = it.description
                val rediCash = it.offer?.discount ?: 0
                promotion_detail_cash_back.text = "$rediCash%"
                //promotion_detail_date1.text = "De : "+it.validFrom
                //promotion_detail_date2.text = "Hasta : "+it.validTo
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
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(it.url)
                startActivity(intent)
            })

        compositeDisposable.add(viewModel.outputs.locationAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val uri = Uri.parse("geo:"+it.latitude+","+it.longitude)
                val intent = Intent(Intent.ACTION_VIEW,uri)
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
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(getString(R.string.Schedule))
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.setMargins(0,30,0,0)
        linearLayout.layoutParams = params
        showSchedule(linearLayout,schedule.mon, getString(R.string.Monday))
        showSchedule(linearLayout,schedule.tue, getString(R.string.Tuesday))
        showSchedule(linearLayout,schedule.wed,getString(R.string.Wednesday))
        showSchedule(linearLayout,schedule.thu,getString(R.string.Thursday))
        showSchedule(linearLayout,schedule.fri,getString(R.string.Friday))
        showSchedule(linearLayout,schedule.sat,getString(R.string.Saturday))
        showSchedule(linearLayout,schedule.sun,getString(R.string.Sunday))
        dialog.setView(linearLayout)
        dialog.setPositiveButton(getString(R.string.Close),null)
        val view = dialog.create()
        val window = view.window
        val wlp = window?.attributes
        wlp?.gravity = Gravity.BOTTOM
        window?.attributes = wlp
        view.show()

    }

    private fun showSchedule(view : LinearLayout, obj : ShoppingHour, date : String){
        val calendar = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.schedule_row_view, null)
        val hour1 = rowView.findViewById<TextView>(R.id.schedule_row_hour1)
        val hour2 = rowView.findViewById<TextView>(R.id.schedule_row_hour2)
        val day = rowView.findViewById<TextView>(R.id.schedule_row_date)
        val middleDescription = rowView.findViewById<TextView>(R.id.schedule_row_until)
        if(obj.open){
            hour1.text = obj.openningHour

            hour2.text = obj.closingHour
        }else{
            hour1.text = getString(R.string.Closed)
            hour2.visibility = View.INVISIBLE
            middleDescription.visibility = View.INVISIBLE
        }
        day.text = date

        if(obj.weekday == calendar){
            hour1.setTextColor(getColor(android.R.color.black))
            hour2.setTextColor(getColor(android.R.color.black))
            day.setTextColor(getColor(android.R.color.black))
            middleDescription.setTextColor(getColor(android.R.color.black))
        }
        // Add the new row before the add field button.
        view.addView(rowView)
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
