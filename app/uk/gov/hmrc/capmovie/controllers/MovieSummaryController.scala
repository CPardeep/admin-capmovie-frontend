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
import uk.gov.hmrc.capmovie.connectors.MovieConnector
import uk.gov.hmrc.capmovie.controllers.predicates.Login
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.capmovie.views.html.{MovieSummary, SubmissionConfirmation}

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MovieSummaryController @Inject()(repo: SessionRepo,
                                       mcc: MessagesControllerComponents,
                                       summaryPage: MovieSummary,
                                       connector: MovieConnector,
                                       login: Login,
                                       submission: SubmissionConfirmation
                                      )
  extends FrontendController(mcc) {

  def getSummary(isSessionUpdate: Boolean): Action[AnyContent] = Action async { implicit request =>
    login.check { id => repo.readOne(id).map { movie => Ok(summaryPage(movie.get, isSessionUpdate)) }

    }
  }

  def confirmation: Action[AnyContent] = Action async { implicit request =>
    login.check { _ => Future.successful(Ok(submission())) }
  }

  def submitSummary: Action[AnyContent] = Action async { implicit request =>
    login.check { id =>
      repo.readOne(id).flatMap { movie =>
        connector.create(movie.get)
      }.map {
        case true => {
          repo.clearSession(id)
          Redirect(routes.MovieSummaryController.confirmation())
        }
        case _ => BadRequest
      }
    }
  }
}