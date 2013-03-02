import org.eclipse.jetty.server.Server

import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler}
import net.liftweb.http.LiftFilter
import java.util.EnumSet
import javax.servlet.DispatcherType
import org.h2.server.web.WebServlet

object JettyLauncher extends App {
  val port = Option(System.getenv("PORT")).getOrElse("8080").toInt
  val server = new Server(port)

  val context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS)
  context.addFilter(classOf[LiftFilter], "/*", EnumSet.of(DispatcherType.REQUEST))
  context.setResourceBase("src/main/webapp")

  server.start
  server.join
}