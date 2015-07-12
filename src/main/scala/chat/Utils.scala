package chat

import joptsimple.{OptionException, OptionParser}

trait ChatArgument {
  val parser = new OptionParser()

  val userName = parser.accepts("name", "[required] user name")
                    .withRequiredArg
                    .describedAs("user name")
                    .ofType(classOf[String])

  val chatRoom = parser.accepts("room", "[required] chat room name")
                    .withRequiredArg
                    .describedAs("chat room")
                    .ofType(classOf[String])

  val server = parser.accepts("server", "[required] server ip address/port")
                    .withRequiredArg
                    .describedAs("server information")
                    .ofType(classOf[String])

  def tryParse(parser: OptionParser, args: Array[String]) = {
    try {
      parser.parse(args : _*)
    } catch {
      case e: OptionException => {
        null
      }
    }
  }
}
