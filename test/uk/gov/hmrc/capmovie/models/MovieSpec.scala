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

package uk.gov.hmrc.capmovie.models

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsSuccess, JsValue, Json}

class MovieSpec extends AnyWordSpec with Matchers {

  val movie: Movie = Movie(
    id = "TESTMOV",
    plot = "Test plot",
    genres = List(
      "testGenre1",
      "testGenre2"),
    rated = "testRating",
    cast = List(
      "testPerson",
      "TestPerson"),
    poster = "testURL",
    title = "testTitle")
  val movieJson: JsValue = Json.parse(
    s"""{
       |    "id" : "${movie.id}",
       |    "plot" : "${movie.plot}",
       |    "genres" : [
       |       "${movie.genres.head}",
       |       "${movie.genres(1)}"
       |    ],
       |    "rated" : "${movie.rated}",
       |    "cast" : [
       |       "${movie.cast.head}",
       |       "${movie.cast(1)}"
       |    ],
       |    "poster" : "${movie.poster}",
       |    "title" : "${movie.title}"
       |}
       |""".stripMargin)

  "Oformat" should {
    "convert object to json" in {
      Json.toJson(movie) shouldBe movieJson
    }
    "convert json to object" in {
      Json.fromJson[Movie](movieJson) shouldBe JsSuccess(movie)
    }
  }

}
