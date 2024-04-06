package xyz.nietongxue.docbase

fun testingBase(): DefaultBase {
    val base = DefaultBase(DoNothingPersistence, mutableListOf(), mutableListOf())
    return base
}

fun testingBaseWithPersistence(jsonStore: JsonStore): DefaultBase {
    val persistence = PersistenceJson(jsonStore)
    return DefaultBase(persistence, mutableListOf(), mutableListOf())
}


fun testingDependBase():DependsBase{
    val base = DependsBase(DoNothingPersistence, mutableListOf(), mutableListOf())
    return base
}