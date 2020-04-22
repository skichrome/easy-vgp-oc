package com.skichrome.oc.easyvgp.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.skichrome.oc.easyvgp.EasyVGPApplication
import com.skichrome.oc.easyvgp.R
import com.skichrome.oc.easyvgp.model.local.database.Company
import com.skichrome.oc.easyvgp.model.local.database.User
import com.skichrome.oc.easyvgp.model.local.database.UserAndCompany
import com.skichrome.oc.easyvgp.util.*
import com.skichrome.oc.easyvgp.viewmodel.HomeViewModel
import com.skichrome.oc.easyvgp.viewmodel.vmfactory.HomeViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity()
{
    // App icon credit : Icons made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>

    private val viewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory((application as EasyVGPApplication).homeRepository)
    }

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
            }
            else
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
        {
            configureAppLogin()
        }
        else
        {
            val userUid = FirebaseAuth.getInstance().currentUser!!.uid
            viewModel.getCurrentFirebaseUser(userUid)

            viewModel.currentUserId.observe(this, EventObserver {
                val userName = FirebaseAuth.getInstance().currentUser?.displayName
                val userEmail = FirebaseAuth.getInstance().currentUser?.email

                if (userName != null && userEmail != null)
                {
                    if (it == -1L)
                    {
                        val company = Company(id = 0L, name = "", siret = "", localCompanyLogo = null, remoteCompanyLogo = null)
                        val user = User(
                            id = 0L,
                            firebaseUid = userUid,
                            name = userName,
                            email = userEmail,
                            approval = null,
                            vatNumber = null,
                            companyId = company.id
                        )
                        viewModel.saveNewUserAndCompany(UserAndCompany(company = company, user = user))
                    }
                    else
                    {
                        PreferenceManager.getDefaultSharedPreferences(this)
                            .edit()
                            .putLong(CURRENT_LOCAL_PROFILE, it)
                            .apply()
                    }
                    viewModel.synchronizeLocalDatabaseWithRemote()
                }
            })
            viewModel.message.observe(this, EventObserver { toast(getString(it)) })
        }
    }

    private fun configureAppLogin()
    {
        val loginProviders = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setTheme(R.style.EasyVGPTheme_LoginTheme)
                .setAvailableProviders(loginProviders)
                .setIsSmartLockEnabled(false)
                .build(),
            RC_SIGN_IN_CODE
        )
    }
}