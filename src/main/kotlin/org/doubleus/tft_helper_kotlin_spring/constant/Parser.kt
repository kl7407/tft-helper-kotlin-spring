package org.doubleus.tft_helper_kotlin_spring.constant

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File
import java.nio.file.Paths

object Parser {
    private val path = "${Paths.get("").toAbsolutePath()}/src/main/assets"
    private val file = File("${path}/description-en.json")
    private val rawData = file.readText(Charsets.UTF_8)
    private val mapper = ObjectMapper()
    private val tree = mapper.readTree(rawData)
    private const val seasonIndex = 6
    private val seasonData = tree.get("sets").get(seasonIndex.toString())

    private fun toCamelCase(str: String): String {
        var shouldUpper = false
        val parsed = StringBuffer()
        str.forEachIndexed{ i, it ->
            if (it == '-' || it == '_')
                shouldUpper = true
            else {
                if (i == 0)
                    parsed.append(it.lowercase())
                else {
                    if (shouldUpper) {
                        parsed.append(it.uppercase())
                        shouldUpper = false
                    }
                    else if (it == ' ') {
                        shouldUpper = true
                    }
                    else if (it == '\'') {
                        return@forEachIndexed
                    }
                    else parsed.append(it)
                }
            }
        }
        return parsed[0].lowercase() + parsed.substring(1)
    }

    fun getAugmentInfo() {
        val itemList = tree.get("items")
        itemList
            .filter{
                val filePath = it.get("icon").toString().replace("\"", "")
                return@filter filePath.startsWith("ASSETS/Maps/Particles/TFT/Item_Icons/Augments")
            }
            .sortedBy{ it.get("id").asInt() }
            .forEach{
                val name = it.get("name").toString()
                val words = name.replace("\"", "").split(" ")
                var id = ""
                for (word in words) {
                    id += when (word) {
                        "I" -> "1"
                        "II" -> "2"
                        "III" -> "3"
                        else -> word
                    }
                }
                println("private const val ${id} = Augment(\"${id}\")")
            }
    }

    fun getTraitInfo() {
        val traitList = seasonData.get("traits")
        traitList
            .sortedBy { it.get("apiName").toString() }
            .forEach{ trait ->
                val id = trait.get("apiName").toString()
                val styles = mutableListOf<TraitStyle>()
                trait.get("effects").forEach{ style ->
                    styles.add(
                        TraitStyle(
                            style = style.get("style").asInt(),
                            min = style.get("minUnits").asInt(),
                            max = style.get("maxUnits").asInt(),
                        )
                    )
                }
                var stylesStr = "listOf("
                styles.forEachIndexed{ i, it ->
                    stylesStr += it.toString()
                    if (i != (styles.size - 1))
                        stylesStr += ", "
                }
                stylesStr += ")"
                val parsedId = id.split("_")[1].replace("\"", "")
                println("private val ${toCamelCase(parsedId)} = Trait(${id}, ${stylesStr})")
            }
    }

    fun getChampionInfo() {
        val championList = seasonData.get("champions")
        championList
            .sortedBy { champion ->
                val cost = champion.get("cost").asInt()
                val id = champion.get("apiName").toString().replace("\"", "")
                return@sortedBy "${cost}${id}"
            }
            .forEach{ champion ->
                val cost = champion.get("cost").asInt()
                val id = champion.get("apiName").toString().replace("\"", "")
                val parsedId = id.split("_")[1].replace("\"", "")
                val traits = champion.get("traits").toList()
                var traitsStr = "listOf("
                traits.forEachIndexed{i, it ->
                    traitsStr += toCamelCase(it.toString().replace("\"", ""))
                    if (i != (traits.size - 1))
                        traitsStr += ", "
                }
                traitsStr += ")"
                println("val ${toCamelCase(parsedId)} = Champion(\"${id}\", ${traitsStr}, ${cost})")
            }
    }

    fun getItemInfo() {
        val itemList = tree.get("items")
        itemList
            .filter{
                val filePath = it.get("icon").toString().replace("\"", "")
                filePath.startsWith("ASSETS/Maps/Particles/TFT/Item_Icons/Spatula/Set6") || filePath.startsWith("ASSETS/Maps/Particles/TFT/Item_Icons/Standard")
            }
            .sortedBy{ it.get("id").asInt() }
            .forEach{
                println("private const val ${toCamelCase(it.get("name").toString().replace("\"", ""))} = ${it.get("id").asInt()}")
            }
    }

    fun getAndroidStringInfo(target: String) {
        if (target == "items") {
            tree.get("items")
                .filter{
                    val filePath = it.get("icon").toString().replace("\"", "")
                    filePath.startsWith("ASSETS/Maps/Particles/TFT/Item_Icons/Spatula/Set6") || filePath.startsWith("ASSETS/Maps/Particles/TFT/Item_Icons/Standard")
                }
                .sortedBy{ it.get("id").asInt() }
                .forEach{
                    println(toStringInfo("item" + toCamelCase(it.get("id").toString().replace("\"", "")), it.get("name").toString()))
                }
            return
        }
        val dataList = seasonData.get(target)
        dataList
            .sortedBy { data -> data.get("apiName").toString() }
            .forEach { champion ->
                val id = champion.get("apiName").toString().replace("\"", "").replace("'", "\\'")
                val name = champion.get("name").toString().replace("\"", "").replace("'", "\\'")
                println(toStringInfo(id, name))
            }
    }

    private fun toStringInfo(id: String, name: String): String = "<string name=\"${id}\">${name}</string>"
}

/*
fun main() {
    Parser.getAugmentInfo()
    println("----")
    Parser.getTraitInfo()
    println("----")
    Parser.getChampionInfo()
    println("----")
    Parser.getItemInfo()
    println("----")
    Parser.getAndroidStringInfo("champions")
    println("----")
    Parser.getAndroidStringInfo("traits")
    println("----")
    Parser.getAndroidStringInfo("items")
}
*/