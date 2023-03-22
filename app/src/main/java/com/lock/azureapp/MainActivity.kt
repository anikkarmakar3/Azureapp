package com.lock.azureapp

import android.R.attr
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lock.azureapp.databinding.ActivityMainBinding
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val SELECT_PICTURE = 200
    val FILE_SELECT_CODE = 2
    var arraylist = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        val view = binding.root
        setContentView(view)

        binding.fileName.visibility = View.GONE


        binding.pic.setOnClickListener {
            imageChooser()
        }

        binding.download.setOnClickListener {
            val path: String = Environment.getExternalStorageDirectory().toString();
            val uri: Uri = Uri.parse(path);
            val intent = Intent(Intent.ACTION_GET_CONTENT);

            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.putExtra("CONTENT_TYPE", "*/*");

            intent.setDataAndType(uri, "*/*");
            /*startActivity(intent);*/
            startActivityForResult(
                Intent.createChooser(intent, "Select a File to Upload"),
                FILE_SELECT_CODE
            );
            /*startActivityForResult(intent,2)*/
        }
    }

    fun imageChooser() {

        // create an instance of the
        // intent of the type image
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode === -1) {

            when (requestCode) {
                SELECT_PICTURE -> {
                    val selectedImageUri: Uri = data!!.data!!
                    if (null != selectedImageUri) {
                        // update the preview image in the layout
                        binding.IVPreviewImage.setImageURI(selectedImageUri)
                    }
                }
                FILE_SELECT_CODE -> {
                    if (null != data) {
                        val newClipData = data.clipData
                        if (newClipData != null) {
                            for (i in 0 until newClipData.itemCount) {
                                val uri = newClipData.getItemAt(i).uri
                                val uriString = uri.toString()
                                val myFile = File(uriString)
                                val path: String = myFile.getAbsolutePath()
                                var displayName: String? = null

                                if (uriString.startsWith("content://")) {
                                    var cursor: Cursor? = null
                                    try {
                                        cursor = applicationContext.getContentResolver()
                                            .query(uri, null, null, null, null)
                                        if (cursor != null && cursor.moveToFirst()) {
                                            displayName =
                                                cursor.getString(
                                                    cursor.getColumnIndex(
                                                        OpenableColumns.DISPLAY_NAME
                                                    )
                                                )
                                            arraylist.addAll(listOf(displayName))
                                            /*binding.fileName.text = displayName*/
                                        }
                                    } finally {
                                        cursor?.close()
                                    }
                                } else if (uriString.startsWith("file://")) {
                                    displayName = myFile.getName()
                                    binding.fileName.visibility = View.VISIBLE
                                    binding.fileName.text = displayName
                                }
                            }
                            binding.fileName.visibility = View.VISIBLE
                            binding.fileName.text= arraylist.toString()
//                                Log.d("filesUri [" + uri + "] : ", uri.toString() );
                        }
                    } else {
                        val uri = data?.data
                        arraylist.addAll(listOf(uri.toString()))
                        Log.d("filesUri [" + uri + "] : ", uri.toString());
                    }
                }
                /*val uri: Uri = data?.data!!
                    val uriString = uri.toString()
                    val myFile = File(uriString)
                    val path: String = myFile.getAbsolutePath()
                    var displayName: String? = null

                    if (uriString.startsWith("content://")) {
                        var cursor: Cursor? = null
                        try {
                            cursor = applicationContext.getContentResolver()
                                .query(uri, null, null, null, null)
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName =
                                    cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                binding.fileName.text = displayName
                            }
                        } finally {
                            cursor?.close()
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName()
                        binding.fileName.text = displayName
                    }
                }
            }*/

                // compare the resultCode with the
                // SELECT_PICTURE constant

            }
        }
    }

}