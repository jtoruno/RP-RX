package com.zimplifica.redipuntos.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
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


@RequiresActivityViewModel(HomeViewModel.ViewModel::class)
class HomeActivity : BaseActivity<HomeViewModel.ViewModel>(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var Catalogfragment : Fragment
    lateinit var Payfragment : Fragment
    lateinit var Movementsfragment : Fragment
    private val fm = supportFragmentManager
    lateinit var active : Fragment
    private var menuActionBar : Menu ? = null

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        toolbar.title = "RediPuntos"
        setSupportActionBar(toolbar)
        Catalogfragment = CatalogFragment()
        Payfragment = PayFragment()
        Movementsfragment = MovementsFragment()
        active = Payfragment

        fm.beginTransaction().add(R.id.home_frame_layout,Catalogfragment, "catalog").hide(Catalogfragment).commit()
        fm.beginTransaction().add(R.id.home_frame_layout,Payfragment, "pay").commit()
        fm.beginTransaction().add(R.id.home_frame_layout,Movementsfragment,"movements").hide(Movementsfragment).commit()

        val bottomNav : BottomNavigationView = findViewById(R.id.home_nav_bottom)
        bottomNav.setOnNavigationItemSelectedListener(navItemListener)
        bottomNav.selectedItemId = R.id.nav_pay



        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        this.viewModel.outputs.signOutAction().observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

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
        home_payment_methods.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
        }
        home_change_password.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
        }
        home_account_info.setOnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                drawer_layout.closeDrawer(GravityCompat.START)
            }
        }

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
                toolbar.title = "Movimientos"
                invalidateOptionsMenu()
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_catalog->{
                fm.beginTransaction().hide(active).show(Catalogfragment).commit()
                active = Catalogfragment
                toolbar.title = "Catálogo"
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
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuActionBar = menu
        // Inflate the menu; this adds items to the action bar if it is present.
        when(active){
            is PayFragment -> {menuInflater.inflate(R.menu.toolbar_points_menu, menu)}
            else -> {menuInflater.inflate(R.menu.home, menu)}
        }
        //menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            R.id.points_action -> {
                Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()
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
}
