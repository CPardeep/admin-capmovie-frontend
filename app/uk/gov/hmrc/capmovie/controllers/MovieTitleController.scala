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

import play.api.i18n.Messages.implicitMessagesProviderToMessages
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.capmovie.connectors.UpdateConnector
import uk.gov.hmrc.capmovie.controllers.predicates.Login
import uk.gov.hmrc.capmovie.models.{MovieReg, MovieRegTitle}
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.MovieTitle
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MovieTitleController @Inject()(repo: SessionRepo,
                                     mcc: MessagesControllerComponents,
                                     titlePage: MovieTitle,
                                     login: Login,
                                     connector: UpdateConnector
                                    )
  extends FrontendController(mcc) {

  def getMovieTitle(isSessionUpdate: Boolean): Action[AnyContent] = Action async { implicit request =>
    login.check { id =>
      repo.create(MovieReg(adminId = id))
      repo.readOne(id).map { x =>
        val form = MovieRegTitle.form.fill(MovieRegTitle(x.get.title.getOrElse("")))
        Ok(titlePage(form, isSessionUpdate, isUpdate = false, ""))
      }
    }
  }

  def submitMovieTitle(isSessionUpdate: Boolean): Action[AnyContent] = Action async { implicit request =>
    login.check {
      id =>
        MovieRegTitle.form.bindFromRequest().fold({
          formWithErrors => Future(BadRequest(titlePage(formWithErrors, isSessionUpdate = false, isUpdate = false, "")))
        }, { formData =>
          for {
            same <- repo.readOne(id).map { x => x.get.title.contains(formData.title) }
            added <- repo.addTitle(id, formData.title)
          } yield (same, added) match {
            case (true, false) => Redirect(routes.MovieSummaryController.getSummary(isSessionUpdate = true))
            case (false, true) => if (isSessionUpdate) Redirect(routes.MovieSummaryController.getSummary(isSessionUpdate = true)) else Redirect(routes.MovieGenresController.getMovieGenres(false))
            case _ => InternalServerError
          }
        }
        )
    }
  }

  def getUpdateTitle(id: String): Action[AnyContent] = Action async { implicit request =>
    login.check { _ =>
      connector.readOne(id).flatMap { x =>
        val form = MovieRegTitle.form.fill(MovieRegTitle(x.get.movie.title))
        Future.successful(Ok(titlePage(form, isSessionUpdate = false, isUpdate = true, id)))
      }
    }
  }

  def updateMovieTitle(id: String): Action[AnyContent] = Action async {
    implicit request =>
      login.check {
        _ =>
          MovieRegTitle.form.bindFromRequest().fold({
            formWithErrors => Future(BadRequest(titlePage(formWithErrors, isSessionUpdate = false, isUpdate = true, id)))
          }, {
            formData =>
              for {
                same <- connector.readOne(id).map { x => x.get.movie.title.contains(formData.title) }
                updated <- connector.updateTitle(id, formData.title)
              } yield (same, updated) match {
                case (true, false) | (false, true) => Redirect(routes.MovieGenresController.getUpdateConfirmationPage(id))
                case _ => InternalServerError
              }
          })
      }
  }

}
