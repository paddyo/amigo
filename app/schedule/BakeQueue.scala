package schedule

import java.util.concurrent.ConcurrentLinkedQueue

import data.{ Bakes, Dynamo }
import models.BakeStatus
import org.quartz.SimpleScheduleBuilder
import packer.PackerJob
import services.Loggable

import scala.util.Try

object BakeQueue {
  val bakeQueue = new ConcurrentLinkedQueue[PackerJob]()
}

class BakeQueue(implicit val dynamo: Dynamo) extends ScheduledJob with Loggable {
  override val schedule: SimpleScheduleBuilder = SimpleScheduleBuilder.repeatSecondlyForever(60)

  val MAX_CONCURRENT_BAKES = 3

  override def scheduleAction(): Unit = {
    val currentBakes = Bakes.scanForAll().count(_.status == BakeStatus.Running)
    val bakesToLaunch = MAX_CONCURRENT_BAKES - currentBakes
    for (_ <- 1 to bakesToLaunch) {
      Try(BakeQueue.bakeQueue.poll().execute())
    }
  }
}