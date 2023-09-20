package com.rajapps.watsappstatussaver.views.adapters

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.rajapps.watsappstatussaver.R
import com.rajapps.watsappstatussaver.databinding.ItemVideoPreviewBinding
import com.rajapps.watsappstatussaver.models.MEDIA_TYPE_IMAGE
import com.rajapps.watsappstatussaver.models.MEDIA_TYPE_VIDEO
import com.rajapps.watsappstatussaver.models.MediaModel
import com.rajapps.watsappstatussaver.utils.saveStatus
import java.io.File

class VideoPreviewAdapter(val list: ArrayList<MediaModel>, val context: Context) :
    RecyclerView.Adapter<VideoPreviewAdapter.ViewHolder>() {

    private var mInterstitialAd: InterstitialAd? = null //inter

    init {
        load_ads()  //interstetial
    }

    inner class ViewHolder(val binding: ItemVideoPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaModel: MediaModel) {
            binding.apply {

                val player = ExoPlayer.Builder(context).build()
                playerView.player = player
                val mediaItem = MediaItem.fromUri(mediaModel.pathUri)

                player.setMediaItem(mediaItem)

                player.prepare()


                val downloadImage = if (mediaModel.isDownloaded) {
                    R.drawable.ic_downloaded
                } else {
                    R.drawable.ic_download
                }
                tools.statusDownload.setImageResource(downloadImage)




                tools.share.setOnClickListener {
                    val mediaUri = mediaModel.pathUri.toUri()
                    val mimeType = context.contentResolver.getType(mediaUri)

                    // Create a message with your app's Play Store link
                    val packageName = context.packageName
                    val appLink =
                        "Get my awesome app on Play Store: https://play.google.com/store/apps/details?id=$packageName"


                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = mimeType
                    shareIntent.putExtra(Intent.EXTRA_STREAM, mediaUri)
                    shareIntent.putExtra(Intent.EXTRA_TEXT, appLink) // Add your app's link as text

                    // Use a chooser to allow the user to pick how to share the content
                    val chooser = Intent.createChooser(shareIntent, "Share via...")

                    try {
                        context.startActivity(chooser)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Unable to share", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }



                tools.download.setOnClickListener {
                    display_ads(it) //interstetial ad
                    val isDownloaded = context.saveStatus(mediaModel)
                    if (isDownloaded) {
                        // status is downloaded
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                        mediaModel.isDownloaded = true
                        tools.statusDownload.setImageResource(R.drawable.ic_downloaded)
                    } else {
                        // unable to download status
                        Toast.makeText(context, "Unable to Save", Toast.LENGTH_SHORT).show()
                    }
                }



                tools.repost.setOnClickListener {
                    val currentItemPosition = adapterPosition
                    if (currentItemPosition != RecyclerView.NO_POSITION) {
                        val mediaModel = list[currentItemPosition]

                        val mediaUri = mediaModel.pathUri.toUri()

                        // Check if it's an image or video
                        if (mediaModel.type == MEDIA_TYPE_IMAGE) {

                            // Show the interstitial ad
                            display_ads(it)

                            // Reposting an image
                            val shareImageIntent = Intent(Intent.ACTION_SEND)
                            shareImageIntent.type = "image/*"
                            shareImageIntent.putExtra(Intent.EXTRA_STREAM, mediaUri)
                            shareImageIntent.setPackage("com.whatsapp") // Specify WhatsApp

                            try {
                                context.startActivity(
                                    Intent.createChooser(
                                        shareImageIntent,
                                        "Repost Image to WhatsApp"
                                    )
                                )
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Unable to repost image",
                                    Toast.LENGTH_SHORT
                                ).show()
                                e.printStackTrace()
                            }
                        } else if (mediaModel.type == MEDIA_TYPE_VIDEO) {

                            // Show the interstitial ad
                            display_ads(it)

                            // Reposting a video
                            val shareVideoIntent = Intent(Intent.ACTION_SEND)
                            shareVideoIntent.type = "video/*"
                            shareVideoIntent.putExtra(Intent.EXTRA_STREAM, mediaUri)
                            shareVideoIntent.setPackage("com.whatsapp") // Specify WhatsApp

                            try {
                                context.startActivity(
                                    Intent.createChooser(
                                        shareVideoIntent,
                                        "Repost Video to WhatsApp"
                                    )
                                )
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Unable to repost video",
                                    Toast.LENGTH_SHORT
                                ).show()
                                e.printStackTrace()
                            }
                        }
                    }
                }


            }
        }

        fun stopPlayer() {
            binding.playerView.player?.stop()
        }
    }


    // Function to load the interstitial ad

    fun load_ads() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            context.getString(R.string.admob_interstitial), // Replace with your interstitial ad unit ID
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle ad loading failure
                    adError.message?.let { errorMessage ->
                        Log.e(ContentValues.TAG, "Interstitial ad failed to load: $errorMessage")
                    }
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // Ad has been successfully loaded
                    Log.d(ContentValues.TAG, "Interstitial ad loaded.")
                    mInterstitialAd = interstitialAd
                }
            }
        )
    }


    fun display_ads(view: View) {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Called when the ad is dismissed
                    // You can proceed with reposting or any other action here
                    // Make sure to reload the ad for the next use
                    load_ads()
                }



                override fun onAdShowedFullScreenContent() {
                    // Called when the ad is shown
                    // This is a good place to disable any UI elements that should not be clickable
                }
            }
            mInterstitialAd?.show(context as Activity)
        } else {
            // The interstitial ad wasn't loaded yet, you can handle this case accordingly
            // For example, show a toast or proceed with reposting
            Toast.makeText(context, "Interstitial ad not loaded", Toast.LENGTH_SHORT).show()
            // ... (reposting logic)
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoPreviewAdapter.ViewHolder {
        return ViewHolder(
            ItemVideoPreviewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VideoPreviewAdapter.ViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount() = list.size

}











