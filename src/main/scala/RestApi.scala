import akka.actor.{ActorRef, ActorSystem}
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
  implicit val getStreamedMessagesFormat = jsonFormat1(GetStreamedMessages)

  def createRandomMessages = system.actorOf(RandomMessages.props, "RandomMessages")
  def routes: server.Route = getAllMsgs ~ saveNewMsg ~ getReverce

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

  def getReverce = {
    get {
      path("get-reverce") {
        //GET /get-reverce
        onSuccess(getRevercedMessages()) {
          list => complete(list)
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
              case RandomMessages.MessageSaved(str) => complete(MessageSaved(str))
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

  def getRevercedMessages() = {
    randomMessages.ask(GetStreamedMessages).mapTo[GetStreamedMessages]
  }

}