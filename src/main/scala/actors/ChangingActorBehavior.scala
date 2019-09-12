package actors

import actors.ActorCapabilities.{Bank, system}
import actors.ChangingActorBehavior.Counter.{Decrement, Increment, Print, start}
import actors.ChangingActorBehavior.FussyKid.{KidAccept, KidReject}
import actors.ChangingActorBehavior.Mom.MomStart
import akka.actor.{Actor, ActorContext, ActorRef, ActorSystem, Props}

object ChangingActorBehavior extends App {

  object FussyKid {

    case object KidAccept

    case object KidReject

    val HAPPY = "happy"
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
        if (state == HAPPY) sender() ! KidAccept
        else sender() ! KidReject
    }
  }

  object Mom {

    case class MomStart(kidRef: ActorRef)

    case class Food(food: String)

    case class Ask(message: String)

    val VEGETABLE = "vegetable"
    val CHOCOLATE = "chocolate"

  }

  class Mom extends Actor {

    import Mom._

    override def receive: Receive = {
      case MomStart(kidRef) =>
        kidRef ! Food(VEGETABLE)
        kidRef ! Ask("do you want to play?")
      case KidAccept => println("yay my kid is happy")
      case KidReject => println("my kid life but he is sad")
    }
  }

  class StatelessFussyKid extends Actor {

    import FussyKid._
    import Mom._

    override def receive: Receive = ???

    def happyRecieve: Receive = {
      case Food(VEGETABLE) => context.become(sadRecieve, false)
      case Food(CHOCOLATE) =>
      case Ask(_) => sender() ! KidAccept
    }

    def sadRecieve: Receive = {
      case Food(VEGETABLE) => context.unbecome()
      case Food(CHOCOLATE) => context.become(happyRecieve, false)
      case Ask(_) => sender() ! KidReject
    }
  }

  val system = ActorSystem("changingActorBehaviorDemo")
  val fussyKid = system.actorOf(Props[FussyKid])
  val statelessFussyKid = system.actorOf(Props[StatelessFussyKid])
  val mom = system.actorOf(Props[Mom])


  mom ! MomStart(statelessFussyKid)

  /**
   * Exercisies
   * 1- recreate the Counter Actor with context.become and no MUTABLE STATE
   */
  object Counter {

    case class Increment(number: Int = 1)

    case class Decrement(number: Int = 1)

    case object Print

    case class start(number: Int)

  }

  class Counter extends Actor {

    import Counter._
    import Bank._


    def math(number: Int): Receive = {
      case Increment => context.become(math(number + 1))
      case Decrement =>
      case Increment => context.become(math(number - 1))
      case Print => println(s"SImple actor I have recieverd a NUMBER: $number")
    }

    override def receive: Receive = {
      case start(number: Int) => context.become(math(number))
    }
  }

  val counter = system.actorOf(Props[Counter], "myCounter")
  counter ! start(5)
  counter ! Increment
  counter ! Decrement(2)
  counter ! Increment(7)
  counter ! Print

  /**
   * Exercises 2 - a simplified voting system
   */

  case class Vote(candidate: String)

  case object VoteStatusRequest

  case class VoteStatusReply(candidate: Option[String])

  var candidate: Option[String] = None

  class Citizen extends Actor {
    override def receive: Receive = {
      case Vote(c) => context.become(voted(c))
      case VoteStatusRequest => sender() ! VoteStatusReply(candidate)
    }
    def voted(candidate: String) : Receive = {
      case VoteStatusRequest => sender() ! VoteStatusReply(Some(candidate))
    }

  }

  case class AggregateVotes(citizens: Set[ActorRef])

  class VoteAgreggator extends Actor {

    override def receive: Receive = awaitingCommand



    def awaitingCommand : Receive ={
      case AggregateVotes(citizens) =>
        citizens.foreach(citizensRef => citizensRef ! VoteStatusRequest)
        context.become(awaitingStatuses(citizens,Map()))
    }
    def awaitingStatuses(stillWaiting: Set[ActorRef],currentStats:Map[String,Int]): Receive ={
      case VoteStatusReply(Some(candidate)) =>
        val newStillWaiting = stillWaiting - sender()
        val currentVotesOfCandidate = currentStats.getOrElse(candidate,0)
        val newStats = currentStats +(candidate -> (currentVotesOfCandidate+1))
        if(newStillWaiting.isEmpty) {
          println(s"$self poll stats: $currentStats")
        }
       context.become(awaitingStatuses(newStillWaiting,newStats))

    }
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
  voteAgreggator ! AggregateVotes(Set(alice, bob, charlie, daniel))

  /*
  print the status of the votes
  Martin -> 1
  Jonas ->1
  Roland ->2

   */
}