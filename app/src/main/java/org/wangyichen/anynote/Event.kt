package org.wangyichen.anynote

class Event<T>(private val content:T) {
  private var hasBeenHandled = false

  fun getContent():T? {
    if (hasBeenHandled) {
      return null
    } else {
      hasBeenHandled = true
      return content
    }

  }
}