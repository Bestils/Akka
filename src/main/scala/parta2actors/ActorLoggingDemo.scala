package parta2actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging

object ActorLoggingDemo extends App {

class SimpleActorWithExplicitLogger extends Actor {
  // # 1 explicit logging
  val logger = Logging(context.system,this)
  override def receive: Receive = {

    /**
     * 1 debug
     * 2 info
     * 3 warn
     * 4 error
     * */
    case message => logger.info(message.toString)//LOGIT

  }
}
  val system =ActorSystem("LoggingDemo")
  val actor = system.actorOf(Props[SimpleActorWithExplicitLogger])

  actor ! "Logging a simple message "
  // #2 actorLogging
  class ActorWithLogging extends  Actor with ActorLogging{
    override def receive: Receive = {
      case (a,b) => log.info("Two things {} and {}",a,b)
      case message => log.info(message.toString)
    }
  }
  val actorLogged = system.actorOf(Props[SimpleActorWithExplicitLogger])
  actorLogged ! "Logging a simple message by trait "
}
