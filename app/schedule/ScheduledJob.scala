package schedule

import org.quartz.{JobKey, ScheduleBuilder, Trigger, TriggerKey}

trait ScheduledJob {
  val schedule: ScheduleBuilder[_ <: Trigger]

  val name: String = getClass.getName

  val jobKey = new JobKey(name)
  val triggerKey = new TriggerKey(name)

  def scheduleAction(): Unit
}
