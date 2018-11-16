import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}

import language.postfixOps
import scala.concurrent.duration._

object Main extends App with RequestTimeout {

  val config = ConfigFactory.load()
  implicit val system = ActorSystem("testActorSystem")
  val pinger = system.actorOf(Props[Pinger], "pinger")
  val ponger = system.actorOf(Props(classOf[Ponger], pinger), "ponger")

  implicit val executionContext = system.dispatcher
  implicit val actorMaterializer = ActorMaterializer()

  val api = new RestApi(system, requestTimeout(config))

  val bindingFuture = Http().bindAndHandle(api.routes, "localhost", 8080)

  system.scheduler.scheduleOnce(500 millis) {
    ponger ! Ping
  }
}

trait RequestTimeout {
  import scala.concurrent.duration._
  def requestTimeout(config: Config): Timeout = {
    val t = config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }
}
