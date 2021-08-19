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

package connectors

import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.capmovie.connectors.MovieConnector
import uk.gov.hmrc.capmovie.models.MovieReg

class MovieConnectorISpec extends AnyWordSpec with Matchers with GuiceOneServerPerSuite
  with WireMockHelper with BeforeAndAfterEach {

  lazy val connector: MovieConnector = app.injector.instanceOf[MovieConnector]

  override val wireMockPort: Int = 9009

  override def beforeEach(): Unit = startWireMock()

  override def afterEach(): Unit = stopWireMock()

  val movie: MovieReg = MovieReg(
    adminId = "testId",
    name = Some("testMov"),
    year = Some(1111),
    genre = Some("testGenre"),
    ageRating = Some("testAgeRating"),
    img = Some("testImg"),
    description = Some("testDesc"))

  "create" should {
    "return true" when {
      "the data is inserted into db" in {
        stubPost(s"/movie", 201, Json.toJson(movie).toString())
        val result = connector.create(movie)
        await(result) shouldBe true
      }
    }
    "return false" when {
      "invalid data is inserted into db" in {
        stubPost(s"/movie", 400, Json.toJson("{}").toString())
        val result = connector.create(movie)
        await(result) shouldBe false
      }
      "database fails to insert the data" in {
        stubPost(s"/movie", 500, Json.toJson("movie").toString())
        val result = connector.create(movie)
        await(result) shouldBe false
      }
    }
  }

}
