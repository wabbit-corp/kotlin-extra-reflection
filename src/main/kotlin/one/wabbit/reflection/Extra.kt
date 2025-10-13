package one.wabbit.reflection

import java.util.ArrayDeque

/**
 * Returns the strict superclass chain excluding this, up to (and including) Object.
 */
fun <T> Class<T>.superclasses(): List<Class<*>> {
    val superclasses = mutableListOf<Class<*>>()
    var superclass = this.superclass
    while (superclass != null) {
        superclasses.add(superclass)
        superclass = superclass.superclass
    }
    return superclasses
}

/** Returns a BFS‑ordered list of all supertypes (superclass and interfaces).
 * Set includeSelf=true to include this class as the first element.
 */
fun <T> Class<T>.supertypes(includeSelf: Boolean = false): List<Class<*>> {
    val seen = LinkedHashSet<Class<*>>()
    val q = ArrayDeque<Class<*>>()

    fun enqueue(c: Class<*>?) {
        if (c != null && seen.add(c)) q.addLast(c)
    }

    if (includeSelf) enqueue(this)
    enqueue(this.superclass)
    this.interfaces.forEach { enqueue(it) }

    while (q.isNotEmpty()) {
        val c = q.removeFirst()
        enqueue(c.superclass)
        c.interfaces.forEach { enqueue(it) }
    }
    return seen.toList()
}

/** BFS over all supertypes (superclass + interfaces). */
fun <T> Class<T>.supertypesSequence(includeSelf: Boolean = false): Sequence<Class<*>> = sequence {
    val seen = LinkedHashSet<Class<*>>()
    val q = ArrayDeque<Class<*>>()

    fun enqueue(c: Class<*>?) {
        if (c != null && seen.add(c)) q.addLast(c)
    }

    if (includeSelf) enqueue(this@supertypesSequence)
    enqueue(this@supertypesSequence.superclass)
    this@supertypesSequence.interfaces.forEach(::enqueue)

    while (q.isNotEmpty()) {
        val c = q.removeFirst()
        yield(c)
        enqueue(c.superclass)
        c.interfaces.forEach(::enqueue)
    }
}

/** All interfaces reachable from this type (including those of superclasses), BFS order, unique. */
fun Class<*>.allInterfaces(): List<Class<*>> {
    val seen = LinkedHashSet<Class<*>>()
    val q = ArrayDeque<Class<*>>()

    // seed with every direct interface of every supertype (incl. self)
    sequenceOf(this).plus(this.supertypesSequence(includeSelf = true))
        .forEach { t -> t.interfaces.forEach { if (seen.add(it)) q.addLast(it) } }

    while (q.isNotEmpty()) {
        val i = q.removeFirst()
        i.interfaces.forEach { if (seen.add(it)) q.addLast(it) }
    }
    return seen.toList()
}

/** Readable wrappers for assignability semantics. */
fun Class<*>.isAssignableTo(target: Class<*>) = target.isAssignableFrom(this)
fun Class<*>.isSubclassOf(target: Class<*>) = this != target && target.isAssignableFrom(this)

/** Nearest common superclass (ignores interfaces); returns null only for pathological classloaders. */
fun Class<*>.nearestCommonSuperclass(other: Class<*>): Class<*>? {
    if (this == other) return this
    val supers = LinkedHashSet<Class<*>>()
    var a: Class<*>? = this
    while (a != null) { supers.add(a); a = a.superclass }
    var b: Class<*>? = other
    while (b != null) {
        if (b in supers) return b
        b = b.superclass
    }
    return null
}

/** Least upper bounds (per JLS intuition): intersection of supertypes, reduced to the most-specific set. */
fun lub(types: Collection<Class<*>>): Set<Class<*>> {
    require(types.isNotEmpty()) { "types must not be empty" }
    val superSets = types.map { t -> sequenceOf(t).plus(t.supertypesSequence(true)).toSet() }
    val intersection = superSets.reduce { a, b -> a intersect b }
    // keep minimal types (drop anything that has a subtype also in the intersection)
    return intersection.filterTo(LinkedHashSet()) { c ->
        intersection.none { other -> other !== c && c.isAssignableFrom(other) }
    }
}
