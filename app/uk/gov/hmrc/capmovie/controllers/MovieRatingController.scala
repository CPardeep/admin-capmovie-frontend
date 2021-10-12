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
import uk.gov.hmrc.capmovie.models.MovieRegRating
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.MovieRating
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MovieRatingController @Inject()(repo: SessionRepo,
                                      mcc: MessagesControllerComponents,
                                      ageRatingPage: MovieRating,
                                      login: Login,
                                      connector: UpdateConnector)

  extends FrontendController(mcc) {

  def getAgeRating(isSessionUpdate: Boolean): Action[AnyContent] = Action async { implicit request =>
    login.check { id =>
      repo.readOne(id).map { x =>
        val form = MovieRegRating.form.fill(MovieRegRating(x.get.rated.getOrElse("")))
        Ok(ageRatingPage(form, isSessionUpdate, isUpdate = false, ""))
      }
    }
  }

  def submitAgeRating(isSessionUpdate: Boolean): Action[AnyContent] = Action async { implicit request =>
    login.check {
      id =>
        MovieRegRating.form.bindFromRequest().fold({
          formWithErrors => Future(BadRequest(ageRatingPage(formWithErrors, isSessionUpdate = false, isUpdate = false, "")))
        }, { formData =>
          for {
            same <- repo.readOne(id).map { x => x.get.rated.contains(formData.rating) }
            added <- repo.addAgeRating(id, formData.rating)

          } yield (same, added) match {
            case (true, false) => Redirect(routes.MovieSummaryController.getSummary(isSessionUpdate = true))
            case (false, true) => if (isSessionUpdate) Redirect(routes.MovieSummaryController.getSummary(isSessionUpdate = true)) else Redirect(routes.MoviePlotController.getMoviePlot(false))
            case _ => InternalServerError
          }
        }
        )
    }
  }


  def getUpdateAgeRating(id: String): Action[AnyContent] = Action async { implicit request =>
    login.check { _ =>
      connector.readOne(id).flatMap { x =>
        val form = MovieRegRating.form.fill(MovieRegRating(x.get.movie.rated))
        Future.successful(Ok(ageRatingPage(form, isSessionUpdate = false, isUpdate = true, id)))
      }
    }
  }

  def updateAgeRating(id: String): Action[AnyContent] = Action async { implicit request =>
    login.check { _ =>
      MovieRegRating.form.bindFromRequest().fold({
        formWithErrors => Future(BadRequest(ageRatingPage(formWithErrors, isSessionUpdate = false, isUpdate = true, id)))
      }, { formData =>
        for {
          same <- connector.readOne(id).map { x => x.get.movie.rated.contains(formData.rating) }
          updated <- connector.updateRating(id, formData.rating)
        } yield (same, updated) match {
          case (true, false) | (false, true) => Redirect(routes.MoviePlotController.updateMoviePlot(id))
          case _ => InternalServerError
        }
      })
    }
  }

}



