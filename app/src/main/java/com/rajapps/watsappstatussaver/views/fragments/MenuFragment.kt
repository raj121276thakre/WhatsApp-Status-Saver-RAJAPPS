package com.rajapps.watsappstatussaver.views.fragments

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.rajapps.watsappstatussaver.R

import com.rajapps.watsappstatussaver.databinding.FragmentMenuBinding
import com.rajapps.watsappstatussaver.views.activities.AppSettings


class MenuFragment : Fragment() {

    private lateinit var mContext: Context
    private var binding: FragmentMenuBinding? = null

    private var adView: LinearLayout? = null  // banner ads


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMenuBinding.inflate(layoutInflater, container, false)

        loadBannerAd() // banner ad


        return binding!!.root
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        binding!!.drawerPremiumItem.setOnClickListener {
            showDownloadsDialog()
        }


        binding!!.drawerShareItem.setOnClickListener {
            shareApp()
        }
        binding!!.drawerPrivacyItem.setOnClickListener {
            privacyPolicyLink()
        }
        binding!!.drawerAboutItem.setOnClickListener {
            showAboutDialog()
        }
        binding!!.drawerRateItem.setOnClickListener {

            rateUs()
        }
        binding!!.drawerFaqItem.setOnClickListener {

            moreApps()

        }
        super.onViewCreated(view, savedInstanceState)
    }




    private fun moreApps() {
        val developerPageUrl = "https://play.google.com/store/apps/dev?id=7691527306445378965" // Replace with your developer's name or package name

        val uri = Uri.parse(developerPageUrl)

        val intent = Intent(Intent.ACTION_VIEW, uri)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Handle the case where no activity can handle the intent (e.g., Play Store not installed)
        }
    }



    private fun rateUs() {
        val packageName =  requireContext().packageName

        val uri = Uri.parse("market://details?id=$packageName")

        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // If the Play Store app is not installed on the device,
            // you can open the Play Store in a web browser
            val webUri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            val webIntent = Intent(Intent.ACTION_VIEW, webUri)
            startActivity(webIntent)
        }
    }

    private fun showDownloadsDialog() {
        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(com.rajapps.watsappstatussaver.R.layout.dialog_custom)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        (dialog.findViewById<View>(com.rajapps.watsappstatussaver.R.id.bt_close) as RelativeLayout).setOnClickListener { dialog.dismiss() }
        dialog.show()
        dialog.window!!.attributes = lp
    }



    private fun showAboutDialog() {
        val dialog = Dialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(com.rajapps.watsappstatussaver.R.layout.dialog_about)
        dialog.setCancelable(true)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        (dialog.findViewById<View>(com.rajapps.watsappstatussaver.R.id.bt_close) as RelativeLayout).setOnClickListener { dialog.dismiss() }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun privacyPolicyLink() {
        val privacyPolicyUrl = resources.getString(com.rajapps.watsappstatussaver.R.string.privacy_policy_link) // Replace with the actual URL of your privacy policy

        val uri = Uri.parse(privacyPolicyUrl)

        val intent = Intent(Intent.ACTION_VIEW, uri)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Handle the case where no activity can handle the intent (e.g., no web browser installed)
        }
    }


    fun shareApp() {
        val packageName =   requireContext().packageName   //"com.rajapps.apps.studio.ctvpn" // This will automatically get the package name of your app
        val shareMessage = getString(com.rajapps.watsappstatussaver.R.string.share_msg)
        val appLink = "https://play.google.com/store/apps/details?id=$packageName"

        val shareIntent = ShareCompat.IntentBuilder.from(requireActivity())
            .setType("text/plain")
            .setSubject(getString(com.rajapps.watsappstatussaver.R.string.app_name))
            .setText("$shareMessage\n$appLink")
            .createChooserIntent()

        startActivity(shareIntent)
    }






    private fun loadBannerAd() {
        if (!AppSettings.isUserPaid) {
            binding!!.menuBannerBlock.visibility = View.VISIBLE
            binding!!.menuBannerContainerAdmob.visibility = View.GONE


            if (AppSettings.enableAdmobAds) {
                binding!!.menuBannerContainerAdmob.visibility = View.VISIBLE
                loadAdmobBannerAd()

            }
        } else {
            binding!!.menuBannerBlock.visibility = View.GONE
        }
    }

    private fun loadAdmobBannerAd() {
        val adview = binding!!.menuBannerContainerAdmob
        val adRequest = AdRequest.Builder().build()
        adview.loadAd(adRequest)
    }





}