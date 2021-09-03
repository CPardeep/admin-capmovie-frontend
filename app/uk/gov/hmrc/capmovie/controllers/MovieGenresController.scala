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
import uk.gov.hmrc.capmovie.models.MovieRegGenres
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.{MovieGenres, MovieGenresConfirmation}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.capmovie.controllers.predicates.Login

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MovieGenresController @Inject()(repo: SessionRepo,
                                      mcc: MessagesControllerComponents,
                                      genresPage: MovieGenres,
                                      genresConfirmationPage: MovieGenresConfirmation,
                                      login: Login
                                     )
  extends FrontendController(mcc) {

  def getMovieGenres: Action[AnyContent] = Action async { implicit request =>
    login.check { _ =>
      Future.successful(Ok(genresPage(MovieRegGenres.form.fill(MovieRegGenres("")))))
    }
  }

  def submitMovieGenres(): Action[AnyContent] = Action async { implicit request =>
    login.check { id =>
      MovieRegGenres.form.bindFromRequest().fold({
        formWithErrors => Future(BadRequest(genresPage(formWithErrors)))
      }, { formData =>
        repo.addGenres(id, formData.genres).map {
          case true => Redirect(routes.MovieGenresController.getConfirmationPage())
          case false => Unauthorized("error")
        }.recover {
          case _ => InternalServerError
        }
      })
    }
  }

  def getConfirmationPage: Action[AnyContent] = Action.async { implicit request =>
    login.check { _ =>
      repo.readOne(request.session.get("adminId").getOrElse("")).map { x =>
        Ok(genresConfirmationPage(x.get.genres))
      }
    }
  }

  def deleteGenre(genre: String): Action[AnyContent] = Action.async { implicit request =>
    login.check { _ =>
      for {
        deleted <- repo.removeGenre(request.session.get("adminId").getOrElse(""), genre)
        optMovie <- repo.readOne(request.session.get("adminId").getOrElse(""))
      } yield optMovie match {
        case Some(movie) => if (movie.genres.nonEmpty) Redirect(routes.MovieGenresController.getConfirmationPage())
        else Redirect(routes.MovieGenresController.getMovieGenres())
       case _ => InternalServerError
      }
    }
  }


}

