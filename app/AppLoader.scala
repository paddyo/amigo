import data.Recipes
import play.api.libs.logback.LogbackLoggerConfigurator
import play.api.{ Logger, Application, ApplicationLoader }
import components.AppComponents

import play.api.ApplicationLoader.Context

import scala.concurrent.Future

class AppLoader extends ApplicationLoader {
  override def load(context: Context): Application = {
    new LogbackLoggerConfigurator().configure(context.environment)
    val components = new AppComponents(context)

    Logger.info("Registering all scheduled bakes and starting the scheduler")
    components.bakeScheduler.start(Recipes.list()(components.dynamo))

    components.applicationLifecycle.addStopHook { () =>
      println("Shutting down scheduler")
      Future.successful(components.bakeScheduler.shutdown())
    }

    components.application
  }

}
