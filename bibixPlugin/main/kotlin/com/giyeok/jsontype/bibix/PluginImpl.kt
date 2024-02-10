package com.giyeok.jsontype.bibix

import com.giyeok.bibix.base.BibixValue
import com.giyeok.bibix.base.BuildContext
import com.giyeok.bibix.base.FileValue
import com.giyeok.jsontype.JsonClassCodeGen
import com.giyeok.jsontype.JsonTypeCompiler
import com.giyeok.jsontype.JsonTypeParser
import com.giyeok.jsontype.util.KotlinCodeWriter
import kotlin.io.path.readText
import kotlin.io.path.writeText

class PluginImpl {
  fun build(ctx: BuildContext): BibixValue {
    val parsed = JsonTypeParser.parse(ctx.getFileField("schema").readText())
    val compiler = JsonTypeCompiler(parsed.pkg?.name)
    compiler.compile(parsed.defs)

    val writer = KotlinCodeWriter(compiler.pkg?.names?.joinToString("."))

    compiler.classes.forEach { (_, cls) ->
      writer.writeLine()
      JsonClassCodeGen(writer).generateClass(cls)
    }

    compiler.classes.forEach { (_, cls) ->
      writer.writeLine()
      JsonClassCodeGen(writer).generateClassReader(cls, "", cls.defaultReader)
    }

    compiler.classReaders.forEach { (clsName, readers) ->
      val cls = compiler.classes.getValue(clsName)
      readers.forEach { (_, reader) ->
        writer.writeLine()
        JsonClassCodeGen(writer).generateClassReader(cls, reader.readerName, reader.body)
      }
    }

    val destFile = ctx.clearDestDirectory().resolve(ctx.getStringArg("fileName"))
    destFile.writeText(writer.toString())

    return FileValue(destFile)
  }
}
