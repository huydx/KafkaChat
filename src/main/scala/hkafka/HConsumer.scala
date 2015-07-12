package hkafka

import java.nio.charset.StandardCharsets
import java.util.Properties

import kafka.consumer.{Whitelist, Consumer, ConsumerConfig}
import kafka.serializer.DefaultDecoder

class HConsumer(topic: String, groupId: String, zkHost: String) {

  val consumerProps = new Properties()
  consumerProps.put("group.id", groupId)
  consumerProps.put("auto.offset.reset", "smallest")
  consumerProps.put("zookeeper.connect", zkHost)

  val config = new ConsumerConfig(consumerProps)
  val connector = Consumer.create(config)

  def consume(callBack: String => Unit) = {
    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run() {
        connector.shutdown()
      }
    })

    val filterSpec = new Whitelist(topic)
    val stream = connector.createMessageStreamsByFilter(filterSpec, 1, new DefaultDecoder(), new DefaultDecoder()).head
    val iter = stream
    for (messageAndTopic <- iter) {
      val str = new String(messageAndTopic.message, StandardCharsets.UTF_8);
      callBack(str)
    }
  }
}
