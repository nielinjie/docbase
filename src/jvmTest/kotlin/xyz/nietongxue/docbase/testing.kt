package xyz.nietongxue.docbase

import xyz.nietongxue.docbase.depends.DependsBase
import java.io.File

fun testingBase(): DefaultBase {
    val base = DefaultBase(DoNothingPersistence, listOf())
    return base
}

fun testingBaseWithPersistence(jsonStore: JsonStore): DefaultBase {
    val persistence = PersistenceJson(jsonStore)
    return DefaultBase(persistence, listOf())
}


fun testingDependBase(): DependsBase {
    val base = DependsBase(DoNothingPersistence, listOf())
    return base
}

fun withChangedFile(base: File, name: String, content: String, block: () -> Unit) {
    val file = File(base, name)
    val oldContent = file.readText()
    file.writeText(content)
    try {
        block()
    } catch (e: Throwable) {
        file.writeText(oldContent)
        throw e
    }
    file.writeText(oldContent)
}

fun withNewFile(base: File, name: String, content: String, block: () -> Unit) {
    val file = File(base, name)
    require(!file.exists())
    file.writeText(content)
    try {
        block()
    } catch (e: Throwable) {
        file.delete()
        throw e
    }
    file.delete()
}
fun withMovedFile(base: File, name: String, newName: String, block: () -> Unit) {
    val file = File(base, name)
    val newFile = File(base, newName)
    require(!newFile.exists())
    file.renameTo(newFile)
    try {
        block()
    } catch (e: Throwable) {
        newFile.renameTo(file)
        throw e
    }
    newFile.renameTo(file)
}