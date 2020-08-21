package com.sungbin.autoreply.bot.three.utils.ui

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.ImageView
import com.sungbin.sungbintool.StorageUtils
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object ImageUtils {
    fun download(name: String, url: String, context: Context) {
        StorageUtils.createFolder("/Android/data/com.sungbin.autoreply.bot.three/chat/content/picture")
        File("${StorageUtils.sdcard}/Android/data/com.sungbin.autoreply.bot.three/chat/content/picture/.nomedia")
            .createNewFile()
        val path =
            getDownloadFilePath(
                name
            )
        ImageDownloadTask(
            context
        ).execute(path, url)
    }

    fun checkDownloadFileExist(name: String): Boolean {
        return File(
            getDownloadFilePath(
                name
            )
        ).exists()
    }

    fun set(path: String, view: ImageView, context: Context) {
        var name = path.replaceLast("/", "*").split("*")[1]
        if (name.contains("?")) name = name.split("?")[0]
        val isDownloadedd =
            checkDownloadFileExist(
                name
            )
        if (isDownloadedd) {
            Glide.set(
                context,
                getDownloadFilePath(
                    name
                ), view
            )
        } else {
            Glide.set(context, path, view)
            download(
                name,
                path,
                context
            )
        }
    }

    fun getDownloadFilePath(path: String): String {
        var name: String
        try {
            name = path.replaceLast("/", "*").split("*")[1]
            if (name.contains("?")) name = name.split("?")[0]
        } catch (e: Exception) {
            name = path
        }
        return StorageUtils.sdcard +
                "/Android/data/com.sungbin.autoreply.bot.three/chat/content/picture/$name"
    }

    private fun String.replaceLast(regex: String, replacement: String): String {
        val regexIndexOf = this.lastIndexOf(regex)
        return if (regexIndexOf == -1) this
        else {
            this.substring(0, regexIndexOf) + this.substring(regexIndexOf)
                .replace(regex, replacement)
        }
    }

    private class ImageDownloadTask constructor(context: Context) :
        AsyncTask<String?, Void?, Void?>() {

        private val ctx = context

        override fun doInBackground(vararg params: String?): Void? {
            try {
                val imageFile = File(params[0]!!)
                val imgUrl = URL(params[1])
                val conn = imgUrl.openConnection() as HttpURLConnection
                val len = conn.contentLength
                val tmpByte = ByteArray(len)
                val `is` = conn.inputStream
                val fos = FileOutputStream(imageFile)
                var read: Int

                while (true) {
                    read = `is`.read(tmpByte)
                    if (read <= 0) {
                        break
                    }
                    fos.write(tmpByte, 0, read)
                }

                `is`.close()
                fos.close()
                conn.disconnect()
            } catch (e: Exception) {
                Log.e("image error", e.toString())
                StorageUtils.createFolder("KTBH Error")
                StorageUtils.save("KTBH Error/${e.stackTrace[0]}.log", e.toString())
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            return
        }

    }
}