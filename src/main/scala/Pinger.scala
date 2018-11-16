import akka.actor.{Actor, PoisonPill}

case object Ping

class Pinger extends Actor {
  var countDown = 100

  def receive = {
    case Pong => println(s"${self.path} received pong, count down ${countDown}")

      if (countDown > 0) {
        countDown -= 1
        sender() ! Ping
      } else {
        sender() ! PoisonPill
        self ! PoisonPill
      }
  }
}