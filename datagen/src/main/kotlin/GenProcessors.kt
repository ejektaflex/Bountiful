import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.io.File

/*
This file replaces our basic processor definitions with full ones from a template. It's not super flexible,
but it's all we really need right now.
 */

val processorsLocation = File("common/src/main/resources/data/bountiful/worldgen/processor_list")
val processorFolder = File("datagen/data/processors")
val defaultProcessorText = File(processorFolder, "default.json5").readText()
val procReplace = File(processorFolder, "replacements.json").readText()

fun extract(text: String, section: String, replaceBlock: String? = null): String {
    println("Extracting $section")
    return text.substringAfter("/*${section}_start*/")
        .substringBefore("/*${section}_end*/")
        .run { if (replaceBlock != null)
            replace("%%%%", replaceBlock)
        else
            this
        }
}

fun main() {

    val replaceObj = Json.decodeFromString(
        MapSerializer(String.serializer(), MapSerializer(String.serializer(), String.serializer())), procReplace
    )

    // Delete old processors
    processorsLocation.apply {
        if (exists()) {
            deleteRecursively()
        }
        mkdirs()
    }

    for ((type, obj) in replaceObj) {
        var procText = defaultProcessorText
        var proto = ""

        proto += extract(defaultProcessorText, "header")

//        for ((part, newBlock) in obj) {
//            procText = procText.replace("%$part%", newBlock)
//        }

        obj.toList().forEachIndexed { index, pair ->
            proto += extract(defaultProcessorText, pair.first, pair.second)
        }

        val abc = """
            					},
					"location_predicate": {
						"predicate_type": "minecraft:always_true"
					}
				},
				
			],
			"processor_type": "minecraft:rule"
		}
        """.trimIndent()

        proto += extract(defaultProcessorText, "footer")

        // Replace the comment section seps
        proto = proto.replace(
            Regex("/\\*.+\\*/"), ""
        )

        // Remove trailing comma
        proto = proto.replace(Regex("}(,)([\n\\s]+)]"), "}$2]")

        File(processorsLocation, "${type}.json").writeText(proto)
    }

}