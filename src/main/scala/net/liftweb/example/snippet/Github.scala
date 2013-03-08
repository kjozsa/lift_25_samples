package net.liftweb.example.snippet

import net.liftweb.http.{RequestVar, S}
import net.liftweb.http.js.JsCmd
import net.liftweb.util.Props


// Github source configuration
object SourceConfig {
  def author = Props.get("github.author") openOr "lift"
  def repo = Props.get("github.repo") openOr "samples"
  def path = author + "/" + repo
}

class Github {
  object jsAddedAlready extends RequestVar[Boolean](false)

  object Prettify extends JsCmd {
    def toJsCmd = """$.getScript("https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js?lang=scala", function() {});"""
  }

  object GithubEmbedder extends JsCmd {
    def toJsCmd = """$.getScript("/scripts/ghembedder.min.js", function() { ghe.autoload(); });"""
  }


  def render = {
    if (!jsAddedAlready) {
      S.appendJs(Prettify & GithubEmbedder)
      jsAddedAlready set true
    }

    val fileParam = S.attr("file") openOr sys.error("No file specified")
    val file = if (fileParam.startsWith("src")) fileParam else "src/main/scala/"+fileParam

    val lines = S.attr("lines") openOr sys.error("No lines specified")

    <div
      data-ghuserrepo={SourceConfig.path}
      data-ghpath={file}
      data-ghlines={lines} class="prettyprint"></div>

  }
}

