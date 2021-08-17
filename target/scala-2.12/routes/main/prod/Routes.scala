// @GENERATOR:play-routes-compiler
// @SOURCE:/Users/chetanpardeep/IdeaProjects/admin-capmovie-frontend/conf/prod.routes
// @DATE:Tue Aug 17 08:50:55 BST 2021

package prod

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:2
  app_Routes_1: app.Routes,
  // @LINE:3
  health_Routes_0: health.Routes,
  // @LINE:5
  MetricsController_0: com.kenshoo.play.metrics.MetricsController,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:2
    app_Routes_1: app.Routes,
    // @LINE:3
    health_Routes_0: health.Routes,
    // @LINE:5
    MetricsController_0: com.kenshoo.play.metrics.MetricsController
  ) = this(errorHandler, app_Routes_1, health_Routes_0, MetricsController_0, "/")

  def withPrefix(addPrefix: String): Routes = {
    val prefix = play.api.routing.Router.concatPrefix(addPrefix, this.prefix)
    prod.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, app_Routes_1, health_Routes_0, MetricsController_0, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    prefixed_app_Routes_1_0.router.documentation,
    prefixed_health_Routes_0_1.router.documentation,
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """admin/metrics""", """com.kenshoo.play.metrics.MetricsController.metrics"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:2
  private[this] val prefixed_app_Routes_1_0 = Include(app_Routes_1.withPrefix(this.prefix + (if (this.prefix.endsWith("/")) "" else "/") + "registration"))

  // @LINE:3
  private[this] val prefixed_health_Routes_0_1 = Include(health_Routes_0.withPrefix(this.prefix + (if (this.prefix.endsWith("/")) "" else "/") + ""))

  // @LINE:5
  private[this] lazy val com_kenshoo_play_metrics_MetricsController_metrics2_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("admin/metrics")))
  )
  private[this] lazy val com_kenshoo_play_metrics_MetricsController_metrics2_invoker = createInvoker(
    MetricsController_0.metrics,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "prod",
      "com.kenshoo.play.metrics.MetricsController",
      "metrics",
      Nil,
      "GET",
      this.prefix + """admin/metrics""",
      """""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:2
    case prefixed_app_Routes_1_0(handler) => handler
  
    // @LINE:3
    case prefixed_health_Routes_0_1(handler) => handler
  
    // @LINE:5
    case com_kenshoo_play_metrics_MetricsController_metrics2_route(params@_) =>
      call { 
        com_kenshoo_play_metrics_MetricsController_metrics2_invoker.call(MetricsController_0.metrics)
      }
  }
}
