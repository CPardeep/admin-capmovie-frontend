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
                                      login: Login)

  extends FrontendController(mcc) {
  def getAgeRating: Action[AnyContent] = Action async { implicit request =>
    login.check { _ => Future.successful(Ok(ageRatingPage(MovieRegRating.form.fill(MovieRegRating("")))))
    }
  }

  def submitAgeRating: Action[AnyContent] = Action async { implicit request =>
    login.check { id =>
      MovieRegRating.form.bindFromRequest().fold({ formWithErrors =>
        Future(BadRequest(ageRatingPage(formWithErrors)))
      }, { formData =>
        repo.addAgeRating(id, formData.rating).map {
          case true => Redirect(routes.MoviePlotController.getMoviePlot())
          case false => Unauthorized("error")
        }.recover {
          case _ => InternalServerError
        }
      })
    }
  }
}



