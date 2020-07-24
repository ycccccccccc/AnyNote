package org.wangyichen.anynote.widget.editReminderDialog

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_edit_reminder.*
import org.wangyichen.anynote.R
import org.wangyichen.anynote.utils.ConfermDialogFragment
import org.wangyichen.anynote.utils.TimeUtils

class EditReminderDialog : DialogFragment() {
  private var chooseTime = true
  private lateinit var listener: ConfermListener
  private var year = 0
  private var month = 0
  private var day = 0
  private var hour = 0
  private var minute = 0

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.dialog_edit_reminder, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initReminder()
    setupButton()
  }

  private fun initReminder() {
    val reminder =
      if (arguments?.getLong(EXTRA_REMINDER) != 0L) arguments?.getLong(EXTRA_REMINDER)!! else TimeUtils.getTime()
    val list = TimeUtils.time2Date(reminder)
    year = list[0]
    month = list[1] - 1
    day = list[2]
    hour = list[3]
    minute = list[4]

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      time_picker.hour = hour
      time_picker.minute = minute
    }
    time_picker.setOnTimeChangedListener { timePicker, hour, minute ->
      this.hour = hour
      this.minute = minute
    }
    data_picker.init(year, month, day) { datePicker: DatePicker, year: Int, month: Int, day: Int ->
      this.year = year
      this.month = month
      this.day = day
    }

  }

  private fun setupButton() {
    btn_switch.setOnClickListener {
      chooseTime = !chooseTime
      if (chooseTime) {
        btn_switch.text = "日期"
        time_picker.visibility = View.VISIBLE
        data_picker.visibility = View.GONE
      } else {
        btn_switch.text = "时间"
        time_picker.visibility = View.GONE
        data_picker.visibility = View.VISIBLE
      }
    }
    btn_save_reminder.setOnClickListener {
      listener.onPositive(TimeUtils.data2time(year, month + 1, day, hour, minute))
      dismiss()
    }
    btn_delete_reminder.setOnClickListener {
      listener.onNegtive()
      dismiss()
    }
  }

  companion object {
    const val EXTRA_ID = "EXTRA_ID"
    const val EXTRA_REMINDER = "EXTRA_REMINDER"

    fun newInstance(id: Long, reminder: Long, listener: ConfermListener) =
      EditReminderDialog()
        .apply {
          arguments = Bundle().apply {
            putLong(EXTRA_ID, id)
            putLong(EXTRA_REMINDER, reminder)
          }
          this.listener = listener
        }
  }
  interface ConfermListener {
    fun onPositive(reminder:Long)
    fun onNegtive()
  }
}