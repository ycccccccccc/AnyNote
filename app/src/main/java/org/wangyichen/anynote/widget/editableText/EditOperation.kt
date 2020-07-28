package org.wangyichen.anynote.widget.editableText

import android.os.Parcel
import android.os.Parcelable
import android.widget.EditText
import java.io.Serializable

//  https://blog.csdn.net/drkcore/article/details/53440392
class EditOperation() : Parcelable,Serializable {
  //  原始内容
  private var src: String? = null
  private var srcStart: Int? = null
  private var srcEnd: Int? = null

  //  目标内容
  private var dst: String? = null
  private var dstStart: Int? = null
  private var dstEnd: Int? = null

  constructor(parcel: Parcel) : this() {
    src = parcel.readString()
    srcStart = parcel.readValue(Int::class.java.classLoader) as? Int
    srcEnd = parcel.readValue(Int::class.java.classLoader) as? Int
    dst = parcel.readString()
    dstStart = parcel.readValue(Int::class.java.classLoader) as? Int
    dstEnd = parcel.readValue(Int::class.java.classLoader) as? Int
  }

  fun setSrc(src: CharSequence?, srcStart: Int, srcEnd: Int): EditOperation = this.apply {
    this.src = src?.toString() ?: ""
    this.srcStart = srcStart
    this.srcEnd = srcEnd
  }

  fun setDst(dst: CharSequence?, dstStart: Int, dstEnd: Int): EditOperation = this.apply {
    this.dst = dst?.toString() ?: ""
    this.dstStart = dstStart
    this.dstEnd = dstEnd
  }

  fun redo(editText: EditText) {
    val text = editText.text
    var idx = -1
    if (src != null) {
      text.delete(srcStart!!, srcEnd!!)
      idx = srcStart!!
    }
    if (dst != null) {
      text.insert(dstStart!!, dst)
      idx = dstEnd!!
    }
    if (idx != -1) {
      editText.setSelection(idx)
    }
  }

  fun undo(editText: EditText) {
    val text = editText.text
    var idx = -1
    if (dst != null) {
      text.delete(dstStart!!, dstEnd!!)
      idx = dstStart!!
    }
    if (src != null) {
      text.insert(srcStart!!, src)
      idx = srcEnd!!
    }
    if (idx != -1) {
      editText.setSelection(idx)
    }
  }

  override fun writeToParcel(parcel: Parcel, flags: Int) {
    parcel.writeString(src)
    parcel.writeValue(srcStart)
    parcel.writeValue(srcEnd)
    parcel.writeString(dst)
    parcel.writeValue(dstStart)
    parcel.writeValue(dstEnd)
  }

  override fun describeContents(): Int {
    return 0
  }

  companion object CREATOR : Parcelable.Creator<EditOperation> {
    override fun createFromParcel(parcel: Parcel): EditOperation {
      return EditOperation(parcel)
    }

    override fun newArray(size: Int): Array<EditOperation?> {
      return arrayOfNulls(size)
    }
  }
}
