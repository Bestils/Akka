//package actors
//
//import akka.actor.{Actor, ActorRef, ActorSystem, Props}
//
//class ChildActors extends App{
//object  Parent {
//  case class CreateChild(name: String)
//  case class TellChild(message:String)
//
//}
//  class Parent extends Actor {
//    import Parent._
//
//    override def receive: Receive = {
//      case CreateChild(name) =>
//        println(s"${self.path} creating child")
//        val childRef = context.actorOf(Props[Child],name)
//context.become(readyChild(childRef))
//
//    }
//    def readyChild(child : ActorRef) : Receive ={
//      case TellChild(message)=> {
//        if (child != null)  child forward message
//    }
//    }
//
//    class Child extends Actor{
//      override def receive: Receive = {
//        case message => println(s"${self.path} i got : $message")
//      }
//    }
//
//
//
//  }
//  import Parent._
//  val system = ActorSystem("ParentChildDemo")
//  val parent = system.actorOf(Props[Parent],"parent")
//  parent ! CreateChild("child")
//  parent ! TellChild("wake up you little dipshit")
//  object NaiveBankAccount {
//    case class Deposit(amount:Int)
//    case class Withdraw ( amount: Int)
//    case object InitializeAccount
//  }
//  class NaiveBankAccount extends Actor{
//    import  NaiveBankAccount._
//    import CreditCard._
//
//    override def receive: Receive = {
//      case InitializeAccount =>
//        val creditCardRef = context.actorOf(Props[CreditCard],"card")
//        creditCardRef ! AttachToAccount(this)
//      case Deposit(funds) => deposit(funds)
//      case Withdraw(funds) => withdraw(funds)
//    }
//    def deposit(i: Int) = {
//      println(s"${self.path} depositing $i on top of $amount")
//    }
//    def withdraw(i: Int) = {
//      println(s"${self.path} depositing $i on top of $amount")
//    }
//
//
//
//  }
//  object CreditCard {
//    case class  AttachToAccount(bankAccount: ActorRef)
//    case object CheckStatus
//  }
//  class CreditCard extends Actor{
//    import  CreditCard._
//    override def receive: Receive = {
//      case AttachToAccount(account) => context.become(attachedTo(account))
//    }
//    def attachedTo(account: NaiveBankAccount) : Receive = {
//      case CheckStatus => println(s"$self your msg has been procsd")
//    }
//  }
//  import NaiveBankAccount._
//  import CreditCard._
//
//  val bankAccountRef = system.actorOf(Props[NaiveBankAccount])
//  bankAccountRef ! InitializeAccount
//bankAccountRef ! Deposit(100)
//
//  val ccSelection = system.actorSelection("/user/account/card")
//  ccSelection ! CheckStatus
//}
