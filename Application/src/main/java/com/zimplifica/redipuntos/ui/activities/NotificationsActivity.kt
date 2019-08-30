package com.zimplifica.redipuntos.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.ui.adapters.NotificationAdapter
import com.zimplifica.redipuntos.viewModels.NotificationsVM
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_notifications.*

@RequiresActivityViewModel(NotificationsVM.ViewModel::class)
class NotificationsActivity : BaseActivity<NotificationsVM.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()
    lateinit var adapter : NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        supportActionBar?.title = "Notificaciones"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        val manager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        notification_recycler_view.layoutManager = manager
        notification_recycler_view.setHasFixedSize(true)
        val divider = DividerItemDecoration(this, manager.orientation)
        notification_recycler_view.addItemDecoration(divider)
        adapter = NotificationAdapter()
        notification_recycler_view.adapter = adapter

        notification_swipe.setOnRefreshListener {
            viewModel.inputs.swiped()
        }

        compositeDisposable.add(viewModel.outputs.updateNotifications().observeOn(AndroidSchedulers.mainThread()).subscribe {
            if (it.isEmpty()){
                notification_no_message.visibility = View.VISIBLE
            }else{
                notification_no_message.visibility = View.GONE
            }
            notification_swipe.isRefreshing = false
            adapter.setNotifications(it)
        })

        viewModel.inputs.onCreate()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

}
