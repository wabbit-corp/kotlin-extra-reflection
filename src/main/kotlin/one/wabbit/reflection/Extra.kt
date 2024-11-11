package one.wabbit.reflection

import java.util.ArrayDeque

fun <T> Class<T>.superclasses(): List<Class<*>> {
    val superclasses = mutableListOf<Class<*>>()
    var superclass = this.superclass
    while (superclass != null) {
        superclasses.add(superclass)
        superclass = superclass.superclass
    }
    return superclasses
}

fun <T> Class<T>.parents(): List<Class<*>> {
    val parents = LinkedHashSet<Class<*>>()
    val queue = ArrayDeque<Class<*>>()
    queue.add(this)
    while (queue.isNotEmpty()) {
        val clazz = queue.removeFirst()

        val candidates = mutableListOf<Class<*>>()
        candidates.add(clazz)
        if (clazz.superclass != null)
            candidates.add(clazz.superclass)
        candidates.addAll(clazz.interfaces)

        for (candidate in candidates)
            if (parents.add(candidate)) queue.add(candidate)
    }
    return parents.toList()
}
