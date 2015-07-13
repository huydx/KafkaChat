package chat

import hkafka.{HZooKeeper, HConsumer}
import joptsimple.{OptionSet}

object RoomConsole extends ChatArgument {
  def main(args: Array[String]) = {

    val options: OptionSet = tryParse(parser, args)
    val room   =  options.valueOf(chatRoom)
    val uname  =  options.valueOf(userName)
    val host   =  options.valueOf(server)

    val consumer = new HConsumer(room , uname, host)
    val hZooKeeper  = new HZooKeeper(host)
    try {
      val partition = hZooKeeper.getPartions(room).values.head
      hZooKeeper.setToDesireOffset(
        uname,
        room,
        partition.head,
        10L
      )
    } catch {
      case e: Exception => //オフセットが１０以下の場合オフセットを戻せない場合がある
    }
    consumer.consume(printOut)
  }

  def printOut(message: String) = {
    Console.out.print(s"$message \n")
  }
}
