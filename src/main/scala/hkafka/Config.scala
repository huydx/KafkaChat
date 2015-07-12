package hkafka

import java.util.Properties

import com.typesafe.config.ConfigFactory
import HProducerConfig._


trait HProducerConfig extends Properties {

  private val producerPrefixWithDot = producerPrefix + "."
  private val allKeys = Seq(zookeeperConnect, brokers, serializer)

  lazy val typesafeConfig = ConfigFactory.load()

  allKeys.map { key =>
    if (typesafeConfig.hasPath(key))
      put(key.replace(producerPrefixWithDot, ""), typesafeConfig.getString(key))
  }

  def getCustomString(key: String) = typesafeConfig.getString(key)
  def getCustomInt(key: String) = typesafeConfig.getInt(key)
}

object HProducerConfig {

  val producerPrefix = "producer"
  val zookeeperConnect = s"$producerPrefix.zookeeper.connect"
  val brokers = s"$producerPrefix.metadata.broker.list"
  val serializer = s"$producerPrefix.serializer.class"

  def apply() = new HProducerConfig {}
}
