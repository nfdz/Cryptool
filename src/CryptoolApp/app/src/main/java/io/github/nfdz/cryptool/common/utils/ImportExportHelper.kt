package io.github.nfdz.cryptool.common.utils


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.documentfile.provider.DocumentFile
import com.google.gson.Gson
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.common.model.MigrationData
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


private const val MIME_TYPE = "text/*"
private const val SUGGESTED_NAME_FORMAT = "%s.cryptool"
private const val DATE_FORMAT = "yyyy-MM-dd"

/**
 * This class has methods to manage import/export operations.
 */
class ImportExportHelper(
    private val prefs: PreferencesHelper,
    private val readRequestCode: Int = 933,
    private val writeRequestCode: Int = 944
) {

    /**
     * This method starts open document system activity.
     */
    fun importData(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Timber.w("Invalid SDK version ${Build.VERSION.SDK_INT}")
            return
        }
        // Choose a file via the system's file browser
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        // Show only results that can be "opened", such as a file
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        // Filter to show only plain text
        intent.type = MIME_TYPE
        activity.startActivityForResult(intent, readRequestCode)
    }

    /**
     * This methods manage the result of an open document activity.
     * @return true if activity result was managed by this method, false if not.
     */
    fun onImportActivityResult(
        requestCode: Int,
        resultCode: Int,
        resultData: Intent?,
        context: Context,
        onDataImported: () -> Unit
    ): Boolean {
        return if (requestCode == readRequestCode) {
            // URI to user document is contained in the return intent
            if (resultCode == Activity.RESULT_OK) {
                val uri = resultData?.data
                doAsync {
                    val data: MigrationData? = try {
                        // Get content of the file
                        val file: DocumentFile? =
                            uri?.let { DocumentFile.fromSingleUri(context, it) }
                        val input = file?.let { context.contentResolver.openInputStream(it.uri) }
                        val inputAsString = input?.bufferedReader().use { it?.readText() }
                        inputAsString?.let {
                            val json = CryptographyHelper().decrypt(it, CODE)
                            Gson().fromJson(json, MigrationData::class.java)
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error importing data")
                        null
                    }
                    doMainThread {
                        if (data != null) {
                            MigrationHelper(prefs, data).deployData()
                            context.toast(R.string.import_success)
                            onDataImported()
                        } else {
                            context.toast(R.string.import_read_error)
                        }
                    }
                }
            } else if (resultCode != Activity.RESULT_CANCELED) {
                context.toast(R.string.import_open_error)
            }
            true
        } else {
            false
        }
    }

    /**
     * This method starts create document system activity.
     */
    fun exportDocument(
        activity: Activity
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Timber.w("Invalid SDK version ${Build.VERSION.SDK_INT}")
            return
        }
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        // Show only results that can be "opened", such as a file
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        // Create a file with plain text MIME type
        intent.type = MIME_TYPE
        val sdf =
            SimpleDateFormat(DATE_FORMAT, Locale.US)
        val currentDate = sdf.format(Date())
        val suggestedName = java.lang.String.format(
            SUGGESTED_NAME_FORMAT,
            currentDate
        )
        intent.putExtra(Intent.EXTRA_TITLE, suggestedName)
        activity.startActivityForResult(intent, writeRequestCode)
    }

    /**
     * This methods manage the result of an create document activity.
     * @return true if activity result was managed by this method, false if not.
     */
    fun onExportActivityResult(
        requestCode: Int,
        resultCode: Int,
        resultData: Intent?,
        context: Context
    ): Boolean {
        return if (requestCode == writeRequestCode) {
            // URI to user document is contained in the return intent
            if (resultCode == Activity.RESULT_OK) {
                val uri = resultData?.data
                doAsync {
                    val success: Boolean = try {
                        // Write content in the selected file
                        val data = MigrationHelper(prefs).data
                        val json = Gson().toJson(data)
                        val content = CryptographyHelper().encrypt(json, CODE)
                        val file: DocumentFile? =
                            uri?.let { DocumentFile.fromSingleUri(context, it) }
                        val output = file?.let { context.contentResolver.openOutputStream(it.uri) }
                        output?.bufferedWriter().use { it?.write(content) }
                        true
                    } catch (e: Exception) {
                        Timber.e(e, "Error exporting data")
                        false
                    }
                    doMainThread {
                        if (success) {
                            context.toast(R.string.export_success)
                        } else {
                            context.toast(R.string.export_write_error)
                        }
                    }
                }
            } else if (resultCode != Activity.RESULT_CANCELED) {
                context.toast(R.string.export_open_error)
            }
            true
        } else {
            false
        }
    }
}