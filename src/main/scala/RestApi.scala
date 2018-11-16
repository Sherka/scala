import java.util.concurrent.Future

import akka.Done
import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model._
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.util.Timeout
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol

import scala.concurrent.ExecutionContext

class RestApi(system: ActorSystem, timeout: Timeout) extends RandomMessagesApi with DefaultJsonProtocol{

  implicit val requestTimeout = timeout
  implicit val executionContext = system.dispatcher

  import RandomMessages._
  implicit val listOfMessagesFormat = jsonFormat1(ListOfMessages)
  implicit val messageSavedFormat = jsonFormat1(MessageSaved)
  implicit val saveNewMessageFormat = jsonFormat1(SaveNewMessage)

  def createRandomMessages = system.actorOf(RandomMessages.props, "RandomMessages")
  def routes: server.Route = getAllMsgs ~ saveNewMsg

  def getAllMsgs = {
    path("get-messages") {
      get {
        //GET /get-messages
        onSuccess(getListMessages()) { list =>
            complete(list)
        }
        }
      }
    }

  def saveNewMsg = {
     post {
       path("save-message") {
         //POST /save-message
          entity(as[SaveNewMessage]) { nm =>
            onSuccess(saveNewMessage(nm.msg)) {
              case RandomMessages.MessageSaved(str) => complete(str)
            }
          }
        }
     }
  }
}

trait RandomMessagesApi {
  import RandomMessages._
  def createRandomMessages(): ActorRef
  implicit def executionContext: ExecutionContext
  implicit def requestTimeout: Timeout

  lazy val randomMessages = createRandomMessages()

  def getListMessages() = {
    randomMessages.ask(GetListOfMessages).mapTo[ListOfMessages]
  }

  def saveNewMessage(msg: String) = {
    randomMessages.ask(SaveNewMessage(msg)).mapTo[MessageSaved]
  }

}