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
import play.api.http.Status.{BAD_REQUEST, OK, UNAUTHORIZED}
import play.api.libs.json.Json
import play.api.test.Helpers.{await, defaultAwaitTimeout}
import uk.gov.hmrc.capmovie.connectors.MovieConnector
import uk.gov.hmrc.capmovie.models.{Admin, Movie, MovieReg}

class MovieConnectorISpec extends AnyWordSpec with Matchers with GuiceOneServerPerSuite
  with WireMockHelper with BeforeAndAfterEach {

  lazy val connector: MovieConnector = app.injector.instanceOf[MovieConnector]

  override val wireMockPort: Int = 9009

  override def beforeEach(): Unit = startWireMock()

  override def afterEach(): Unit = stopWireMock()

  val movieReg: MovieReg = MovieReg(
    adminId = "TESTMOV",
    plot = Some("Test plot"),
    genres = List(
      "testGenre1",
      "testGenre2"),
    rated = Some("testRating"),
    cast = Some(List(
      "testPerson",
      "TestPerson")),
    poster = Some("testURL"),
    title = Some("testTitle"))

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

  val movieList = List(movie, movie.copy(id = "TESTMOV2"))

  val adminUser: Admin = Admin(
    id = "TESTID",
    password = "TESTPASS"
  )

  "create" should {
    "return true" when {
      "the data is inserted into db" in {
        stubPost(s"/movie", 201, Json.toJson(movieReg).toString())
        val result = connector.create(movieReg)
        await(result) shouldBe true
      }
    }
    "return false" when {
      "invalid data is inserted into db" in {
        stubPost(s"/movie", 400, Json.toJson("{}").toString())
        val result = connector.create(movieReg)
        await(result) shouldBe false
      }
      "database fails to insert the data" in {
        stubPost(s"/movie", 500, Json.toJson("movie").toString())
        val result = connector.create(movieReg)
        await(result) shouldBe false
      }
    }
  }

  "readAll" should {
    "return movieList" in {
      stubGet(s"/movie-list", 200, Json.toJson(movieList).toString())
      val result = connector.readAll()
      await(result) shouldBe movieList
    }
    "return emptyList" in {
      stubGet(s"/movie-list", 400, Json.toJson("{}").toString())
      val result = connector.readAll()
      await(result) shouldBe List()
    }

    "fail to connect to database" in {
      stubGet(s"/movie-list", 200, Json.toJson("{}").toString())
      val result = connector.readAll()
      await(result) shouldBe List()
    }
  }

  "login" should {
    "return Ok" in {
      stubPost(s"/admin-login", 200, Json.toJson(adminUser).toString())
      val result = connector.login(adminUser)
      await(result) shouldBe OK
    }

    "return Unauthorised" in {
      stubPost(s"/admin-login", 401, Json.toJson("{}").toString())
      val result = connector.login(adminUser)
      await(result) shouldBe UNAUTHORIZED
    }
  }
  "return Badrequest" in {
    stubPost(s"/admin-login", 400, Json.toJson("{}").toString())
    val result = connector.login(adminUser)
    await(result) shouldBe BAD_REQUEST

  }

}
