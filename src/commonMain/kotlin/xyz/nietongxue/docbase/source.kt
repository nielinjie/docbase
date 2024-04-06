package xyz.nietongxue.docbase


interface Change {
    object Removed : Change
    object Added : Change
    object Changed : Change
}

interface Source {
    val sourceInfo: String
    fun updateBase(docBase: DefaultBase)
}



interface Reactor : Source,DocListener{
}