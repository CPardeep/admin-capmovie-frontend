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

package controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status._
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.capmovie.connectors.UpdateConnector
import uk.gov.hmrc.capmovie.controllers.MoviePosterController
import uk.gov.hmrc.capmovie.controllers.predicates.Login
import uk.gov.hmrc.capmovie.models.{Movie, MovieReg, MovieWithAvgRating}
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.MoviePoster

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MoviePosterControllerISpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val repo: SessionRepo = mock[SessionRepo]
  val connector: UpdateConnector = mock[UpdateConnector]
  val posterPage: MoviePoster = app.injector.instanceOf[MoviePoster]
  val login: Login = app.injector.instanceOf[Login]
  val controller = new MoviePosterController(repo, Helpers.stubMessagesControllerComponents(), posterPage, login, connector)

  val movieReg: MovieReg = MovieReg(
    adminId = "TESTID",
    plot = Some("Test plot"),
    genres = List(
      "testGenre1",
      "testGenre2"),
    rated = Some("testRating"),
    cast = List(
      "testPerson",
      "TestPerson"),
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

  val movieWithAvgRating: MovieWithAvgRating = MovieWithAvgRating(
    movie = movie,
    avgRating = 0.0
  )

  "getMoviePoster" should {
    "load the page when called" in {
      when(repo.readOne(any()))
        .thenReturn(Future(Some(movieReg)))
      val result = controller.getMoviePoster(isSessionUpdate = false)(FakeRequest("GET", "/")
        .withSession("adminId" -> "TESTID"))
      status(result) shouldBe OK
    }
  }
  "submitMoviePoster" should {
    "return a form value" when {
      "the form is submitted" in {
        when(repo.readOne(any())).thenReturn(Future(Some(movieReg)))
        when(repo.addPoster(any(), any())).thenReturn(Future(true))
        val result = controller.submitMoviePoster(isSessionUpdate = false).apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("poster" -> "testURL1"))
        status(result) shouldBe SEE_OTHER
      }
    }

    "returns redirect" when {
      "when form value is the same" in {
        when(repo.readOne(any())).thenReturn(Future(Some(movieReg)))
        when(repo.addPoster(any(), any())).thenReturn(Future(false))
        val result = controller.submitMoviePoster(isSessionUpdate = true).apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("poster" -> "testURL"))
        status(result) shouldBe SEE_OTHER
      }
    }

    "return a bad request" when {
      "the form is submitted with errors" in {
        val result = controller.submitMoviePoster(isSessionUpdate = false).apply(FakeRequest("POST", "/")
          .withFormUrlEncodedBody("poster" -> "")
          .withSession("adminId" -> "TESTID"))
        status(result) shouldBe BAD_REQUEST
      }
    }

    "returns InternalServerError" in {
      when(repo.readOne(any())).thenReturn(Future(Some(movieReg)))
      when(repo.addPoster(any(), any())).thenReturn(Future(false))
      val result = controller.submitMoviePoster(isSessionUpdate = false).apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("poster" -> "testURL1"))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "getUpdatePoster" should {
    "load the page when called" in {
      when(connector.readOne(any()))
        .thenReturn(Future(Some(movieWithAvgRating)))
      val result = controller.getUpdatePoster("TESTMOV").apply(FakeRequest("GET", "/")
        .withSession("adminId" -> "TESTID"))
      status(result) shouldBe OK
    }
  }

  "updateMoviePoster" should {
    "the form is submitted with errors" in {
      when(connector.readOne(any()))
        .thenReturn(Future(Some(movieWithAvgRating)))
      val result = controller.updateMoviePoster("TESTMOV").apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("poster" -> ""))
      status(result) shouldBe BAD_REQUEST
    }

    "the form is submitted" in {
      when(connector.readOne(any()))
        .thenReturn(Future(Some(movieWithAvgRating)))
      when(connector.updatePoster(any(), any())).thenReturn(Future(true))
      val result = controller.updateMoviePoster("TESTMOV").apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("poster" -> "UpdatedPoster"))
      status(result) shouldBe SEE_OTHER
    }

    "returns redirect" when{
      "when form value is the same" in {
        when(connector.readOne(any()))
          .thenReturn(Future(Some(movieWithAvgRating)))
        when(connector.updatePoster(any(), any())).thenReturn(Future(false))
        val result = controller.updateMoviePoster("TESTMOV").apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("poster" -> "testURL"))
        status(result) shouldBe SEE_OTHER
      }
    }

    "returns internalServerError" in {
      when(connector.readOne(any()))
        .thenReturn(Future(Some(movieWithAvgRating)))
      when(connector.updatePoster(any(), any())).thenReturn(Future(false))
      val result = controller.updateMoviePoster("TESTMOV").apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("poster" -> "UpdatedPoster"))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }
}
