package com.example.agendapersonal

import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    //instanciamos un objeto de tipo fragment
   private var nuevoFragment: androidx.fragment.app.Fragment?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)



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
              Calendario()
           }
           R.id.nav_Acerca->{
               DefaultFragment()
           }
        else->{
            DefaultFragment()
        }
    }
        supportFragmentManager.beginTransaction().replace(R.id.cmPrincipal,nuevoFragment!!).commit()
    }
}
