package com.skichrome.oc.easyvgp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.view.base.BaseFragment
import com.skichrome.oc.easyvgp.view.fragments.HomeFragment
import com.skichrome.oc.easyvgp.view.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
{
    // App icon credit : Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>

    // =================================
    //              Fields
    // =================================

    private var homeFragment: BaseFragment? = null
    private var settingsFragment: SettingsFragment? = null

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureToolbar()
        configureHomeFragment()
    }

    override fun onBackPressed()
    {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0)
            finish()
    }

    // =================================
    //              Methods
    // =================================

    // --- UI --- //

    private fun configureToolbar() = toolbar?.let {
        it.setTitle(R.string.app_name)

        it.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId)
            {
                R.id.settingsFragment -> configureSettingFragment()
                else -> return@setOnMenuItemClickListener super.onOptionsItemSelected(menuItem)
            }
            return@setOnMenuItemClickListener true
        }
    } ?: Unit

    // --- Fragments --- //

    private fun showFragment(fragment: Fragment) = supportFragmentManager.beginTransaction()
        .replace(R.id.activityMainFrameLayout, fragment)
        .addToBackStack("MainActivity")
        .commit()

    private fun configureHomeFragment() = showFragment(homeFragment ?: HomeFragment.newInstance().also { homeFragment = it })
    private fun configureSettingFragment() = showFragment(settingsFragment ?: SettingsFragment.newInstance().also { settingsFragment = it })
}