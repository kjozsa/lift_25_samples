package net.liftweb.example.snippet

import net.liftweb.http.S
import net.liftweb.http.js.JsCmd
import net.liftweb.util.Props


// Github source configuration
object SourceConfig {
  def author = Props.get("github.author") openOr "lift"
  def repo = Props.get("github.repo") openOr "samples"
  def path = author + "/" + repo
}

class Github {
  object Prettify extends JsCmd {
    def toJsCmd = """$.getScript("https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js?lang=scala", function() {});"""
  }

  object GithubEmbedder extends JsCmd {
    def toJsCmd = """$.getScript("/scripts/ghembedder.min.js", function() { ghe.autoload(); });"""
  }


  def render = {
    S.appendJs(Prettify & GithubEmbedder)
    val file = S.attr("file") openOr sys.error("No file specified")
    val lines = S.attr("lines") openOr sys.error("No lines specified")

    <div
      data-ghuserrepo={SourceConfig.path}
      data-ghpath={"src/main/scala/"+file}
      data-ghlines={lines} class="prettyprint"></div>

  }
}

