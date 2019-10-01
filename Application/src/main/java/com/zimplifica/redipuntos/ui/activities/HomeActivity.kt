package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.viewModels.HomeViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.hsalf.smilerating.SmileRating
import com.tapadoo.alerter.Alerter
import com.zimplifica.domain.entities.CommercesRate
import com.zimplifica.domain.entities.VerificationStatus
import com.zimplifica.redipuntos.extensions.capitalizeWords
import com.zimplifica.redipuntos.libs.utils.SharedPreferencesUtils
import com.zimplifica.redipuntos.models.ManagerNav
import com.zimplifica.redipuntos.ui.fragments.*
import io.reactivex.disposables.CompositeDisposable

import kotlinx.android.synthetic.main.nav_header_home.view.*


@RequiresActivityViewModel(HomeViewModel.ViewModel::class)
class HomeActivity : BaseActivity<HomeViewModel.ViewModel>() {
    private val compositeDisposable = CompositeDisposable()
    lateinit var Catalogfragment : androidx.fragment.app.Fragment
    lateinit var Payfragment : androidx.fragment.app.Fragment
    lateinit var Movementsfragment : androidx.fragment.app.Fragment
    lateinit var PointsFragment : androidx.fragment.app.Fragment
    lateinit var ProfileFragmentClass : androidx.fragment.app.Fragment
    private val fm = supportFragmentManager
    lateinit var active : androidx.fragment.app.Fragment
    private var menuActionBar : Menu ? = null
    lateinit var bottomNav : BottomNavigationView

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        toolbar.title = "RediPuntos"
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_notifications_black_24dp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Catalogfragment = CatalogFragment()
        Payfragment = PayFragment()
        PointsFragment = PointsFragment()
        Movementsfragment = MovementsFragment()
        ProfileFragmentClass =  ProfileFragment()
        active = Payfragment

        fm.beginTransaction().add(R.id.home_frame_layout,Catalogfragment,"commerce_fragment").hide(Catalogfragment).commit()
        fm.beginTransaction().add(R.id.home_frame_layout,Payfragment, "pay").commit()
        fm.beginTransaction().add(R.id.home_frame_layout,PointsFragment, "points").hide(PointsFragment).commit()
        fm.beginTransaction().add(R.id.home_frame_layout,Movementsfragment,"movements").hide(Movementsfragment).commit()

        fm.beginTransaction().add(R.id.home_frame_layout,ProfileFragmentClass, "profile").hide(ProfileFragmentClass).commit()

        bottomNav = findViewById(R.id.home_nav_bottom)
        bottomNav.setOnNavigationItemSelectedListener(navItemListener)
        bottomNav.selectedItemId = R.id.nav_pay


        /*
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val header = navigationView.getHeaderView(0)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        */
        compositeDisposable.add(viewModel.outputs.showRateCommerceAlert().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val model = it
                val alertDialog = AlertDialog.Builder(this)
                val row = layoutInflater.inflate(R.layout.dialog_rate_commerce,null)
                val smileRate = row.findViewById<SmileRating>(R.id.smile_rating)
                alertDialog.setView(row)
                alertDialog.setTitle(it.commerceName)
                alertDialog.setNegativeButton("Saltar"){
                    _,_ ->
                    viewModel.inputs.rateCommerceInput(model)
                }
                val alert = alertDialog.create()
                alert.show()
                smileRate.setOnSmileySelectionListener { smiley, reselected ->
                    when(smiley){
                        SmileRating.BAD ->{
                            model.rate = CommercesRate.POOR
                        }
                        SmileRating.GOOD ->{
                            model.rate = CommercesRate.GOOD
                        }
                        SmileRating.GREAT ->{
                            model.rate = CommercesRate.EXCELLENT
                        }
                        SmileRating.OKAY ->{
                            model.rate = CommercesRate.REGULAR
                        }
                        SmileRating.TERRIBLE ->{
                            model.rate = CommercesRate.BAD
                        }
                    }
                    viewModel.inputs.rateCommerceInput(model)
                    alert.dismiss()
                }
            })

        compositeDisposable.add(this.viewModel.outputs.signOutAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this, WalkThrough::class.java)
                startActivity(intent)
                finishAffinity()
            })

        compositeDisposable.add(viewModel.outputs.showAlert().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Alerter.create(this)
                    .setTitle(it.first)
                    .setText(it.second)
                    .enableSwipeToDismiss()
                    .setBackgroundColorRes(R.color.colorPrimary)
                    .enableInfiniteDuration(true)
                    .show()
            })

        compositeDisposable.add(this.viewModel.outputs.showCompletePersonalInfoAlert().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.e("HOme", it.name)
                when(it){
                    VerificationStatus.Pending -> {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Hola, ¿cómo te llamas?")
                        builder.setMessage("Debes de verificar tu identidad para activar tu cuenta.")
                        builder.setPositiveButton("Verificar"){
                                _,_ ->
                            this.viewModel.inputs.completePersonalInfoButtonPressed()
                        }
                        val dialog = builder.create()
                        dialog.show()
                    }
                    else -> {

                    }
                }
            })

        compositeDisposable.add(this.viewModel.outputs.goToCompletePersonalInfoScreen().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                ManagerNav.getInstance(this).initNav()
            })
        compositeDisposable.add(this.viewModel.outputs.addPaymentMethodAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                val intent = Intent(this,AddPaymentMethodActivity::class.java)
                startActivity(intent)
            })

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("SplashActivity", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result?.token
                this.viewModel.inputs.token(token?:"")
            })

        this.viewModel.inputs.onCreate()

    }

    private val navItemListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId){
            R.id.nav_pay -> {
                fm.beginTransaction().hide(active).show(Payfragment).commit()
                active = Payfragment
                toolbar.title = "Pagar"
                invalidateOptionsMenu()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_movements->{
                fm.beginTransaction().hide(active).show(Movementsfragment).commit()
                active = Movementsfragment
                toolbar.title = "Historial"
                invalidateOptionsMenu()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_catalog->{
                fm.beginTransaction().hide(active).show(Catalogfragment).commit()
                active = Catalogfragment
                toolbar.title = "Comercios"
                invalidateOptionsMenu()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_points->{
                fm.beginTransaction().hide(active).show(PointsFragment).commit()
                active = PointsFragment
                toolbar.title = "Puntos"
                invalidateOptionsMenu()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile->{
                fm.beginTransaction().hide(active).show(ProfileFragmentClass).commit()
                active = ProfileFragmentClass
                toolbar.title = "Cuenta"
                invalidateOptionsMenu()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            //super.onBackPressed()
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Cerrar Sesión")
            builder.setMessage("¿Desea salir de la aplicación?")
            builder.setPositiveButton("Aceptar"){
                    _,_ ->
                this.viewModel.inputs.signOutButtonPressed()
            }
            builder.setNegativeButton("Cancelar", null)
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuActionBar = menu
        // Inflate the menu; this adds items to the action bar if it is present.
        when(active){
            is PointsFragment -> {menuInflater.inflate(R.menu.toolbar_points_menu, menu)}
            is CatalogFragment -> menuInflater.inflate(R.menu.toolbar_sort_menu,menu)

            //else -> {menuInflater.inflate(R.menu.home, menu)}
        }
        //menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.filter_action -> {
                val frag = fm.findFragmentByTag("commerce_fragment") as CatalogFragment
                frag.filterAction()
                return true
            }
            R.id.action_settings -> return true
            R.id.points_action -> {
                this.viewModel.inputs.addPaymentButtonPressed()
                //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
                return true}
            android.R.id.home -> {
                val intent = Intent(this,NotificationsActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        val navigation = SharedPreferencesUtils.getBooleanInSp(this,"nav_to_mov")
        if (navigation){
            bottomNav.selectedItemId = R.id.nav_movements
            val settings = this.getSharedPreferences("SP", Activity.MODE_PRIVATE)
            settings.edit().remove("nav_to_mov").apply()
        }
    }

}
