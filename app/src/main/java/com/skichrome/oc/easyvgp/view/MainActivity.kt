package com.skichrome.oc.easyvgp.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.util.FRAGMENTS_INT_ARGUMENTS
import com.skichrome.oc.easyvgp.util.RC_SIGN_IN_CODE
import com.skichrome.oc.easyvgp.util.errorLog
import com.skichrome.oc.easyvgp.util.toast
import com.skichrome.oc.easyvgp.view.base.FragmentNavigation
import com.skichrome.oc.easyvgp.view.fragments.CustomerFragment
import com.skichrome.oc.easyvgp.view.fragments.HomeFragment
import com.skichrome.oc.easyvgp.view.fragments.LoginFragment
import com.skichrome.oc.easyvgp.view.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), HomeFragment.HomeFragmentListener
{
    // App icon credit : Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>

    // =================================
    //              Fields
    // =================================

    private var loginFragment: LoginFragment? = null
    private var homeFragment: HomeFragment? = null
    private var customerFragment: CustomerFragment? = null
    private var settingsFragment: SettingsFragment? = null

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureToolbar()
    }

    override fun onResume()
    {
        super.onResume()
        getStartDestinationAccordingToUserLoggedInOrNot()
    }

    override fun onBackPressed()
    {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount == 0)
            finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN_CODE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                toast("Successfully logged in")
                configureHomeFragment()
            } else
            {
                val response = IdpResponse.fromResultIntent(data)
                errorLog("An error occurred when trying to login : ${response?.error?.message}")
                loginFragment?.apply { arguments = Bundle().apply { putInt(FRAGMENTS_INT_ARGUMENTS, R.string.frag_login_btn_retry) } }
                configureLoginFragment()
            }
        }
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
                R.id.logoutItem ->
                {
                    AuthUI.getInstance().signOut(this)
                    finish()
                }
                else -> return@setOnMenuItemClickListener super.onOptionsItemSelected(menuItem)
            }
            return@setOnMenuItemClickListener true
        }
    } ?: Unit

    // --- Fragments --- //

    private fun showFragment(fragment: Fragment)
    {
        if (!fragment.isVisible)
            supportFragmentManager.beginTransaction()
                .replace(R.id.activityMainFrameLayout, fragment)
                .addToBackStack(null)
                .commit()
    }

    private fun configureLoginFragment() = showFragment(loginFragment ?: LoginFragment.newInstance().also { loginFragment = it })
    private fun configureHomeFragment() = showFragment(homeFragment ?: HomeFragment.newInstance().also { homeFragment = it })
    private fun configureCustomerFragment() = showFragment(customerFragment ?: CustomerFragment.newInstance().also { customerFragment = it })
    private fun configureSettingFragment() = showFragment(settingsFragment ?: SettingsFragment.newInstance().also { settingsFragment = it })

    private fun getFragmentDestination(destination: FragmentNavigation)
    {
        return when (destination)
        {
            FragmentNavigation.LOGIN -> configureLoginFragment()
            FragmentNavigation.CUSTOMERS -> configureCustomerFragment()
            FragmentNavigation.SETTINGS -> configureSettingFragment()
            else -> getStartDestinationAccordingToUserLoggedInOrNot()
        }
    }

    private fun getStartDestinationAccordingToUserLoggedInOrNot()
    {
        if (FirebaseAuth.getInstance().currentUser != null)
        {
            supportFragmentManager.popBackStack()
            configureHomeFragment()
        } else
            configureLoginFragment()
    }

    // =================================
    //            Callbacks
    // =================================

    override fun onNavigationRequested(destination: FragmentNavigation) = getFragmentDestination(destination)
}