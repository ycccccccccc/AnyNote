package org.wangyichen.anynote.module.notes

interface NotesActionModeActionListener {
  fun onActionTopping()
  fun onActionArchive()
  fun onActionUnarchive()
  fun onActionDelete()
  fun onActionChangeNotebook()
  fun onActionClear()
  fun onActionRestore()
}