package com.stem.stemtraining

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Binder
import android.os.Process
import com.stem.stemtraining.data.TrainingDatabase

class TrainingSummaryProvider : ContentProvider() {
    override fun onCreate() = true
    private fun authorize() {
        val uid = Binder.getCallingUid()
        if (uid == Process.myUid()) return
        val callers = context?.packageManager?.getPackagesForUid(uid).orEmpty()
        if (callers.none { it == "com.stem.companion" }) throw SecurityException("Only S.T.E.M. Companion may read training summary")
    }
    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        authorize()
        if (uri.path != "/summary") throw IllegalArgumentException("Only /summary is supported")
        val dao = TrainingDatabase.getInstance(requireNotNull(context)).trainingDao()
        val since = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000
        val goal = requireNotNull(context).getSharedPreferences("stem_settings", 0).getInt("weekly_goal", 3)
        return MatrixCursor(arrayOf("workouts_7d","working_sets_7d","weekly_goal","latest_workout_at")).apply {
            addRow(arrayOf(dao.completedSince(since), dao.workingSetsSince(since), goal, dao.latestCompletedAt()))
        }
    }
    override fun getType(uri: Uri) = "vnd.android.cursor.item/vnd.stem.training.summary"
    override fun insert(uri: Uri, values: ContentValues?) = throw UnsupportedOperationException("Read only")
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = throw UnsupportedOperationException("Read only")
    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?) = throw UnsupportedOperationException("Read only")
}
