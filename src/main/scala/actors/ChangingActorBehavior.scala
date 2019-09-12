package actors

import actors.ActorCapabilities.{Bank, system}
import actors.ChangingActorBehavior.FussyKid.{KidAccept, KidReject}
import actors.ChangingActorBehavior.Mom.MomStart
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ChangingActorBehavior extends App{

  object FussyKid{
    case object KidAccept
    case object KidReject
    val HAPPY ="happy"
    val SAD = "sad"
  }

  class FussyKid extends Actor {
    import FussyKid._
    import Mom._

    var state = HAPPY
    override def receive: Receive = {
      case Food(VEGETABLE) => state = SAD
      case Food(CHOCOLATE) => state = HAPPY
      case Ask(message) =>
        if(state ==HAPPY) sender() ! KidAccept
        else sender() ! KidReject
    }
  }

    object  Mom {
      case class MomStart(kidRef:ActorRef)
      case class Food(food: String)
      case class Ask(message: String)
    val VEGETABLE = "vegetable"
      val CHOCOLATE= "chocolate"

    }
    class Mom extends Actor{
import Mom._
      override def receive: Receive = {
        case MomStart(kidRef) =>
          kidRef ! Food(VEGETABLE)
          kidRef ! Ask("do you want to play?")
        case KidAccept => println("yay my kid is happy")
        case KidReject => println("my kid life but he is sad")
      }
    }
  class StatelessFussyKid extends  Actor{
    import  FussyKid._
    import Mom._

    override def receive: Receive = ???

    def happyRecieve: Receive = {
      case Food(VEGETABLE) => context.become(sadRecieve,false)
      case Food(CHOCOLATE) =>
      case Ask(_) => sender() ! KidAccept}

    def sadRecieve: Receive = {
      case Food(VEGETABLE) =>context.unbecome()
      case Food(CHOCOLATE) =>context.become(happyRecieve,false  )
      case Ask(_) => sender() ! KidReject
    }
  }

val system =ActorSystem("changingActorBehaviorDemo")
  val fussyKid = system.actorOf(Props[FussyKid])
  val statelessFussyKid = system.actorOf(Props[StatelessFussyKid])
  val mom = system.actorOf(Props[Mom])


  mom ! MomStart(statelessFussyKid)

  /**
   * Exercisies
   *  1- recreate the Counter Actor with context.become and no MUTABLE STATE
   */
  object Counter {

    case object Increment

    case object Decrement

    case object Print

  }

  class Counter extends Actor {

    import Counter._
    import Bank._

    var count = 0

    override def receive: Receive = {
      case Increment => context.sender() ! count + 1
      case Decrement => context.sender() ! count - 1
      case Print => println(s"SImple actor I have recieverd a NUMBER: $count")
    }
  }

  val counter = system.actorOf(Props[Counter], "myCounter")

  /**
   * Exercises 2 - a simplified voting system
   */

  case class Vote(candidate: String)
  case object VoteStatusRequest
  case class VoteStatusReply(candidate: Option[String])
  class Citizen extends Actor{
    override def receive: Receive = ???
  }
  case class AgregateVotes(citizens:Set[ActorRef])

  class VoteAgreggator extends Actor {
    override def receive: Receive = ???
  }
  val alice = system.actorOf(Props[Citizen])
  val bob = system.actorOf(Props[Citizen])
  val charlie = system.actorOf(Props[Citizen])
  val daniel = system.actorOf(Props[Citizen])

  alice ! Vote("Martin")
  bob ! Vote("Martin")
  charlie ! Vote("Martin")
  daniel ! Vote("Martin")

  val voteAgreggator = system.actorOf(Props[VoteAgreggator])
  voteAgreggator !AgregateVotes(Set(alice,bob,charlie,daniel))
}