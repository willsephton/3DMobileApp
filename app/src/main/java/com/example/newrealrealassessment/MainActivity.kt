package com.example.newrealrealassessment

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.mapFragmentContainer, MapFrag())
                .commit()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RecycleFrag())
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        when(item.itemId) {
            R.id.menu1 -> {
                /*
                val intent = Intent(this,MapChooseActivity::class.java)
                mapChooseLauncher.launch(intent)
                return true */

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val db = PointsOfInterestDatabase.getDatabase(application)
                        db.PointsOfInterestDAO().deleteAllPoints()
                    }
                    Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                }
            }
            R.id.menu2 -> {
                /*
                val intent = Intent(this,MyPrefsActivity::class.java)
                startActivity(intent)
                return true */
                Toast.makeText(getApplicationContext(), "menu2", Toast.LENGTH_SHORT).show();
            }


        }
        return false
    }
}
