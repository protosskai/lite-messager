package xyz.lvren.lite_messager.entity

import java.util.*


open class Message(val message: String)

class TextMessage(message: String) : Message(message)

object DataSource {
    val messages = LinkedList<Message>()
}