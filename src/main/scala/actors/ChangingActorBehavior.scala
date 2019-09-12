package actors

import akka.actor.Actor

object ChangingActorBehavior extends App{

  object FussyKid{
    case object KidAccept
    case object KidReject
  }

  class FussyKid extends Actor {
    override def receive: Receive = {}
  }

    object  Mom {
      case class Food(food: String)
      case class Ask(message: String)
    }
    class Mom extends Actor{
      override def receive: Receive = ???
    }

}