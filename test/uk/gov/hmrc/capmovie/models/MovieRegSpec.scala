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

class MovieRegSpec extends AnyWordSpec with Matchers {

  val movieReg: MovieReg = MovieReg(
    adminId = "testId",
    name = Some("testMov"),
    year = Some(1111),
    genre = Some("testGenre"),
    ageRating = Some("testAgeRating"),
    img = Some("testImg"),
    description = Some("testDesc"))

  val movieRegJson: JsValue = Json.parse(
    s"""{
       |    "adminId" : "testId",
       |    "name" : "testMov",
       |    "year" : 1111,
       |    "genre" : "testGenre",
       |    "ageRating" : "testAgeRating",
       |    "img" : "testImg",
       |    "description" : "testDesc"
       |}
       |""".stripMargin)


  "OFormat" should {
    "convert object to json" in {
      Json.toJson(movieReg) shouldBe movieRegJson
    }
    "convert json to object" in {
      Json.fromJson[MovieReg](movieRegJson) shouldBe JsSuccess(movieReg)
    }
  }
}
