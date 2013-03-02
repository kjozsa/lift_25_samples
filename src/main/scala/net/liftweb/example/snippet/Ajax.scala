/*
 * Copyright 2007-2010 WorldWide Conferencing, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.liftweb.example.snippet

import _root_.net.liftweb.http._
import net.liftweb.common._

//import _root_.net.liftweb.widgets.autocomplete._

import net.liftweb.util.Helpers._

import js._
import js.jquery._
import JqJsCmds._
import JsCmds._
import SHtml._
import _root_.scala.xml.{Text, NodeSeq}

import scala.language.postfixOps

class Ajax extends Loggable {

  def render = {
    // local state for the counter
    var cnt = 0

    // build up an ajax <a> tag to increment the counter
    def doClicker(in: NodeSeq) = a(() => {
      cnt = cnt + 1;
      SetHtml("count", Text(cnt.toString))
    }, in)

    // create an ajax select box
    def doSelect(msg: NodeSeq) = ajaxSelect((1 to 50).toList.map(i => (i.toString, i.toString)),
      Full(1.toString),
      v => DisplayMessage("messages",
        bind("sel", msg, "number" -> Text(v)),
        5 seconds, 1 second))

    // build up an ajax text box
    def doText(msg: NodeSeq) = ajaxText("", v => DisplayMessage("messages", {
      val css = "#value" #> Text(v)
      css(msg)
    }, 4 seconds, 1 second))




    // use css selectors to bind the view to the functionality
    "#clicker" #> doClicker _ &
      "select" #> doSelect _ &
      "#ajaxText" #> doText _ // &
    //    "auto" #> AutoComplete("", buildQuery _, _ => ()))
  }

  private def buildQuery(current: String, limit: Int): Seq[String] = {
    logger.info("Checking on server side with " + current + " limit " + limit)
    (1 to limit).map(n => current + "" + n)
  }

  def buttonClick = {
    import js.JE._

    "* [onclick]" #> SHtml.ajaxCall(ValById("the_input"),
      s => SetHtml("messages",
        <i>Text box is
          {s}
        </i>))
  }
}