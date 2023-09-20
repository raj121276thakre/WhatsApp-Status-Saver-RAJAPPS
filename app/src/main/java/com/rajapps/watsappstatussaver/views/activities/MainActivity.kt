package com.rajapps.watsappstatussaver.views.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.rajapps.watsappstatussaver.R
import com.rajapps.watsappstatussaver.databinding.ActivityMainBinding
import com.rajapps.watsappstatussaver.utils.Constants
import com.rajapps.watsappstatussaver.utils.SharedPrefUtils
import com.rajapps.watsappstatussaver.utils.replaceFragment
import com.rajapps.watsappstatussaver.utils.slideFromStart
import com.rajapps.watsappstatussaver.utils.slideToEndWithFadeOut
import com.rajapps.watsappstatussaver.views.fragments.FragmentStatus
import com.rajapps.watsappstatussaver.views.fragments.MenuFragment
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder

class MainActivity : AppCompatActivity() {
    private val activity = this
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var slidingRootNav: SlidingRootNav? = null // sliding drawer
    private lateinit var appUpdateManager: AppUpdateManager                    // in app update

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        SharedPrefUtils.init(activity)
        binding.apply {


            splashLogic()
            val fragmentWhatsAppStatus = FragmentStatus()
            val bundle = Bundle()
            bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_MAIN)
            replaceFragment(fragmentWhatsAppStatus, bundle)

            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_status -> {
                        // whatsapp status

                        val fragmentWhatsAppStatus = FragmentStatus()
                        val bundle = Bundle()
                        bundle.putString(Constants.FRAGMENT_TYPE_KEY, Constants.TYPE_WHATSAPP_MAIN)
                        replaceFragment(fragmentWhatsAppStatus, bundle)
                    }

                    R.id.menu_business_status -> {
                        // whatsapp business status

                        val fragmentWhatsAppStatus = FragmentStatus()
                        val bundle = Bundle()
                        bundle.putString(
                            Constants.FRAGMENT_TYPE_KEY,
                            Constants.TYPE_WHATSAPP_BUSINESS
                        )
                        replaceFragment(fragmentWhatsAppStatus, bundle)
                    }

                    R.id.menu_settings -> {
                        // settings
                        //replaceFragment(FragmentSettings())

                        replaceFragment(MenuFragment())
                    }
                }

                return@setOnItemSelectedListener true
            }


            // sliding

            slidingRootNav = SlidingRootNavBuilder(this@MainActivity)
                .withToolbarMenuToggle(binding.toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(true)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject()


            appUpdateManager = AppUpdateManagerFactory.create(this@MainActivity)  //in app update
            checkUpdates()         //in app update


        }
    }


    //in app update
    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ){result:androidx.activity.result.ActivityResult->
        if (result.resultCode!= RESULT_OK){
            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
        }

    }


    //in app update
    private fun checkUpdates(){
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                // Request the update.
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE)
                        .build()
                )

            }
        }

        appUpdateManager.registerListener(listener)


    }


    //in app update
    private val listener = InstallStateUpdatedListener{state->
        // notify the user to complete the update.
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            popupSnackbarForCompleteUpdate()
        }

    }

    //in app update
    fun popupSnackbarForCompleteUpdate() {
        Snackbar.make(
            findViewById(R.id.activity_main_layout),
            "An update has just been downloaded.",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            setAction("Install") { appUpdateManager.completeUpdate() }
            setActionTextColor(resources.getColor(R.color.snackbar_action_text_color))
            show()
        }
    }


    //in app update
    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.unregisterListener(listener)
    }

    //in app update
    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate()
                }
        }
    }


    private fun splashLogic() {
        binding.apply {
            splashLayout.cardView.slideFromStart()
            Handler(Looper.myLooper()!!).postDelayed({
                splashScreenHolder.slideToEndWithFadeOut()
                splashScreenHolder.visibility = View.GONE
            }, 2000)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager?.findFragmentById(R.id.fragment_container)
        fragment?.onActivityResult(requestCode, resultCode, data)
    }


}









