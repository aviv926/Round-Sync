package de.felixnuesse.extract.notifications.implementations

import android.content.Context
import ca.pkay.rcloneexplorer.Items.FileItem
import ca.pkay.rcloneexplorer.R
import ca.pkay.rcloneexplorer.notifications.prototypes.WorkerNotification
import ca.pkay.rcloneexplorer.notifications.support.StatusObject
import androidx.preference.PreferenceManager

class UploadWorkerNotification(var context: Context) : WorkerNotification(context) {

    override val CHANNEL_ID = "de.felixnuesse.extract.upload_service"

    override val initialTitle = string(R.string.worker_upload_initialtitle)
    override val serviceOngoingTitle = initialTitle
    override val serviceFailed = string(R.string.worker_upload_failed)
    override val serviceCancelled = string(R.string.worker_upload_cancelled)
    override val serviceSuccess = string(R.string.worker_upload_complete)


    override val channel_ongoing_title = string(R.string.upload_service_ongoing_notification_title)
    override val channel_ongoing_description = string(R.string.upload_service_ongoing_notification_description)
    override val channel_success_title = string(R.string.upload_service_success_notification_title)
    override val channel_success_description = string(R.string.upload_service_success_notification_description)
    override val channel_failed_title = string(R.string.upload_service_failed_notification_title)
    override val channel_failed_description = string(R.string.upload_service_failed_notification_description)

    override val PERSISTENT_NOTIFICATION_ID = 90

    override val SUMMARY_ID = 91

    override fun generateSuccessMessage(statusObject: StatusObject, fileItem: FileItem): String {
        val transfers = statusObject.getTotalTransfers()
        val message = if (transfers <= 1 && !fileItem.isDir) {
            statusObject.getTransfers().toString()
        } else {
            fileItem.name
        }
        val prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
        val transfersPref = prefs.getInt(mContext.getString(R.string.pref_key_rclone_transfers), 1)
        val bwlimitPref = prefs.getInt(mContext.getString(R.string.pref_key_rclone_bwlimit), 0)
        val checkersPref = prefs.getInt(mContext.getString(R.string.pref_key_rclone_checkers), 8)
        val bwlimitStr = if (bwlimitPref > 0) " | BWLimit: ${bwlimitPref}MB" else ""
        val paramInfo = " | Transfers: $transfersPref | Checkers: $checkersPref$bwlimitStr"
        return mContext.resources.getQuantityString(
            R.plurals.worker_upload_success_message,
            transfers,
            message
        ) + paramInfo
    }

}