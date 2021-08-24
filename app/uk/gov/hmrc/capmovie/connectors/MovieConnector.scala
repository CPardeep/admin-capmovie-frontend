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

import play.api.libs.json.{JsArray, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, ControllerComponents}
import uk.gov.hmrc.capmovie.models.{Movie, MovieReg}

import javax.inject.Inject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MovieConnector @Inject()(ws: WSClient, cc: ControllerComponents)
  extends AbstractController(cc) {

  def create(movie: MovieReg): Future[Boolean] = {
    ws.url("http://localhost:9009/movie").post(Json.toJson(movie)).map {
      _.status match {
        case CREATED => true
        case _ => false
      }
    }
  }

  def readAll(): Future[List[Movie]] = {
    ws.url("http://localhost:9009/movie-list").get().map{
      x => x.status match {
        case 200 => x.json.as[JsArray].value.map(response => Movie(
          (response \ "id").as[String],
          (response \ "plot").as[String],
          (response \ "genres").as[List[String]],
          (response \ "rated").as[String],
          (response \ "cast").as[List[String]],
          (response \ "poster").as[String],
          (response \ "title").as[String],
        )).toList
        case _ => List()
      }
    }.recover {case _ => List()}
  }


}
