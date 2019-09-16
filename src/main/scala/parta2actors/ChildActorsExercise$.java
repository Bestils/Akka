package parta2actors;

object ChildActorsExercise extends App{
  // Distibuted Word counting

  object WordCounterMaster{
    case class Initialize(nChildren: Int)
    case class WordCountTask(id:Int,text:String)
    case class WordCountReplay(id:Int,count:Int)
  }
  class WordCounterMaster extends Actor {
    import WordCounterMaster._
    override def receive: Receive = {

      case Initialize(num:Int) =>
        println(s"${sender.path.name}  initialazing....")
        val childrenRefs = for(i <- 1 to num) yield context.actorOf(Props[WordCounterWorker],s"wcw_$i")
      context.become(withChildren(childrenRefs,0,0,Map()))
    }
    def withChildren(childrenRefs:Seq[ActorRef],currentChildIndex : Int,currentTaskId:Int,requestMap : Map[Int,ActorRef]) : Receive={
      case text: String =>
        println(s"${sender.path.name} i have recived $text - i will send it to to child $currentChildIndex")
        val originalSender = sender()
        val task = WordCountTask(currentTaskId,text)
        val childRef = childrenRefs(currentChildIndex)
        childRef ! task
        val nextChildIndex = (currentChildIndex + 1) % childrenRefs.length
        val newTaskId = currentTaskId + 1
        val newRequestMap = requestMap + (currentTaskId -> originalSender)
        context.become(withChildren(childrenRefs, nextChildIndex,newTaskId,newRequestMap))
      case WordCountReplay(id,count) =>
        println(s"${sender.path.name}  ii have recived a replay for tssk $id with $count....")
val originalSender = requestMap(id)
        originalSender ! count
        context.become(withChildren(childrenRefs,currentChildIndex,currentTaskId,requestMap-id))
    }
  }
  class WordCounterWorker extends Actor {
    override def receive: Receive = {
      case WordCountTask(id,text:String) =>
        println(s"${sender.path}  i have recived task $id with $text....")
        sender ! WordCountReplay(id,text.split(" ").length)


    }
  }
  class TestActor extends Actor {
    import WordCounterMaster._
    override def receive: Receive =
      {
        case "go" =>
          val master = context.actorOf(Props[WordCounterMaster],"master")
           master ! Initialize(3)
          val text = List("I love scala", "Akka is super dope", "tea","me fucking no too"," i hate modern feminism")
          text.foreach(text => master ! text)
        case count : Int =>       println(s"${sender.path.name}  i recived a replay :$count....")
      }
  }
  val system = ActorSystem("roundRobinWordConut")
  val testActor = system.actorOf(Props[TestActor],"testactor")
  testActor ! "go"

  }
