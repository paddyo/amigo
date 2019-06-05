package schedule

import org.quartz.{ DisallowConcurrentExecution, Job, JobDataMap, JobExecutionContext }

/** Quartz job wrapper for [[schedule.ScheduledBakeRunner]] */
@DisallowConcurrentExecution
class HousekeepingJobWrapper extends Job {

  private def getAs[T](key: String)(implicit jobDataMap: JobDataMap): T = jobDataMap.get(key).asInstanceOf[T]

  override def execute(context: JobExecutionContext): Unit = {
    implicit val jobDataMap: JobDataMap = context.getJobDetail.getJobDataMap

    val housekeepingJob = getAs[ScheduledJob](HousekeepingScheduler.HousekeepingJobInstance)

    housekeepingJob.scheduleAction()
  }

}

