package hkafka

import java.util.Properties

import kafka.producer.{KeyedMessage, Producer => KafkaProducer, ProducerConfig}

case class HProducer(zkHost: String ,topic: String, messagePrefix: String, brokers: List[String]) {
  val props = new Properties
  props.put("zookeeper.connect", s"${zkHost}")
  props.put("serializer.class", "kafka.serializer.StringEncoder")
  props.put("metadata.broker.list", brokers.mkString(","))

  protected val config = new ProducerConfig(props)

  private lazy val producer = new KafkaProducer[String, String](config)

  def send(message: String) = {
    val key = new KeyedMessage[String, String](topic, s"$messagePrefix $message")
    producer.send(key)
  }

}
