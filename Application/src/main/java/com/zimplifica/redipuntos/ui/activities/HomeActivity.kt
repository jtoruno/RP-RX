package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.fragment.app.Fragment
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.zimplifica.redipuntos.R
import com.zimplifica.redipuntos.libs.qualifiers.BaseActivity
import com.zimplifica.redipuntos.libs.qualifiers.RequiresActivityViewModel
import com.zimplifica.redipuntos.ui.fragments.CatalogFragment
import com.zimplifica.redipuntos.ui.fragments.MovementsFragment
import com.zimplifica.redipuntos.ui.fragments.PayFragment
import com.zimplifica.redipuntos.viewModels.HomeViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import android.view.View
import android.widget.Toast
import com.zimplifica.redipuntos.extensions.capitalizeWords
import com.zimplifica.redipuntos.libs.utils.SharedPreferencesUtils
import com.zimplifica.redipuntos.models.ManagerNav
import com.zimplifica.redipuntos.ui.fragments.PointsFragment
import io.reactivex.disposables.CompositeDisposable

import kotlinx.android.synthetic.main.nav_header_home.view.*


@RequiresActivityViewModel(HomeViewModel.ViewModel::class)
class HomeActivity : BaseActivity<HomeViewModel.ViewModel>(), NavigationView.OnNavigationItemSelectedListener {
    private val compositeDisposable = CompositeDisposable()
    lateinit var Catalogfragment : androidx.fragment.app.Fragment
    lateinit var Payfragment : androidx.fragment.app.Fragment
    lateinit var Movementsfragment : androidx.fragment.app.Fragment
    lateinit var PointsFragment : androidx.fragment.app.Fragment
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
        Catalogfragment = CatalogFragment()
        Payfragment = PayFragment()
        PointsFragment = PointsFragment()
        Movementsfragment = MovementsFragment()
        active = Payfragment

        fm.beginTransaction().add(R.id.home_frame_layout,Catalogfragment,"commerce_fragment").hide(Catalogfragment).commit()
        fm.beginTransaction().add(R.id.home_frame_layout,Payfragment, "pay").commit()
        fm.beginTransaction().add(R.id.home_frame_layout,PointsFragment, "points").hide(PointsFragment).commit()
        fm.beginTransaction().add(R.id.home_frame_layout,Movementsfragment,"movements").hide(Movementsfragment).commit()

        bottomNav = findViewById(R.id.home_nav_bottom)
        bottomNav.setOnNavigationItemSelectedListener(navItemListener)
        bottomNav.selectedItemId = R.id.nav_pay

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val header = navigationView.getHeaderView(0)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        compositeDisposable.add(this.viewModel.outputs.signOutAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            })

        home_log_out.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
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
        home_about_us.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            val intent = Intent(this,AboutActivity::class.java)
            startActivity(intent)
        }
        home_terms_and_conditions.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            val intent = Intent(this, TermsActivity::class.java)
            startActivity(intent)
        }
        home_change_password.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
        }
        home_privacy.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            val intent = Intent(this, PrivacyActivity::class.java)
            startActivity(intent)
        }
        home_account_info.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
            val intent = Intent(this,AccountInfoActivity::class.java)
            startActivity(intent)
        }
        compositeDisposable.add(this.viewModel.outputs.accountInformationResult().observeOn(AndroidSchedulers.mainThread())
            .subscribe{
                val name1 = (it.userFirstName?:"").toLowerCase()
                val name2 = (it.userLastName?:"").toLowerCase()
                val completeName = ("$name1 $name2").capitalizeWords()
                if(completeName.isEmpty() || completeName == " "){
                    header.home_header_name.text = "Usuario Invitado"
                }else{
                    header.home_header_name.text = completeName
                }
                header.home_header_points.text = "₡ "+String.format("%,.2f", it.rewards?:0.0) +" RediPuntos"
            })


        compositeDisposable.add(this.viewModel.outputs.showCompletePersonalInfoAlert().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.e("Home","showCompletePersonalInfoAlert")
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Completar Información Personal")
                builder.setMessage("RediPuntos requiere saber un poco mas de ti, ¿deseas completar tu información?")
                builder.setPositiveButton("Completar Información"){
                    _,_ ->
                    this.viewModel.inputs.completePersonalInfoButtonPressed()
                }
                builder.setNegativeButton("Luego", null)
                val dialog = builder.create()
                dialog.show()
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
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_sign_out -> {
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

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
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
