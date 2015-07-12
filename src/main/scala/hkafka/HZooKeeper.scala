package hkafka

import kafka.utils.{ZKStringSerializer, ZKGroupTopicDirs, ZkUtils}
import org.I0Itec.zkclient.ZkClient
import org.apache.kafka.common.KafkaException
import scala.util.parsing.json.{JSONObject, JSON, JSONType}


class HZooKeeper(zkHost: String) {
  //メモ： SerializerがないとExceptionで死んでしまう >.<
  val zkClient = new ZkClient(zkHost, 1000, 1000, ZKStringSerializer)

  def getPartions(topic: String): Map[String, Seq[Int]] = {
    ZkUtils.getPartitionsForTopics(zkClient, Seq(topic)).toMap
  }

  def getGroups: Seq[String] = {
    ZkUtils.getChildren(zkClient, ZkUtils.ConsumersPath).toList
  }

  def getTopicLists(consumerGroup: String): List[String] = {
    ZkUtils.getChildren(zkClient, "/consumers/%s/offsets".format(consumerGroup)).toList
  }

  def  getBrokeridPartition(consumerGroup: String, topic: String): List[String] = {
    ZkUtils.getChildrenParentMayNotExist(zkClient, "/consumers/%s/offsets/%s".format(consumerGroup, topic)).toList
  }

  def getOffset(bidPidList: List[String], consumerGroup: String, topic: String) = {
    for (bidPid <- bidPidList) {
      val zkGrpTpDir = new ZKGroupTopicDirs(consumerGroup,topic)
      val offsetPath = zkGrpTpDir.consumerOffsetDir + "/" + bidPid

      ZkUtils.readDataMaybeNull(zkClient, offsetPath)._1 match {
        case Some(offsetVal) =>
          offsetVal
        case None =>
          Nil
      }
    }
  }

  def setToDesireOffset(groupId: String, topic: String, partition: Int, approximateMessagesBack: Long) = {
    val path = s"${ZkUtils.ConsumersPath}/${groupId}/offsets/${topic}/${partition}"
    if (zkClient.exists(path)) {
      val currentOffset = zkClient.readData[Any](path)
      val desiredOffset = math.max(0, (currentOffset.toString.toLong - approximateMessagesBack))
      zkClient.writeData(path, desiredOffset.toString)
    } else {
      throw new RuntimeException(
        s"""
          | Unable to find the move the consumer back
          | in ZK.  This may or may not be an issue, depending on whether you expect
          | the path to exist. Path: " + ${path})
        """.stripMargin
      )
    }
  }

  def getBrokerLists: List[String] = {
    val listIds = ZkUtils.getChildren(zkClient, "/brokers/ids").toList
    listIds.map { id =>
      val path = s"/brokers/ids/${id}"
      if (zkClient.exists(path)) {
        val result = JSON.parseRaw(ZkUtils.readData(zkClient, path)._1)
        val jo = result.get.asInstanceOf[JSONObject]
        val map = jo.obj.asInstanceOf[Map[String, String]]
        val (host, port) = (map.get("host"), map.get("port"))
        s"${host.get}:${port.get}".stripSuffix(".0")
      } else {
        throw new KafkaException("Broker with ID ${brokerId} doesn't exist")
      }
    }
  }
}
