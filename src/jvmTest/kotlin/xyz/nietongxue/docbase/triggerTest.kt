package xyz.nietongxue.docbase

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.io.File

class TriggerTest : StringSpec({
    "simple" {
        val events = mutableListOf<DocChangeEvent>()
        val file = File(baseDir, "fileSourceTest")
        val fileSystemImporter = FileSystemImporter(file)
        val base = DefaultBase(DoNothingPersistence, listOf(object : DocListener {
            override fun onChanged(docChangeEvent: DocChangeEvent) {
                events.add(docChangeEvent)
            }
        }, fileSystemImporter, SegmentDerivingTrigger(fileSystemImporter)))
        base.docs.shouldHaveSize(7)
        base.docs.filterIsInstance<ReferringDoc>().shouldHaveSize(2).also {
            it.find { it.name == "a.txt" }.shouldNotBeNull().also {
                getDerivedDocs(it, base) shouldHaveSize 2
            }
            it.find { it.name == "c.txt" }.shouldNotBeNull().also {
                getDerivedDocs(it, base) shouldHaveSize 3
            }
        }
        base.docs.filterIsInstance<DerivedDoc>().shouldHaveSize(5).also {
            it.find { it.name.startsWith("a.txt") }.shouldNotBeNull().also {
                it.derived.origins.shouldHaveSize(1)
                getOriginDocs(it, base).shouldHaveSize(1).also {
                    it.first().name shouldBe "a.txt"
                }
            }
            it.find { it.name.startsWith("c.txt") }.shouldNotBeNull().also {
                it.derived.origins.shouldHaveSize(1)
                getOriginDocs(it, base).shouldHaveSize(1).also {
                    it.first().name shouldBe "c.txt"
                }
            }
        }
        events.shouldHaveSize(7)
    }
    "when change content" {
        val events = mutableListOf<DocChangeEvent>()
        val file = File(baseDir, "fileSourceTest")
        val fileSystemImporter = FileSystemImporter(file)
        val base = DefaultBase(DoNothingPersistence, listOf(object : DocListener {
            override fun onChanged(docChangeEvent: DocChangeEvent) {
                events.add(docChangeEvent)
            }
        }, fileSystemImporter, SegmentDerivingTrigger(fileSystemImporter)))
        withChangedFile(file, "a.txt", "new content") {
            fileSystemImporter.updateBase(base)
            base.docs.shouldHaveSize(7)
            base.docs.filterIsInstance<ReferringDoc>().shouldHaveSize(2).also {
                it.find { it.name == "a.txt" }.shouldNotBeNull().also {
                    getDerivedDocs(it, base) shouldHaveSize 2
                }
                it.find { it.name == "c.txt" }.shouldNotBeNull().also {
                    getDerivedDocs(it, base) shouldHaveSize 3
                }
            }
            base.docs.filterIsInstance<DerivedDoc>().shouldHaveSize(5).also {
                it.find { it.name.startsWith("a.txt") }.shouldNotBeNull().also {
                    it.derived.origins.shouldHaveSize(1)
                    getOriginDocs(it, base).shouldHaveSize(1).also {
                        it.first().name shouldBe "a.txt"
                    }
                }
                it.find { it.name.startsWith("c.txt") }.shouldNotBeNull().also {
                    it.derived.origins.shouldHaveSize(1)
                    getOriginDocs(it, base).shouldHaveSize(1).also {
                        it.first().name shouldBe "c.txt"
                    }
                }
            }
        }
    }
    "when add file" {
        val events = mutableListOf<DocChangeEvent>()
        val file = File(baseDir, "fileSourceTest")
        val fileSystemImporter = FileSystemImporter(file)
        val base = DefaultBase(DoNothingPersistence, listOf(object : DocListener {
            override fun onChanged(docChangeEvent: DocChangeEvent) {
                events.add(docChangeEvent)
            }
        }, fileSystemImporter, SegmentDerivingTrigger(fileSystemImporter)))
        withNewFile(file, "d.txt", "I am d") {
            fileSystemImporter.updateBase(base)
            base.docs.shouldHaveSize(10)
            base.docs.filterIsInstance<ReferringDoc>().shouldHaveSize(3).also {
                it.find { it.name == "a.txt" }.shouldNotBeNull().also {
                    getDerivedDocs(it, base) shouldHaveSize 2
                }
                it.find { it.name == "c.txt" }.shouldNotBeNull().also {
                    getDerivedDocs(it, base) shouldHaveSize 3
                }
                it.find { it.name == "d.txt" }.shouldNotBeNull().also {
                    getDerivedDocs(it, base) shouldHaveSize 2
                }
            }
            base.docs.filterIsInstance<DerivedDoc>().shouldHaveSize(7).also {
                it.find { it.name.startsWith("a.txt") }.shouldNotBeNull().also {
                    it.derived.origins.shouldHaveSize(1)
                    getOriginDocs(it, base).shouldHaveSize(1).also {
                        it.first().name shouldBe "a.txt"
                    }
                }
                it.find { it.name.startsWith("c.txt") }.shouldNotBeNull().also {
                    it.derived.origins.shouldHaveSize(1)
                    getOriginDocs(it, base).shouldHaveSize(1).also {
                        it.first().name shouldBe "c.txt"
                    }
                }
                it.find { it.name.startsWith("d.txt") }.shouldNotBeNull().also {
                    it.derived.origins.shouldHaveSize(1)
                    getOriginDocs(it, base).shouldHaveSize(1).also {
                        it.first().name shouldBe "d.txt"
                    }
                }
            }
        }

    }
    "when move file" {
        val events = mutableListOf<DocChangeEvent>()
        val file = File(baseDir, "fileSourceTest")
        val fileSystemImporter = FileSystemImporter(file)
        val base = DefaultBase(DoNothingPersistence, listOf(object : DocListener {
            override fun onChanged(docChangeEvent: DocChangeEvent) {
                events.add(docChangeEvent)
            }
        }, fileSystemImporter, SegmentDerivingTrigger(fileSystemImporter)))
        withMovedFile(file, "a.txt", "a2.txt") {
            fileSystemImporter.updateBase(base)
            base.docs.shouldHaveSize(7)
            base.docs.filterIsInstance<ReferringDoc>().shouldHaveSize(2).also {
                it.find { it.name == "a2.txt" }.shouldNotBeNull().also {
                    getDerivedDocs(it, base) shouldHaveSize 2
                }
                it.find { it.name == "c.txt" }.shouldNotBeNull().also {
                    getDerivedDocs(it, base) shouldHaveSize 3
                }
            }
            base.docs.filterIsInstance<DerivedDoc>().shouldHaveSize(5).also {
                it.find { it.name.startsWith("a2.txt") }.shouldNotBeNull().also {
                    it.derived.origins.shouldHaveSize(1)
                    getOriginDocs(it, base).shouldHaveSize(1).also {
                        it.first().name shouldBe "a2.txt"
                    }
                }
                it.find { it.name.startsWith("c.txt") }.shouldNotBeNull().also {
                    it.derived.origins.shouldHaveSize(1)
                    getOriginDocs(it, base).shouldHaveSize(1).also {
                        it.first().name shouldBe "c.txt"
                    }
                }
            }
        }
    }
})
