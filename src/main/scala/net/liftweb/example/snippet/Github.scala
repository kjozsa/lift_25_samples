package net.liftweb.example.snippet

import net.liftweb.http.{RequestVar, S}
import net.liftweb.http.js.JsCmd
import net.liftweb.util.Props
import xml.NodeSeq


// Github source configuration
object SourceConfig {
  def author = Props.get("github.author") openOr "lift"
  def repo = Props.get("github.repo") openOr "samples"
  def path = author + "/" + repo
}

class Github {
  object jsAddedAlready extends RequestVar[Boolean](false)

  object EnableJqCache extends JsCmd {
    def toJsCmd = """$.ajaxSetup({ cache: true });"""
  }

  object Prettify extends JsCmd {
    def toJsCmd = """$.getScript("https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js?lang=scala", function() {}).fail(function() {alert("could not load prettify");});"""
  }

  object GithubEmbedder extends JsCmd {
    def toJsCmd = """$.getScript("/scripts/ghembedder.min.js", function() { ghe.autoload(); }).fail(function() {alert("could not load github embedder");});"""
  }

  def js = {
    if (!jsAddedAlready) {
      S.appendJs(EnableJqCache & Prettify & GithubEmbedder)
      jsAddedAlready set true
    }
    NodeSeq.Empty
  }

  def render = {
    val fileParam = S.attr("file") openOr sys.error("No file specified")
    val file = if (fileParam.startsWith("src")) fileParam else "src/main/scala/"+fileParam

    val lines = S.attr("lines") openOr sys.error("No lines specified")

    js ++ <div
      data-ghuserrepo={SourceConfig.path}
      data-ghpath={file}
      data-ghlines={lines} class="prettyprint"></div>

  }
}

