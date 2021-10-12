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

package uk.gov.hmrc.capmovie.connectors

import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}
import uk.gov.hmrc.capmovie.models.MovieWithAvgRating

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UpdateConnector @Inject()(ws: WSClient,
                                cc: ControllerComponents)
  extends AbstractController(cc) {

  def readOne(id: String): Future[Option[MovieWithAvgRating]] = {
    ws.url("http://localhost:9009/movie/" + id).get().map { response =>
      response.status match {
        case 200 => response.json.validate[MovieWithAvgRating] match {
          case JsSuccess(movie, _) => Some(movie)
          case JsError(_) => None
        }
        case _ => None
      }
    }
  }

  def removeGenre(id:String, genres: String): Future[Boolean] = {
    ws.url(s"http://localhost:9009/movie/$id/remove-genre")
      .patch(Json.obj("genres" -> genres))
      .map{
        _.status match {
          case 200 => true
          case _ => false
        }
      }
  }

  def removeCast(id:String, cast: String): Future[Boolean] = {
    ws.url(s"http://localhost:9009/movie/$id/remove-cast")
      .patch(Json.obj("cast" -> cast))
      .map{
        _.status match {
          case 200 => true
          case _ => false
        }
      }
  }

  def updateTitle(id: String, title: String): Future[Boolean] = {
    ws.url(s"http://localhost:9009/movie/$id/update-title")
      .patch(Json.obj("title" -> title))
      .map {
        _.status match {
          case 200 => true
          case _ => false
        }
      }
  }

  def updatePlot(id: String, plot: String): Future[Boolean] = {
    ws.url(s"http://localhost:9009/movie/$id/update-plot")
      .patch(Json.obj("plot" -> plot))
      .map {
        _.status match {
          case 200 => true
          case _ => false
        }
      }
  }

  def updatePoster(id: String, poster: String): Future[Boolean] = {
    ws.url(s"http://localhost:9009/movie/$id/update-poster")
      .patch(Json.obj("poster" -> poster))
      .map {
        _.status match {
          case 200 => true
          case _ => false
        }
      }
  }

  def updateRating(id: String, rated: String): Future[Boolean] = {
    ws.url(s"http://localhost:9009/movie/$id/update-age-rating")
      .patch(Json.obj("rated" -> rated))
      .map {
        _.status match {
          case 200 => true
          case _ => false
        }
      }
  }

  def updateGenre(id: String, genres: String): Future[Boolean] = {
    ws.url(s"http://localhost:9009/movie/$id/update-genre")
      .patch(Json.obj("genres" -> genres))
      .map {
        _.status match {
          case 200 => true
          case _ => false
        }
      }
  }

  def updateCast(id: String, cast: String): Future[Boolean] = {
    ws.url(s"http://localhost:9009/movie/$id/update-cast")
      .patch(Json.obj("cast" -> cast))
      .map {
        _.status match {
          case 200 => true
          case _ => false
        }
      }
  }
}
