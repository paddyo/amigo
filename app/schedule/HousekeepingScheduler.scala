package schedule

import org.quartz.JobBuilder._
import org.quartz.TriggerBuilder._
import org.quartz.{ JobDataMap, Scheduler }

class HousekeepingScheduler(scheduler: Scheduler, housekeepingJobs: List[ScheduledJob]) {

  def initialise(): Unit = {
    housekeepingJobs.foreach { housekeepingJob =>
      val jobDetail = newJob(classOf[HousekeepingJobWrapper])
        .withIdentity(housekeepingJob.jobKey)
        .usingJobData(buildJobDataMap(housekeepingJob))
        .build()
      val trigger = newTrigger()
        .withIdentity(housekeepingJob.triggerKey)
        .withSchedule(housekeepingJob.schedule)
        .build()
      scheduler.scheduleJob(jobDetail, trigger)
    }
  }

  private def buildJobDataMap(housekeepingJob: ScheduledJob): JobDataMap = {
    val map = new JobDataMap()
    map.put(HousekeepingScheduler.HousekeepingJobInstance, housekeepingJob)
    map
  }
}

object HousekeepingScheduler {
  val HousekeepingJobInstance = "HousekeepingJobInstance"
}