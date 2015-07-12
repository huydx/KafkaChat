package chat

import hkafka.HProducer
import jline.console.ConsoleReader
import joptsimple._

import hkafka.{HZooKeeper}

object ChatConsole extends ChatArgument {

  def main(args: Array[String]) = {

    val options: OptionSet = tryParse(parser, args)
    val room   =  options.valueOf(chatRoom)
    val uname  =  options.valueOf(userName)
    val host   =  options.valueOf(server)

    val hZooKeeper  = new HZooKeeper(host)
    val brokers = hZooKeeper.getBrokerLists
    val producer = new HProducer(host, room, uname, brokers)

    val reader = new ConsoleReader()
    reader.setPrompt(s"${options.valueOf(userName)}: ")

    var line: String = null

    while ((line = reader.readLine()) != null) {
      producer.send(line)
    }
  }

}
