package actors

import actors.ActorCapabilities.Person.LiveThelife
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorCapabilities extends App {

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case "Hi!" => context.sender() ! "Hello, there!"
      case message: String => println(s" ${self} i have recieve $message")
      case number: Int => println(s"SImple actor I have recieverd a NUMBER: $number")
      case SpecialMessage(contents) => println(s"sa have rec $contents")
      case SendMessageToYourself(content) => self ! content
      case SayHiTo(ref) => ref ! "Hi"
    }
  }

  val system = ActorSystem("actorCapabilitesDemo")
  val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")

  simpleActor ! "hello, actor"
  simpleActor ! 42

  case class SpecialMessage(value: String)

  simpleActor ! SpecialMessage("someSpecialContent")

  case class SendMessageToYourself(content: String)

  simpleActor ! SendMessageToYourself("Im a actor i'm proud of it ")

  val alice = system.actorOf(Props[SimpleActor], "alice")
  val bob = system.actorOf(Props[SimpleActor], "bob")

  case class SayHiTo(ref: ActorRef)

  alice ! SayHiTo(bob)

  alice ! "Hi"

  case class WirelessPhoneMessage(content: String, ref: ActorRef)

  alice ! WirelessPhoneMessage("Hi", bob)

  /**
   * Exercises
   *
   * 1. a Counter actor
   *  - Increment
   *  - Decrement
   *  - Print
   *
   *  2. a Bank account as an actor
   * receives
   *  - Deposit an amount
   *  - Withdraw an amount
   *  - Statement
   * repilies with
   *  - Success
   *  - Failure
   *
   * interact with some other kind of actor
   */
  object Bank {

    case class Deposit(amount: Int)

    case class Withdraw(amount: Int)

    case object Statement

    case class TransactionSuccess(message: String)

    case class TransactionFailure(message: String)

  }

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

  class BankAccount extends Actor {

    import Bank._

    var funds = 0

    override def receive: Receive = {
      case Deposit(amount: Int) =>
        if (amount < 0) sender() ! TransactionFailure("invalid deposit amount")
        else {
          sender() ! TransactionSuccess("successfull deposit amount")
          funds += amount
        }
      case Withdraw(amount: Int) => {
        if (amount > funds) sender() ! TransactionFailure("you have not enough money")
        else {
          sender() ! TransactionSuccess("successfull deposit amount")
          funds -= amount
        }
      }
      case Statement => sender() ! s"Your balance is $funds"

    }
  }

  object Person {

    case class LiveThelife(account: ActorRef)

  }

  class Person extends Actor {

    import Person._
    import Bank._

    override def receive: Receive = {
      case LiveThelife(account) =>
        account ! Deposit(10000)
        account ! Withdraw(90000)
        account ! Withdraw(500)
        account ! Statement
      case message => println(message.toString)

    }

  }

  val account = system.actorOf(Props[BankAccount], "bankAccount")
  val person = system.actorOf(Props[Person], "bankAccount")


  person ! LiveThelife(account)
}
