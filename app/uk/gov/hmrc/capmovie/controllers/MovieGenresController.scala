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
import uk.gov.hmrc.capmovie.models.MovieRegGenres
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.{MovieGenres, MovieGenresConfirmation}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MovieGenresController @Inject()(repo: SessionRepo,
                                      mcc: MessagesControllerComponents,
                                      genresPage: MovieGenres,
                                      genresConfirmationPage: MovieGenresConfirmation,
                                      login: Login,
                                      connector: UpdateConnector
                                     )
  extends FrontendController(mcc) {

  def getMovieGenres(isSessionUpdate: Boolean): Action[AnyContent] = Action async { implicit request =>
    login.check { _ =>
      Future.successful(Ok(genresPage(MovieRegGenres.form.fill(MovieRegGenres("")), isSessionUpdate, isUpdate = false, "")))
    }
  }

  def submitMovieGenres(isSessionUpdate: Boolean): Action[AnyContent] = Action async { implicit request =>
    login.check { id =>
      MovieRegGenres.form.bindFromRequest().fold({
        formWithErrors => Future(BadRequest(genresPage(formWithErrors, isSessionUpdate, isUpdate = false, "")))
      }, { formData =>
        for {
          same <- repo.readOne(id).map { x => x.get.genres.contains(formData.genres) }
          added <- repo.addGenres(id, formData.genres)
        } yield (same, added) match {
          case (true, false) | (false, true) => if (isSessionUpdate)
            Redirect(routes.MovieGenresController.getConfirmationPage(true))
          else Redirect(routes.MovieGenresController.getConfirmationPage(false))
          case _ => InternalServerError
        }
      })
    }
  }

  def getConfirmationPage(isSessionUpdate: Boolean): Action[AnyContent] = Action.async { implicit request =>
    login.check { _ =>
      repo.readOne(request.session.get("adminId").getOrElse("")).map { x =>
        Ok(genresConfirmationPage(x.get.genres, isSessionUpdate, isUpdate = false, ""))
      }
    }
  }

  def deleteGenre(genre: String, isSessionUpdate: Boolean): Action[AnyContent] = Action.async { implicit request =>
    login.check { _ =>
      for {
        deleted <- repo.removeGenre(request.session.get("adminId").getOrElse(""), genre)
        optMovie <- repo.readOne(request.session.get("adminId").getOrElse(""))
      } yield optMovie match {
        case Some(movie) => if (isSessionUpdate) {
          if (movie.genres.nonEmpty) Redirect(routes.MovieGenresController.getConfirmationPage(isSessionUpdate = true))
          else Redirect(routes.MovieGenresController.getMovieGenres(isSessionUpdate = true))
        }
        else {
          if (movie.genres.nonEmpty) Redirect(routes.MovieGenresController.getConfirmationPage(isSessionUpdate = false))
          else Redirect(routes.MovieGenresController.getMovieGenres(isSessionUpdate = false))
        }
        case _ => InternalServerError
      }
    }
  }

  def getUpdateGenre(id: String): Action[AnyContent] = Action async { implicit request =>
    login.check { _ =>
      Future.successful(Ok(genresPage(MovieRegGenres.form.fill(MovieRegGenres("")), isSessionUpdate = false, isUpdate = true, id)))
    }
  }

  def updateMovieGenre(id: String): Action[AnyContent] = Action async { implicit request =>
    login.check { _ =>
      MovieRegGenres.form.bindFromRequest().fold({
        formWithErrors => Future(BadRequest(genresPage(formWithErrors, isSessionUpdate = false, isUpdate = true, id)))
      }, { formData =>
        for {
          same <- connector.readOne(id).map { x => x.get.genres.contains(formData.genres) }
          updated <- connector.updateGenre(id, formData.genres)
        } yield (same, updated) match {
          case (true, false) | (false, true) => Redirect(routes.MovieGenresController.getUpdateConfirmationPage(id))
          case _ => InternalServerError
        }
      })
    }
  }

  def getUpdateConfirmationPage(id: String): Action[AnyContent] = Action.async { implicit request =>
    login.check { _ =>
      connector.readOne(id).map { x =>
        Ok(genresConfirmationPage(x.get.genres, isSessionUpdate = false, isUpdate = true, id))
      }
    }
  }

  def updateDeleteGenre(id: String, genre: String): Action[AnyContent] = Action.async { implicit request =>
    login.check { _ =>
      for {
        deleted <- connector.removeGenre(id, genre)
        optMovie <- connector.readOne(id)
      } yield optMovie match {
        case Some(movie) => if (movie.genres.nonEmpty) Redirect(routes.MovieGenresController.getUpdateConfirmationPage(id))
        else Redirect(routes.MovieGenresController.getUpdateGenre(id))
        case _ => InternalServerError
      }
    }
  }

}

