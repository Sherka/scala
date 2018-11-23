import akka.NotUsed
import akka.actor.{Actor, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout

import scala.collection.mutable.ListBuffer
import scala.util.Success

object RandomMessages {
  def props(implicit timeout: Timeout) = Props(new RandomMessages)

  case object GetListOfMessages
  case class SaveNewMessage(msg: String)

  sealed trait SuccessResponse
  case class MessageSaved(str: String) extends SuccessResponse
  case class ListOfMessages(list: List[String]) extends SuccessResponse
  case class GetStreamedMessages(list: List[String]) extends SuccessResponse
}

class RandomMessages(implicit timeout: Timeout) extends Actor {
  import RandomMessages._

  val listOfMessages: ListBuffer[String] = new ListBuffer[String]
  val listRevercedMessages: ListBuffer[String] = new ListBuffer[String]

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec = context.dispatcher

  def receive = {
    case GetStreamedMessages => {
      val stream: Source[List[String], NotUsed] = Source(listOfMessages.toList :: Nil)
      stream.runWith(Sink.foreach(f => f.foreach(str => listRevercedMessages.append(str.reverse))))
      if(!listRevercedMessages.isEmpty) sender() ! GetStreamedMessages(listRevercedMessages.toList)
      else  sender() ! GetStreamedMessages(List("Work in progress"))
    }

    case GetListOfMessages => {
      sender() ! ListOfMessages(listOfMessages.toList)
    }

    case SaveNewMessage(string) =>
      if (string.isEmpty) sender() ! "Message is empty"
      else listOfMessages.append(string)
      println(s"Added ${string} message")
      sender ! MessageSaved(s"Message ${string} was added")
  }

}