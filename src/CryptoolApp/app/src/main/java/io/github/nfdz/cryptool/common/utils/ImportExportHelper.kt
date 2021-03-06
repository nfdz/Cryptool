package io.github.nfdz.cryptool.common.utils


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import com.google.gson.Gson
import io.github.nfdz.cryptool.R
import io.github.nfdz.cryptool.common.model.MigrationData
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


private const val MIME_TYPE = "*/*"
private const val SUGGESTED_NAME_FORMAT = "%s.cryptool"
private const val DATE_FORMAT = "yyyy-MM-dd"

/**
 * This class has methods to manage import/export operations.
 */
class ImportExportHelper(
    private val prefs: PreferencesHelper,
    private val importRequestCode: Int = 933,
    private val exportRequestCode: Int = 944
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
        val intent = if (isMediaProviderSupported(activity)) {
            Intent(Intent.ACTION_OPEN_DOCUMENT)
        } else {
            Intent(Intent.ACTION_GET_CONTENT)
        }
        // Show only results that can be "opened", such as a file
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        // Filter to show only plain text
        intent.type = MIME_TYPE
        activity.startActivityForResult(intent, importRequestCode)
    }

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        resultData: Intent?,
        context: Context,
        onDataImported: () -> Unit
    ): Boolean {
        return when (requestCode) {
            importRequestCode -> onImportActivityResult(
                requestCode,
                resultCode,
                resultData,
                context,
                onDataImported
            )
            exportRequestCode -> onExportActivityResult(
                requestCode,
                resultCode,
                resultData,
                context
            )
            else -> false
        }
    }

    /**
     * This methods manage the result of an open document activity.
     * @return true if activity result was managed by this method, false if not.
     */
    private fun onImportActivityResult(
        requestCode: Int,
        resultCode: Int,
        resultData: Intent?,
        context: Context,
        onDataImported: () -> Unit
    ): Boolean {
        return if (requestCode == importRequestCode) {
            // URI to user document is contained in the return intent
            if (resultCode == Activity.RESULT_OK) {
                context.toast(R.string.import_ongoing, Toast.LENGTH_SHORT)
                val uri = resultData?.data
                doAsync {
                    var cryptoError = false
                    val data: MigrationData? = try {
                        // Get content of the file
                        val file: DocumentFile? =
                            uri?.let { DocumentFile.fromSingleUri(context, it) }
                        val input = file?.let { context.contentResolver.openInputStream(it.uri) }
                        val inputAsString = input?.bufferedReader().use { it?.readText() }
                        inputAsString?.let {
                            val json = try {
                                CryptographyHelper().decrypt(it, CODE)
                            } catch (e: Exception) {
                                cryptoError = true
                                throw e
                            }
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
                            if (cryptoError) {
                                context.toast(R.string.import_read_crypto_error)
                            } else {
                                context.toast(R.string.import_read_error)
                            }
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
    fun exportData(
        activity: Activity
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Timber.w("Invalid SDK version ${Build.VERSION.SDK_INT}")
            return
        }
        val intent = if (isMediaProviderSupported(activity)) {
            Intent(Intent.ACTION_CREATE_DOCUMENT)
        } else {
            Intent(Intent.ACTION_GET_CONTENT)
        }
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
        activity.startActivityForResult(intent, exportRequestCode)
    }

    /**
     * This methods manage the result of an create document activity.
     * @return true if activity result was managed by this method, false if not.
     */
    private fun onExportActivityResult(
        requestCode: Int,
        resultCode: Int,
        resultData: Intent?,
        context: Context
    ): Boolean {
        return if (requestCode == exportRequestCode) {
            // URI to user document is contained in the return intent
            if (resultCode == Activity.RESULT_OK) {
                context.toast(R.string.export_ongoing, Toast.LENGTH_SHORT)
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

    private fun isMediaProviderSupported(
        activity: Activity
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {

                val pm: PackageManager = activity.packageManager
                val intent = Intent(DocumentsContract.PROVIDER_INTERFACE)
                val providers =
                    pm.queryIntentContentProviders(intent, 0)
                for (info in providers) {
                    if (info?.providerInfo != null) {
                        val authority = info.providerInfo.authority
                        if (isMediaDocumentProvider(Uri.parse("content://$authority"))) return true
                    }
                }
            } catch (e: Exception) {
                Timber.w(e)
            }
        }
        return false
    }

    private fun isMediaDocumentProvider(uri: Uri) =
        "com.android.providers.media.documents" == uri.authority

}