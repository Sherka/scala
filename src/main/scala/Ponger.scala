import akka.actor.{Actor, ActorRef}

case object Pong

class Ponger(pinger: ActorRef) extends Actor {
  def receive = {
    case Ping => println(s"${self.path} received ping")
      pinger ! Pong
  }
}