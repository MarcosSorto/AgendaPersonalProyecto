package com.example.agendapersonal

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //instanciamos un objeto de tipo fragment
   private var nuevoFragment:Fragment?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }



    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        desplegarFragmen(item.itemId)

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun desplegarFragmen(id:Int){
       nuevoFragment=  when(id){
           R.id.nav_Perfil->{
               CrearPerfil()
           }
            R.id.nav_Crear->{
                CrearEventos()
            }
           R.id.nav_Editar->{
               EditarEventos()
           }
           R.id.nav_Calendario->{
               CrearPerfil()
           }
           R.id.nav_Acerca->{
               CrearPerfil()
           }
           R.id.nav_Salir->{
               CrearPerfil()
           }
        else->{
            DefaultFragment()
        }
    }
        supportFragmentManager.beginTransaction().replace(R.id.cmPrincipal,nuevoFragment!!).commit()
    }
}
