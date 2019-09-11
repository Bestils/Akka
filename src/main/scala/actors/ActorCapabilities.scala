package actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorCapabilities extends App {

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case "Hi!" => context.sender() ! "Hello, there!"
      case message: String => println(s" ${self} i have recieve $message")
      case number: Int => println(s"SImple actor I have recieverd a NUMBER: $number")
      case SpecialMessage(contents) => println(s"sa have rec $contents")
      case SendMessageToYourself(content) =>  self ! content
      case SayHiTo(ref) => ref ! "Hi"
    }
  }

  val system = ActorSystem("actorCapabilitesDemo")
  val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")

  simpleActor ! "hello, actor"
  simpleActor ! 42

  case class SpecialMessage(value: String)

  simpleActor ! SpecialMessage("someSpecialContent")

case class SendMessageToYourself(content : String)
  simpleActor ! SendMessageToYourself("Im a actor i'm proud of it ")

  val alice = system.actorOf(Props[SimpleActor],"alice")
  val bob = system.actorOf(Props[SimpleActor],"bob")

case class SayHiTo(ref:ActorRef)
alice ! SayHiTo(bob)

}
