import java.util.concurrent.Future

import akka.Done
import akka.actor.{Actor, Props}
import akka.util.Timeout
import spray.json.DefaultJsonProtocol

object RandomMessages {
  def props(implicit timeout: Timeout) = Props(new RandomMessages)

  case object GetListOfMessages
  case class SaveNewMessage(msg: String)

  sealed trait SucceseResponse
  case class MessageSaved(str: String) extends SucceseResponse
  case class ListOfMessages(list: List[String]) extends SucceseResponse
}

class RandomMessages(implicit timeout: Timeout) extends Actor {
  import RandomMessages._

  var listOfMessages: List[String] = Nil

  def receive = {
    case GetListOfMessages => {
      sender() ! ListOfMessages(listOfMessages)
    }

    case SaveNewMessage(string) =>
        if (string.isEmpty) sender() ! "Message is empty"
        else listOfMessages = List(string) ::: listOfMessages
        println(s"Added ${string} message")
        sender ! MessageSaved(s"Message ${string} was added")

  }

}