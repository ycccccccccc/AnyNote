package org.wangyichen.anynote.utils

import android.app.Activity
import android.content.Intent

class IntentUtils {
  companion object {
    const val REQUEST_CODE_ADD_EDIT_NOTE_ACTIVITY = 1

    const val ADD_EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 1
    const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2
    const val EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 3
    const val ADD_SKETCH_OK = Activity.RESULT_FIRST_USER + 4

    fun startActivity(source: Activity, intent: Intent) {
      source.startActivity(intent)
    }

    fun startActivityForResult(source: Activity, intent: Intent, requestCode: Int) {
      source.startActivityForResult(intent, requestCode)
    }
  }

}