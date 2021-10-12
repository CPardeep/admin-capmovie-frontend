/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.capmovie.controllers

import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.capmovie.connectors.UpdateConnector
import uk.gov.hmrc.capmovie.controllers.predicates.Login
import uk.gov.hmrc.capmovie.models.MovieRegPlot
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.MoviePlot
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MoviePlotController @Inject()(repo: SessionRepo,
                                    mcc: MessagesControllerComponents,
                                    plotPage: MoviePlot,
                                    login: Login,
                                    connector: UpdateConnector)
  extends FrontendController(mcc) {

  def getMoviePlot(isSessionUpdate: Boolean): Action[AnyContent] = Action async { implicit request =>
    login.check { id =>
      repo.readOne(id).map { x =>
        val form = MovieRegPlot.form.fill(MovieRegPlot(x.get.plot.getOrElse("")))
        Ok(plotPage(form, isSessionUpdate, isUpdate = false, ""))
      }
    }
  }

  def submitMoviePlot(isSessionUpdate: Boolean): Action[AnyContent] = Action async { implicit request =>
    login.check {
      id =>
        MovieRegPlot.form.bindFromRequest().fold({
          formWithErrors => Future(BadRequest(plotPage(formWithErrors, isSessionUpdate = false, isUpdate = false, "")))
        }, { formData =>
          for {
            same <- repo.readOne(id).map { x => x.get.plot.contains(formData.plot) }
            added <- repo.addPlot(id, formData.plot)

          } yield (same, added) match {
            case (true, false) => Redirect(routes.MovieSummaryController.getSummary(isSessionUpdate = true))
            case (false, true) => if (isSessionUpdate) Redirect(routes.MovieSummaryController.getSummary(isSessionUpdate = true)) else Redirect(routes.MoviePosterController.getMoviePoster(false))
            case _ => InternalServerError
          }
        }
        )
    }
  }

  def getUpdatePlot(id: String): Action[AnyContent] = Action async { implicit request =>
    login.check { _ =>
      connector.readOne(id).flatMap { x =>
        val form = MovieRegPlot.form.fill(MovieRegPlot(x.get.movie.plot))
        Future.successful(Ok(plotPage(form, isSessionUpdate = false, isUpdate = true, id)))
      }
    }
  }

  def updateMoviePlot(id: String): Action[AnyContent] = Action async { implicit request =>
    login.check { _ =>
      MovieRegPlot.form.bindFromRequest().fold({
        formWithErrors => Future(BadRequest(plotPage(formWithErrors, isSessionUpdate = false, isUpdate = true, id)))
      }, { formData =>
        for {
          same <- connector.readOne(id).map { x => x.get.movie.plot.contains(formData.plot) }
          updated <- connector.updatePlot(id, formData.plot)
        } yield (same, updated) match {
          case (true, false) | (false, true) => Redirect(routes.MoviePosterController.getUpdatePoster(id))
          case _ => InternalServerError
        }
      })
    }
  }

}
