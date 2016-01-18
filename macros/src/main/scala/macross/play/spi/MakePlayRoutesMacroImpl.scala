package macross.play.spi

import scala.io.Source
import scala.reflect.macros.blackbox

import java.io.{File, PrintWriter}

import macross.annotation.base.AnnotationParam
import macross.base.{ProjectFolder, ShowInfo}
import macross.play.RoutesFilePath
import yjs.annotation.Routes._

/**
  * Created by yu jie shui on 2016/1/4 18:50.
  */

case class RouteLine(HttpMethod: String, url: String, codeMethod: String, params: String = "") {
  def id = HttpMethod + url + codeMethod
}

trait MakePlayRoutesMacroImpl
  extends ProjectFolder
  with ShowInfo
  with AnnotationParam {
  val c: blackbox.Context


  import c.universe._

  private[this] def defaultRoutesFile: File = {
    //    config.getString("application.route")
    val of = Play.confOutputDir.listFiles().filter(e ⇒ e.getName == ("routes") || e.getName.contains(".routes"))
    if (of.length < 1)
      c.abort(c.enclosingPosition, s"not find routes file in ${Play.confOutputDir.getAbsolutePath}")
    else if (of.length > 1)
      c.abort(c.enclosingPosition, s"routes file too many ${of.map(_.getAbsolutePath)}")
    else
      of.head
  }


  def controllerRouteLines(controller: Symbol, path: String): Seq[RouteLine] = {
    val controllerMethod =
      controller.typeSignature.members
        .filter(e ⇒ e.annotations.nonEmpty)
        .map(e ⇒
          (e, e.annotations.filter(e ⇒
            e.tree.tpe <:< typeOf[Get] ||
              e.tree.tpe <:< typeOf[Post] ||
              e.tree.tpe <:< typeOf[Put] ||
              e.tree.tpe <:< typeOf[Delete]
          )
            ))
        .filter(_._2.nonEmpty)
    val controllerRouteLines: Seq[RouteLine] = controllerMethod.flatMap {
      case (method: c.universe.Symbol, annotations: List[c.universe.Annotation]) ⇒
        annotations.map(_.tree).map {
          case q"new ${annotation}(url= ${Literal(Constant(url: String))} )" ⇒ annotation → url
          case q"new ${annotation}(${Literal(Constant(url: String))} )" ⇒ annotation → url
        }.map { case (annotation, url) ⇒
          val httpMethod = annotation.tpe match {
            case e if e <:< typeOf[Get] ⇒ "GET"
            case e if e <:< typeOf[Post] ⇒ "POST"
            case e if e <:< typeOf[Put] ⇒ "PUT"
            case e if e <:< typeOf[Delete] ⇒ "DELETE"
          }
          RouteLine(
            HttpMethod = httpMethod,
            url = path + url,
            codeMethod =
              s"${if (controller.isModule || controller.isModuleClass) "" else "@"}${controller.fullName}.${method.name}",
            params =
              method.asMethod.paramLists.map(_.map(e => e.name.toString + ":" + e.info).mkString("(", ",", ")")).mkString
          )
        }
    }.toList
    controllerRouteLines
  }

  private[this] def fileRoutesLines(routesFile: File): Seq[RouteLine] = {
    val asRequestUrl =
      "(GET|POST|DELETE|PUT|->) +([a-z|A-Z|/|0-9|_]+) +([@|a-z|A-Z|.|0-9|_]+)".r
    val asRequestUrlWithParams =
      "(GET|POST|DELETE|PUT|->) +([a-z|A-Z|/|0-9|_]+) +([@|a-z|A-Z|.|0-9|_]+) ?(\\(.*\\))".r

    val routes = Source.fromFile(routesFile).getLines().toList
    val fileRoutes: Seq[RouteLine] = routes.collect {
      case asRequestUrlWithParams(a, b, c, d) ⇒ RouteLine(a.trim, b.trim, c.trim, d.trim)
      case asRequestUrl(a, b, c) ⇒ RouteLine(a.trim, b.trim, c.trim)
    }.toList
    fileRoutes
  }


  def impl(controller: Symbol, path: String, routesFile: Option[File] = None) = {
    val controllerRouteLines: Seq[RouteLine] = this.controllerRouteLines(controller, path)
    val fileRouteLines: Seq[RouteLine] = this.fileRoutesLines(routesFile.getOrElse(this.defaultRoutesFile))
    val fileRoutesMap = fileRouteLines
      .filterNot(e ⇒ controllerRouteLines.exists(_.url == e.url))
      .filterNot(e ⇒ controllerRouteLines.exists(_.codeMethod == e.codeMethod))
      .map(e ⇒ e.id → e).toMap
    val out = fileRoutesMap ++ controllerRouteLines.map(e ⇒ e.id → e).toMap

    val hasChange = !out.toList.map(_._2).forall(e ⇒ fileRouteLines.contains(e))

    val asDebug = false
    if (asDebug) {
      showInfo(
        s"""
           |file      : ${fileRouteLines}
           |out       : ${out.toList.map(_._2)}
           |hasChange : $hasChange
       """.stripMargin)
    }
    if (hasChange) {
      val maxUrlSize = out.values.toList.map(_.url.size).max
      val fileTxt =
        out.values.toList.sortBy(_.url).map(x ⇒ {
          s"${x.HttpMethod}${" " * (8 - x.HttpMethod.size)}${x.url}${" " * (maxUrlSize - x.url.size + 2)}${x.codeMethod}${x.params}"
        }).mkString("\n")

      val outRoutesFile = new PrintWriter(routesFile.getOrElse(this.defaultRoutesFile))
      outRoutesFile.print(fileTxt)
      outRoutesFile.close()

      showInfo("routes file = \n" + show(fileTxt))
    }

  }
  ScopeTag
//  scala.reflect.internal.Scopes

}
/*
java.lang.NullPointerException
        at scala.reflect.internal.Scopes$Scope.unlink(Scopes.scala:195)
        at scala.reflect.internal.Scopes$Scope.unlink(Scopes.scala:215)
        at org.scalamacros.paradise.typechecker.Namers$Namer$MaybeExpandeeCompleter$$anonfun$destroy$1.apply(Namers.scala:303)
        at org.scalamacros.paradise.typechecker.Namers$Namer$MaybeExpandeeCompleter$$anonfun$destroy$1.apply(Namers.scala:301)
        at scala.collection.IndexedSeqOptimized$class.foreach(IndexedSeqOptimized.scala:33)
        at scala.collection.mutable.WrappedArray.foreach(WrappedArray.scala:35)
        at org.scalamacros.paradise.typechecker.Namers$Namer$MaybeExpandeeCompleter.destroy(Namers.scala:301)
        at org.scalamacros.paradise.typechecker.Namers$Namer$$anon$2.maybeExpand(Namers.scala:395)
        at org.scalamacros.paradise.typechecker.Namers$Namer$MaybeExpandeeCompleter.completeImpl(Namers.scala:322)
        at org.scalamacros.paradise.typechecker.Namers$Namer$MaybeExpandeeCompleter.complete(Namers.scala:312)
        at org.scalamacros.paradise.typechecker.Namers$Namer$RichType.completeOnlyExpansions(Namers.scala:340)
        at org.scalamacros.paradise.typechecker.Namers$Namer$MaybeExpandeeCompleter.completeImpl(Namers.scala:324)
        at org.scalamacros.paradise.typechecker.Namers$Namer$MaybeExpandeeCompleter.complete(Namers.scala:312)
        at org.scalamacros.paradise.typechecker.Namers$Namer$RichType.completeOnlyExpansions(Namers.scala:340)
        at org.scalamacros.paradise.typechecker.Expanders$Expander$$anonfun$expandMacroAnnotations$2.apply(Expanders.scala:148)
        at org.scalamacros.paradise.typechecker.Expanders$Expander$$anonfun$expandMacroAnnotations$2.apply(Expanders.scala:141)
        at scala.collection.immutable.List.flatMap(List.scala:327)
        at org.scalamacros.paradise.typechecker.Expanders$Expander$class.expandMacroAnnotations(Expanders.scala:141)
        at org.scalamacros.paradise.typechecker.Expanders$$anon$1.expandMacroAnnotations(Expanders.scala:14)
        at org.scalamacros.paradise.typechecker.AnalyzerPlugins$MacroPlugin$.pluginsEnterStats(AnalyzerPlugins.scala:32)
        at scala.tools.nsc.typechecker.AnalyzerPlugins$$anonfun$pluginsEnterStats$1.apply(AnalyzerPlugins.scala:450)
        at scala.tools.nsc.typechecker.AnalyzerPlugins$$anonfun$pluginsEnterStats$1.apply(AnalyzerPlugins.scala:449)
        at scala.collection.LinearSeqOptimized$class.foldLeft(LinearSeqOptimized.scala:124)
        at scala.collection.immutable.List.foldLeft(List.scala:84)
        at scala.tools.nsc.typechecker.AnalyzerPlugins$class.pluginsEnterStats(AnalyzerPlugins.scala:449)
        at scala.tools.nsc.Global$$anon$1.pluginsEnterStats(Global.scala:463)
        at scala.tools.nsc.typechecker.Typers$Typer.typedPackageDef$1(Typers.scala:5008)
        at scala.tools.nsc.typechecker.Typers$Typer.typedMemberDef$1(Typers.scala:5312)
        at scala.tools.nsc.typechecker.Typers$Typer.typed1(Typers.scala:5359)
        at scala.tools.nsc.typechecker.Typers$Typer.runTyper$1(Typers.scala:5396)
        at scala.tools.nsc.typechecker.Typers$Typer.scala$tools$nsc$typechecker$Typers$Typer$$typedInternal(Typers.scala:5423)
        at scala.tools.nsc.typechecker.Typers$Typer.body$2(Typers.scala:5370)
        at scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:5374)
        at scala.tools.nsc.typechecker.Typers$Typer.typed(Typers.scala:5448)
        at scala.tools.nsc.typechecker.Analyzer$typerFactory$$anon$3.apply(Analyzer.scala:102)
        at scala.tools.nsc.Global$GlobalPhase$$anonfun$applyPhase$1.apply$mcV$sp(Global.scala:441)
        at scala.tools.nsc.Global$GlobalPhase.withCurrentUnit(Global.scala:432)
        at scala.tools.nsc.Global$GlobalPhase.applyPhase(Global.scala:441)
        at scala.tools.nsc.typechecker.Analyzer$typerFactory$$anon$3$$anonfun$run$1.apply(Analyzer.scala:94)
        at scala.tools.nsc.typechecker.Analyzer$typerFactory$$anon$3$$anonfun$run$1.apply(Analyzer.scala:93)
        at scala.collection.Iterator$class.foreach(Iterator.scala:750)
        at scala.collection.AbstractIterator.foreach(Iterator.scala:1202)
        at scala.tools.nsc.typechecker.Analyzer$typerFactory$$anon$3.run(Analyzer.scala:93)
        at scala.tools.nsc.Global$Run.compileUnitsInternal(Global.scala:1500)
        at scala.tools.nsc.Global$Run.compileUnits(Global.scala:1487)
        at scala.tools.nsc.Global$Run.compileSources(Global.scala:1482)
        at scala.tools.nsc.Global$Run.compile(Global.scala:1580)
        at xsbt.CachedCompiler0.run(CompilerInterface.scala:116)
        at xsbt.CachedCompiler0.run(CompilerInterface.scala:95)
        at xsbt.CompilerInterface.run(CompilerInterface.scala:26)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:497)
        at sbt.compiler.AnalyzingCompiler.call(AnalyzingCompiler.scala:101)
        at sbt.compiler.AnalyzingCompiler.compile(AnalyzingCompiler.scala:47)
        at sbt.compiler.AnalyzingCompiler.compile(AnalyzingCompiler.scala:41)
        at sbt.compiler.MixedAnalyzingCompiler$$anonfun$compileScala$1$1.apply$mcV$sp(MixedAnalyzingCompiler.scala:51)
        at sbt.compiler.MixedAnalyzingCompiler$$anonfun$compileScala$1$1.apply(MixedAnalyzingCompiler.scala:51)
        at sbt.compiler.MixedAnalyzingCompiler$$anonfun$compileScala$1$1.apply(MixedAnalyzingCompiler.scala:51)
        at sbt.compiler.MixedAnalyzingCompiler.timed(MixedAnalyzingCompiler.scala:75)
        at sbt.compiler.MixedAnalyzingCompiler.compileScala$1(MixedAnalyzingCompiler.scala:50)
        at sbt.compiler.MixedAnalyzingCompiler.compile(MixedAnalyzingCompiler.scala:65)
        at sbt.compiler.IC$$anonfun$compileInternal$1.apply(IncrementalCompiler.scala:160)
        at sbt.compiler.IC$$anonfun$compileInternal$1.apply(IncrementalCompiler.scala:160)
        at sbt.inc.IncrementalCompile$$anonfun$doCompile$1.apply(Compile.scala:66)
        at sbt.inc.IncrementalCompile$$anonfun$doCompile$1.apply(Compile.scala:64)
        at sbt.inc.IncrementalCommon.cycle(IncrementalCommon.scala:31)
        at sbt.inc.Incremental$$anonfun$1.apply(Incremental.scala:62)
        at sbt.inc.Incremental$$anonfun$1.apply(Incremental.scala:61)
        at sbt.inc.Incremental$.manageClassfiles(Incremental.scala:89)
        at sbt.inc.Incremental$.compile(Incremental.scala:61)
        at sbt.inc.IncrementalCompile$.apply(Compile.scala:54)
        at sbt.compiler.IC$.compileInternal(IncrementalCompiler.scala:160)
        at sbt.compiler.IC$.incrementalCompile(IncrementalCompiler.scala:138)
        at sbt.Compiler$.compile(Compiler.scala:128)
        at sbt.Compiler$.compile(Compiler.scala:114)
        at sbt.Defaults$.sbt$Defaults$$compileIncrementalTaskImpl(Defaults.scala:814)
        at sbt.Defaults$$anonfun$compileIncrementalTask$1.apply(Defaults.scala:805)
        at sbt.Defaults$$anonfun$compileIncrementalTask$1.apply(Defaults.scala:803)
        at scala.Function1$$anonfun$compose$1.apply(Function1.scala:47)
        at sbt.$tilde$greater$$anonfun$$u2219$1.apply(TypeFunctions.scala:40)
        at sbt.std.Transform$$anon$4.work(System.scala:63)
        at sbt.Execute$$anonfun$submit$1$$anonfun$apply$1.apply(Execute.scala:226)
        at sbt.Execute$$anonfun$submit$1$$anonfun$apply$1.apply(Execute.scala:226)
        at sbt.ErrorHandling$.wideConvert(ErrorHandling.scala:17)
        at sbt.Execute.work(Execute.scala:235)
        at sbt.Execute$$anonfun$submit$1.apply(Execute.scala:226)
        at sbt.Execute$$anonfun$submit$1.apply(Execute.scala:226)
        at sbt.ConcurrentRestrictions$$anon$4$$anonfun$1.apply(ConcurrentRestrictions.scala:159)
        at sbt.CompletionService$$anon$2.call(CompletionService.scala:28)
        at java.util.concurrent.FutureTask.run(FutureTask.java:266)
        at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
        at java.util.concurrent.FutureTask.run(FutureTask.java:266)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
        at java.lang.Thread.run(Thread.java:745)

 */