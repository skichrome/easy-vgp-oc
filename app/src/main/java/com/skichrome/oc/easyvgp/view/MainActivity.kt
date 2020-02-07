package com.skichrome.oc.easyvgp.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.util.RC_SIGN_IN_CODE
import com.skichrome.oc.easyvgp.util.errorLog
import com.skichrome.oc.easyvgp.util.snackBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
{
    // App icon credit : Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkIfUserIsAlreadyLoggedIn()
        val navController = getNavController()
        configureToolbar(navController)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN_CODE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                activityMainConstraintLayout.rootView?.snackBar(getString(R.string.frag_login_success))
                checkIfUserIsAlreadyLoggedIn()
            } else
            {
                val response = IdpResponse.fromResultIntent(data)
                errorLog("An error occurred when trying to login : ${response?.error?.cause?.localizedMessage}")
                activityMainConstraintLayout.rootView?.snackBar(getString(R.string.frag_login_error))
                finish()
            }
        }
    }

    // =================================
    //              Methods
    // =================================

    private fun getNavController(): NavController = findNavController(R.id.activityMainHostFragment)

    // --- UI --- //

    private fun configureToolbar(navController: NavController) = toolbar?.let {

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment))
        it.setupWithNavController(navController, appBarConfiguration)

        it.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId)
            {
                R.id.settingsFragment -> navController.navigate(R.id.action_global_settingsFragment)
                R.id.logoutItem ->
                {
                    AuthUI.getInstance().signOut(this)
                    finish()
                }
                else -> return@setOnMenuItemClickListener menuItem.onNavDestinationSelected(navController) || super.onOptionsItemSelected(menuItem)
            }
            return@setOnMenuItemClickListener true
        }
    } ?: Unit

    // --- Login configuration --- //

    private fun checkIfUserIsAlreadyLoggedIn()
    {
        if (FirebaseAuth.getInstance().currentUser == null)
            configureAppLogin()
    }

    private fun configureAppLogin()
    {
        val loginProviders = listOf(AuthUI.IdpConfig.EmailBuilder().build())
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(loginProviders)
                .setIsSmartLockEnabled(false)
                .build(),
            RC_SIGN_IN_CODE
        )
    }
}