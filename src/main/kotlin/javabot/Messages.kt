package javabot

import java.util.ArrayList
import javax.inject.Singleton

@Singleton
public class Messages : Iterable<String> {
    var messages: MutableList<String> = ArrayList()

    public fun isEmpty(): Boolean {
        return messages.isEmpty()
    }

    public fun add(message: String) {
        messages.add(message)
    }

    public fun size(): Int {
        return messages.size
    }

    public fun remove(index: Int): String {
        return messages.removeAt(index)
    }

    public fun get(): List<String> {
        val list = ArrayList(messages)
        messages.clear()
        return list
    }

    public fun get(index: Int): String {
        return messages[index]
    }

    override fun iterator(): Iterator<String> {
        return messages.iterator()
    }

    override fun toString(): String {
        return messages.toString()
    }
}