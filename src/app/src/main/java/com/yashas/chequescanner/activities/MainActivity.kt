package com.yashas.chequescanner.activities

import android.Manifest
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.yashas.chequescanner.fragmets.HomeFragment
import com.yashas.chequescanner.R
import com.yashas.chequescanner.fragmets.HistoryFragment
import com.yashas.chequescanner.fragmets.ScanDetailsFragment
import com.yashas.chequescanner.fragmets.ScanFragment

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var frameLayout: FrameLayout
    private lateinit var navigationView: NavigationView
    private var previousMenuItemBottomNav: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setup()
    }

    private fun setup(){
        setupUI()
        actionBarToggle()
        setUpToolbar()
        setupHome()
        listeners()
        askPermission()
    }

    private fun askPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        }
    }

    private fun listeners(){
        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItemBottomNav != null) {
                it.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItemBottomNav = it

            when(it.itemId){
                R.id.home ->{
                    setupHome()
                    drawerLayout.closeDrawers()
                }

                R.id.scan ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, ScanFragment())
                        .commit()
                    supportActionBar?.title = getString(R.string.scan_cheque)
                    drawerLayout.closeDrawers()
                }

                R.id.history->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, HistoryFragment())
                        .commit()
                    supportActionBar?.title = getString(R.string.history)
                    drawerLayout.closeDrawers()
                }

                R.id.exit ->{
                    finishAffinity()
                }
            }

            return@setNavigationItemSelectedListener true
        }
    }

    private fun setupUI(){
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun setupHome() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, HomeFragment())
            .commit()
        supportActionBar?.title = getString(R.string.home)
        navigationView.menu.findItem(R.id.home).isChecked = true
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.elevation = 0f
        supportActionBar?.title = getString(R.string.home)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    private fun actionBarToggle() {
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        actionBarDrawerToggle.drawerArrowDrawable.color = ResourcesCompat.getColor(
            resources,
            R.color.text_color, null
        )
        actionBarDrawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                drawerLayout.closeDrawers()
            }
            else -> {
                when (supportFragmentManager.findFragmentById(R.id.frame)) {
                    is ScanDetailsFragment ->{
                        val sp = getSharedPreferences("saved", Context.MODE_PRIVATE)
                        val check = sp.getBoolean("saved", true)
                        if(check){
                            sp.edit().putBoolean("saved", false).apply()
                            setupHome()
                        }else{
                            MaterialAlertDialogBuilder(this)
                                .setTitle("Alert")
                                .setMessage("Exit without saving?")
                                .setPositiveButton("Yes"){_,_->
                                    setupHome()
                                }
                                .setNeutralButton("No"){_,_->}
                                .show()
                        }
                    }

                    is HomeFragment ->{
                        MaterialAlertDialogBuilder(this)
                            .setTitle("Exit")
                            .setMessage("Sure you want to Exit?")
                            .setPositiveButton("Yes"){_,_->
                                finishAffinity()
                            }
                            .setNeutralButton("No"){_,_->}
                            .show()
                    }

                    !is HomeFragment -> {
                        setupHome()
                    }
                    else -> super.onBackPressed()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if(grantResults.isNotEmpty()&&grantResults[0]!=PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "You have disabled camera functions.", Toast.LENGTH_LONG).show()
            }
        }
    }
}