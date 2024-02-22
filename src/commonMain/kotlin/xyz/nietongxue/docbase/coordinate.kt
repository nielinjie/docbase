package xyz.nietongxue.docbase

import kotlinx.serialization.Serializable
import xyz.nietongxue.common.coordinate.*


interface DocDimension {
    @Serializable
    object Area : DocDimension, PathLikeDimension("area")

    @Serializable
    object Aspect : DocDimension,
        CategoryDimension("aspect", listOf("entity", "info", "function", "presentation", "page"))

    //TODO version repository
//    object Version : MaterialDimension,
//        OrderedDimension<SingleBaseVersion>("version", VersionSingleStream(emptyList()).toList().toOrdered())
    @Serializable
    object Layer : DocDimension,
        OrderedDimension("layer", listOf("material", "model", "component", "artifact", "runtime"))

    @Serializable
    object Phase : DocDimension,
        OrderedDimension("phase", listOf("require", "design", "develop", "test", "release"))

}



@Serializable
class DocSelector(val pres: List<ValueBasedPredicate>) : PredicatesSelector(pres)