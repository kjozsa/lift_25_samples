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
import _root_.net.liftweb.http.SHtml._
import _root_.net.liftweb.util.Helpers._
import _root_.net.liftweb.common._
import _root_.scala.xml.{NodeSeq, Text}


class DivSelector extends StatefulSnippet {
  private var whichDivs = Array(true, true, true, true, true, true)

  def dispatch: DispatchIt = {
    case "select" => selectDivs
    case "populate" => populate
  }

  def populate =
    ".line" #> whichDivs.zipWithIndex.map {
      case (value, num) =>
        ".number" #> Text(num.toString) &
          ".checkbox" #> checkbox(value, v => whichDivs(num) = v)
    }

  def selectDivs(in: NodeSeq): NodeSeq = {
    def calcNum(in: String): Box[Int] =
      if (in.startsWith("num_")) asInt(in.substring(4))
      else Empty

    for {
      div <- in \\ "div" // select the div tags
      id = (div \ "@id").text // get their id
      num <- calcNum(id) if whichDivs(num) // filter
    } yield div
  }
}
