package com.rajapps.watsappstatussaver.views.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rajapps.watsappstatussaver.R
import com.rajapps.watsappstatussaver.databinding.ItemImagePreviewBinding
import com.rajapps.watsappstatussaver.models.MEDIA_TYPE_IMAGE
import com.rajapps.watsappstatussaver.models.MEDIA_TYPE_VIDEO
import com.rajapps.watsappstatussaver.models.MediaModel
import com.rajapps.watsappstatussaver.utils.saveStatus
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd

import java.io.File

class ImagePreviewAdapter(val list: ArrayList<MediaModel>, val context: Context) :
    RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: ItemImagePreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mediaModel: MediaModel) {
            binding.apply {
                Glide.with(context)
                    .load(mediaModel.pathUri.toUri())
                    .into(zoomableImageView)

                val downloadImage = if (mediaModel.isDownloaded) {
                    R.drawable.ic_downloaded
                } else {
                    R.drawable.ic_download
                }
                tools.statusDownload.setImageResource(downloadImage)



                tools.download.setOnClickListener {
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


//
//                tools.share.setOnClickListener {
//                    val mediaUri = mediaModel.pathUri.toUri()
//                    val mimeType = context.contentResolver.getType(mediaUri)
//
//                    val shareIntent = Intent(Intent.ACTION_SEND)
//                    shareIntent.type = mimeType
//                    shareIntent.putExtra(Intent.EXTRA_STREAM, mediaUri)
//
//                    // Use a chooser to allow the user to pick how to share the content
//                    val chooser = Intent.createChooser(shareIntent, "Share via...")
//
//                    try {
//                        context.startActivity(chooser)
//                    } catch (e: Exception) {
//                        Toast.makeText(context, "Unable to share", Toast.LENGTH_SHORT).show()
//                        e.printStackTrace()
//                    }
//                }


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





                tools.repost.setOnClickListener {
                    val currentItemPosition = adapterPosition
                    if (currentItemPosition != RecyclerView.NO_POSITION) {
                        val mediaModel = list[currentItemPosition]

                        val mediaUri = mediaModel.pathUri.toUri()

                        // Check if it's an image or video
                        if (mediaModel.type == MEDIA_TYPE_IMAGE) {
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
    }


    // Function to load the interstitial ad


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImagePreviewAdapter.ViewHolder {
        return ViewHolder(
            ItemImagePreviewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ImagePreviewAdapter.ViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount() = list.size

}











