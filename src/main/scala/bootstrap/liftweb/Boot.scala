package bootstrap.liftweb


import net.liftweb.http._
import js.jquery.JQueryArtifacts
import net.liftweb._
import common.{Full, Loggable}
import sitemap.Loc.{ExtLink, Unless}
import util.{Props, Helpers}
import http._
import actor._
import sitemap._
import Helpers._

import example._

import comet.ExampleClock
import snippet._

import scala.language.postfixOps
import net.liftmodules.widgets.autocomplete.AutoComplete


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {

  def boot {
    // where to search snippet
    LiftRules.addToPackages("net.liftweb.example")

    //Init the jQuery module, see http://liftweb.net/jquery for more information.
    LiftRules.jsArtifacts = JQueryArtifacts

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    LiftRules.setSiteMapFunc(() => MenuInfo.sitemap)

    // Dump information about session every 10 seconds
    SessionMaster.sessionWatchers = SessionInfoDumper :: SessionMaster.sessionWatchers

    // used by the Ajax example
    AutoComplete.init()

    LiftRules.localeCalculator = r => definedLocale.openOr(LiftRules.defaultLocaleCalculator(r))

    LiftRules.cometCreation.append {
      case CometCreationInfo("Clock", name, defaultXml, attributes, session) =>
        new ExampleClock(session, Full("Clock"), name, defaultXml, attributes)
    }


    //        if (!DB.jndiJdbcConnAvailable_?) {
    //      val vendor =
    //        new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
    //          Props.get("db.url") openOr
    //            "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
    //          Props.get("db.user"), Props.get("db.password"))
    //
    //      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)
    //
    //      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    //    }
    //
    //    // Use Lift's Mapper ORM to populate the database
    //    // you don't need to use Mapper to use Lift... use
    //    // any ORM you want
    //    Schemifier.schemify(true, Schemifier.infoF _, User)
    //
    //
    //    // Build SiteMap
    //    def sitemap = SiteMap(
    //      Menu.i("Home") / "index" >> User.AddUserMenusAfter, // the simple way to declare a menu
    //
    //      // more complex because this menu allows anything in the
    //      // /static path to be visible
    //      Menu(Loc("Static", Link(List("static"), true, "/static/index"), "Static Content")))
    //
    //    def sitemapMutators = User.sitemapMutator
    //
    //    // set the sitemap.  Note if you don't want access control for
    //    // each page, just comment this line out.
    //    LiftRules.setSiteMapFunc(() => sitemapMutators(sitemap))
    //
    //
    //    // What is the function to test if a user is logged in?
    //    LiftRules.loggedInTest = Full(() => User.loggedIn_?)
    //
    //    LiftRules.dispatch.append(DelayedRest)
    //
    //    LiftRules.dataAttributeProcessor.append {
    //      case ("wombat", str, nodes, _) =>
    //        ("div *+" #> str).apply(nodes)
    //    }
    //
    //    JavaScriptContext.install()
    //
    //    // Make a transaction span the whole HTTP request
    //    S.addAround(DB.buildLoanWrapper)
  }

  object MenuInfo {
    def sitemap = SiteMap(
      Menu("Home") / "index",
      Menu("Interactive Stuff") / "interactive" submenus(
        Menu("Comet Chat") / "chat",
        Menu("Ajax Samples") / "ajax",
        Menu("Ajax Form") / "ajax-form",
        Menu("Modal Dialog") / "rhodeisland",
        Menu("Ajax and Forms") / "form_ajax"
        ),
      Menu("Templating") / "templating" / "index" submenus(
        Menu("Surround") / "templating" / "surround",
        Menu("Embed") / "templating" / "embed",
        Menu("Evalutation Order") / "templating" / "eval_order",
        Menu("Select <div>s") / "templating" / "selectomatic",
        Menu("Simple Wizard") / "simple_wizard",
        Menu("Lazy Loading") / "lazy",
        Menu("Parallel Snippets") / "parallel",
        Menu("<head/> tag") / "templating" / "head"
        ),
      Menu("Web Services") / "ws",
      Menu("Localization") / "lang",
      Menu("Menus") / "menu" / "index" submenus(
        Menu("First Submenu") / "menu" / "one",
        Menu("Second Submenu (has more)") / "menu" / "two" submenus(
          Menu("First (2) Submenu") / "menu" / "two_one",
          Menu("Second (2) Submenu") / "menu" / "two_two"),
        Menu("Third Submenu") / "menu" / "three",
        Menu("Forth Submenu") / "menu" / "four"
        ),
      Menu(Loc("lift", ExtLink("http://liftweb.net"), <xml:group>
        <i>Lift</i>
        project home</xml:group>)),
      Menu(Loc("src", ExtLink("https://github.com/kjozsa/lift_25_samples"), "Source code for this site"))

    )
  }

  object SessionInfoDumper extends LiftActor with Loggable {
    private var lastTime = millis

    private def cyclePeriod = 1 minute

    import net.liftweb.example.lib.SessionChecker

    protected def messageHandler = {
      case SessionWatcherInfo(sessions) =>
        if ((millis - cyclePeriod) > lastTime) {
          lastTime = millis
          val rt = Runtime.getRuntime
          rt.gc

          RuntimeStats.lastUpdate = now
          RuntimeStats.totalMem = rt.totalMemory
          RuntimeStats.freeMem = rt.freeMemory
          RuntimeStats.sessions = sessions.size

          val percent = (RuntimeStats.freeMem * 100L) / RuntimeStats.totalMem

          // get more aggressive about purging if we're
          // at less than 35% free memory
          if (percent < 35L) {
            SessionChecker.killWhen /= 2L
            if (SessionChecker.killWhen < 5000L)
              SessionChecker.killWhen = 5000L
            SessionChecker.killCnt *= 2
          } else {
            SessionChecker.killWhen *= 2L
            if (SessionChecker.killWhen >
              SessionChecker.defaultKillWhen)
              SessionChecker.killWhen = SessionChecker.defaultKillWhen
            val newKillCnt = SessionChecker.killCnt / 2
            if (newKillCnt > 0) SessionChecker.killCnt = newKillCnt
          }

          def pretty(in: Long): String = if (in > 1000L) pretty(in / 1000L) + "," + (in % 1000L) else in.toString

          val dateStr: String = now.toString
          logger.debug("[MEMDEBUG] At " + dateStr + " Number of open sessions: " + sessions.size)
          logger.debug("[MEMDEBUG] Free Memory: " + pretty(RuntimeStats.freeMem))
          logger.debug("[MEMDEBUG] Total Memory: " + pretty(RuntimeStats.totalMem))
          logger.debug("[MEMDEBUG] Kill Interval: " + (SessionChecker.killWhen / 1000L))
          logger.debug("[MEMDEBUG] Kill Count: " + (SessionChecker.killCnt))
        }
    }
  }

}
