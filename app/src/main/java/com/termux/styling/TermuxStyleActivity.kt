package com.termux.styling

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.util.AtomicFile
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*

const val DEFAULT_FILENAME = "Default"
const val COLORS_FILENAME = "colors.properties"
const val FONT_FILENAME = "font.ttf"

fun capitalize(str: String): String {
    var lastWhitespace = true
    val chars = str.toCharArray()
    for (i in chars.indices) {
        if (Character.isLetter(chars[i])) {
            if (lastWhitespace) chars[i] = Character.toUpperCase(chars[i])
            lastWhitespace = false
        } else {
            lastWhitespace = Character.isWhitespace(chars[i])
        }
    }
    return String(chars)
}

class TermuxStyleActivity : Activity() {

    enum class ActivityResultCodes {
        ChooseColors,
        ChooseFont
    }

    internal open class Selectable(val fileName: String) {
        val displayName: String

        init {
            var name = fileName.replace('-', ' ')
            val dotIndex = name.lastIndexOf('.')
            if (dotIndex != -1) name = name.substring(0, dotIndex)

            this.displayName = capitalize(name)
        }

        override fun toString(): String {
            return displayName
        }
    }

    internal object ChooseFileSelectable : Selectable("Choose file…")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Avoid dim behind:
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        setContentView(R.layout.layout)

        val colorSpinner = findViewById<Button>(R.id.color_spinner)
        val fontSpinner = findViewById<Button>(R.id.font_spinner)

        val colorAdapter = ArrayAdapter<Selectable>(this, android.R.layout.simple_spinner_dropdown_item)

        colorSpinner.setOnClickListener {
            val dialog = AlertDialog.Builder(this@TermuxStyleActivity).setAdapter(colorAdapter) { _, which -> copyStyleFile(colorAdapter.getItem(which), true) }.create()
            dialog.setOnShowListener {
                val lv = dialog.listView
                lv.setOnItemLongClickListener { _, _, position, _ ->
                    showLicense(colorAdapter.getItem(position) as Selectable, true)
                    true
                }
            }
            dialog.show()
        }

        val fontAdapter = ArrayAdapter<Selectable>(this, android.R.layout.simple_spinner_dropdown_item)
        fontSpinner.setOnClickListener {
            val dialog = AlertDialog.Builder(this@TermuxStyleActivity).setAdapter(fontAdapter) { _, which -> copyStyleFile(fontAdapter.getItem(which), false) }.create()
            dialog.setOnShowListener {
                val lv = dialog.listView
                lv.setOnItemLongClickListener { _, _, position, _ ->
                    showLicense(fontAdapter.getItem(position) as Selectable, false)
                    true
                }
            }
            dialog.show()
        }

        val colorList = ArrayList<Selectable>()
        val fontList = ArrayList<Selectable>()
        for (assetType in arrayOf("colors", "fonts")) {
            val isColors = assetType == "colors"
            val assetsFileExtension = if (isColors) ".properties" else ".ttf"
            val currentList = if (isColors) colorList else fontList

            currentList.add(Selectable(if (isColors) DEFAULT_FILENAME else DEFAULT_FILENAME))

            try {
                assets.list(assetType)!!
                        .filter { it.endsWith(assetsFileExtension) }
                        .forEach { currentList.add(Selectable(it)) }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

        }

        colorAdapter.add(ChooseFileSelectable)
        colorAdapter.addAll(colorList)
        fontAdapter.add(ChooseFileSelectable)
        fontAdapter.addAll(fontList)
    }

    private fun showLicense(mCurrentSelectable: Selectable, colors: Boolean) {
        try {
            val assetsFolder = if (colors) "colors" else "fonts"
            var fileName = mCurrentSelectable.fileName
            val dotIndex = fileName.lastIndexOf('.')
            if (dotIndex != -1) fileName = fileName.substring(0, dotIndex)
            fileName += ".txt"

            assets.open("$assetsFolder/$fileName").use { `in` ->
                val buffer = ByteArray(`in`.available())
                `in`.read(buffer)
                val license = SpannableString(String(buffer))
                Linkify.addLinks(license, Linkify.ALL)
                val dialog = AlertDialog.Builder(this)
                        .setTitle(mCurrentSelectable.displayName)
                        .setMessage(license)
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
                (dialog.findViewById<View>(android.R.id.message) as TextView).movementMethod = LinkMovementMethod.getInstance()
            }
        } catch (e: IOException) {
            // Ignore.
        }

    }

    private fun copyStyleFile(mCurrentSelectable: Selectable?, colors: Boolean) {
        try {
            if (mCurrentSelectable is ChooseFileSelectable) {
                startActivityForResult(
                    Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = if (colors) "text/*" else "font/ttf"
                    },
                    (if (colors) ActivityResultCodes.ChooseColors else ActivityResultCodes.ChooseFont).ordinal
                )
                return
            }
            val assetsFolder = if (colors) "colors" else "fonts"

            val defaultChoice = mCurrentSelectable!!.fileName == DEFAULT_FILENAME
            if (defaultChoice) {
                if (colors) {
                    val comment = "# Using default color theme.".toByteArray(StandardCharsets.UTF_8)
                    writeFile(comment, true)
                } else {
                    // Just leave an empty font file as a marker.
                    writeFile(ByteArray(0), false)
                }
            } else {
                assets.open(assetsFolder + "/" + mCurrentSelectable.fileName).use {
                    writeFile(it, colors)
                }
            }

            reloadStyle(colors)
        } catch (e: Exception) {
            val outputFile = if (colors) COLORS_FILENAME else FONT_FILENAME
            Log.w("termux", "Failed to write $outputFile", e)
            val message = resources.getString(R.string.writing_failed) + e.message
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun writeFile(istream: InputStream, colors: Boolean) {
        val outputFile = if (colors) COLORS_FILENAME else FONT_FILENAME
        val context = createPackageContext("com.termux", CONTEXT_IGNORE_SECURITY)
        val homeDir = File(context.filesDir, "home")
        val termuxDir = File(homeDir, ".termux")
        if (!(termuxDir.isDirectory || termuxDir.mkdirs()))
            throw RuntimeException("Cannot create termux dir=" + termuxDir.absolutePath)

        // Use canonicalFile to follow a possible symlink:
        val destinationFile = File(termuxDir, outputFile).canonicalFile
        // Fix for if the user has messed up with chmod:
        destinationFile.setWritable(true)
        destinationFile.parentFile?.setWritable(true)
        destinationFile.parentFile?.setExecutable(true)

        val atomicFile = AtomicFile(destinationFile)
        val out = atomicFile.startWrite()
        istream.copyTo(out)
        atomicFile.finishWrite(out)
    }

    private fun writeFile(content: ByteArray, colors: Boolean) {
        writeFile(ByteArrayInputStream(content), colors)
    }

    private fun reloadStyle(colors: Boolean) {
        // Note: Must match constant in Term#onCreate():
        val actionReload = "com.termux.app.reload_style"
        val executeIntent = Intent(actionReload)
        executeIntent.putExtra(actionReload, if (colors) "colors" else "font")
        sendBroadcast(executeIntent)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        when (requestCode) {
            ActivityResultCodes.ChooseColors.ordinal, ActivityResultCodes.ChooseFont.ordinal -> {
                if (data != null) {
                    val fd = contentResolver.openFileDescriptor(data.data!!, "r")!!
                    val istream = ParcelFileDescriptor.AutoCloseInputStream(fd)
                    val colors = requestCode == ActivityResultCodes.ChooseColors.ordinal
                    writeFile(istream, colors)
                    reloadStyle(colors)
                }
            }
            else -> Log.wtf("termux", "Weird request code in ${javaClass.simpleName}: $requestCode")
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
