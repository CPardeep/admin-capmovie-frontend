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
import uk.gov.hmrc.capmovie.models.MovieRegCast
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.{MovieCast, MovieCastConfirmation}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MovieCastController @Inject()(repo: SessionRepo,
                                    mcc: MessagesControllerComponents,
                                    castPage: MovieCast,
                                    confirmPage: MovieCastConfirmation,
                                    login: Login,
                                    connector: UpdateConnector
                                   )

  extends FrontendController(mcc) {

  def getMovieCast: Action[AnyContent] = Action async { implicit request =>
    login.check { _ =>
      Future.successful(Ok(castPage(MovieRegCast.form.fill(MovieRegCast("")), isUpdate = false, "")))
    }
  }

  def getConfirmationPage: Action[AnyContent] = Action async { implicit request =>
    login.check { _ =>
      repo.readOne(request.session.get("adminId").getOrElse("")).map { x =>
        Ok(confirmPage(x.get.cast, isUpdate = false, ""))
      }
    }
  }

  def submitMovieCast(): Action[AnyContent] = Action async { implicit request =>
    login.check { id =>
      MovieRegCast.form.bindFromRequest().fold({
        formWithErrors => Future(BadRequest(castPage(formWithErrors, isUpdate = false, "")))
      }, { formData =>
        for {
          same <- repo.readOne(id).map { x => x.get.cast.contains(formData.cast) }
          added <- repo.addCast(id, formData.cast)
        } yield (same, added) match {
          case (true, false) | (false, true) => Redirect(routes.MovieCastController.getConfirmationPage())
          case _ => InternalServerError
        }
      })
    }
  }

  def deleteCast(cast: String): Action[AnyContent] = Action.async { implicit request =>
    login.check { _ =>
      for {
        deleted <- repo.removeCast(request.session.get("adminId").getOrElse(""), cast)
        optMovie <- repo.readOne(request.session.get("adminId").getOrElse(""))
      } yield optMovie match {
        case Some(movie) => if (movie.cast.nonEmpty) Redirect(routes.MovieCastController.getConfirmationPage())
        else Redirect(routes.MovieCastController.getMovieCast())
        case _ => InternalServerError
      }
    }
  }

  def getUpdateCast(id: String): Action[AnyContent] = Action async { implicit request =>
    login.check { _ =>
      Future.successful(Ok(castPage(MovieRegCast.form.fill(MovieRegCast("")), isUpdate = true, id)))
    }
  }

  def updateMovieCast(id: String): Action[AnyContent] = Action async { implicit request =>
    login.check { _ =>
      MovieRegCast.form.bindFromRequest().fold({
        formWithErrors => Future(BadRequest(castPage(formWithErrors, isUpdate = true, id)))
      }, { formData =>
        for {
          same <- connector.readOne(id).map { x => x.get.cast.contains(formData.cast) }
          updated <- connector.updateCast(id, formData.cast)
        } yield (same, updated) match {
          case (true, false) | (false, true) => Redirect(routes.MovieCastController.getUpdateConfirmationPage(id))
          case _ => InternalServerError
        }
      })
    }
  }

  def getUpdateConfirmationPage(id: String): Action[AnyContent] = Action async { implicit request =>
    login.check { _ =>
      connector.readOne(id).map { x =>
        Ok(confirmPage(x.get.cast, isUpdate = true, id))
      }
    }
  }

  def updateDeleteCast(id: String, cast: String): Action[AnyContent] = Action.async { implicit request =>
    login.check { _ =>
      for {
        deleted <- connector.removeCast(id, cast)
        optMovie <- connector.readOne(id)
      } yield optMovie match {
        case Some(movie) => if (movie.cast.nonEmpty) Redirect(routes.MovieCastController.getUpdateConfirmationPage(id))
        else Redirect(routes.MovieCastController.getUpdateCast(id))
        case _ => InternalServerError
      }
    }
  }

}
