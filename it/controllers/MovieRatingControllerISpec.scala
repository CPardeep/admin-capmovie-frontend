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
import play.api.http.Status.{BAD_REQUEST, INTERNAL_SERVER_ERROR, OK, SEE_OTHER}
import play.api.test.Helpers.{defaultAwaitTimeout, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.capmovie.connectors.UpdateConnector
import uk.gov.hmrc.capmovie.controllers.MovieRatingController
import uk.gov.hmrc.capmovie.controllers.predicates.Login
import uk.gov.hmrc.capmovie.models.{Movie, MovieReg}
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.MovieRating

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MovieRatingControllerISpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val repo: SessionRepo = mock[SessionRepo]
  val connector: UpdateConnector = mock[UpdateConnector]
  val ageRatingPage: MovieRating = app.injector.instanceOf[MovieRating]
  val login: Login = app.injector.instanceOf[Login]
  val controller = new MovieRatingController(repo, Helpers.stubMessagesControllerComponents(), ageRatingPage, login, connector)

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

  "getMovieAgeRating" should {
    "load the page when called" in {
      when(repo.readOne(any()))
        .thenReturn(Future(Some(movieReg)))
      val result = controller.getAgeRating(isSessionUpdate = false)(FakeRequest("GET", "/")
        .withSession("adminId" -> "TESTID"))
      status(result) shouldBe OK
    }
  }
  "submitMovieAgeRating" should {
    "return a form value" when {
      "the form is submitted" in {
        when(repo.readOne(any())).thenReturn(Future(Some(movieReg)))
        when(repo.addAgeRating(any(), any())).thenReturn(Future(true))
        val result = controller.submitAgeRating(isSessionUpdate = false).apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("rated" -> "testRating1"))
        status(result) shouldBe SEE_OTHER
      }
    }
    "returns redirect" when {
      "when form value is the same" in {
        when(repo.readOne(any())).thenReturn(Future(Some(movieReg)))
        when(repo.addAgeRating(any(), any())).thenReturn(Future(false))
        val result = controller.submitAgeRating(isSessionUpdate = true).apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("rated" -> "testRating"))
        status(result) shouldBe SEE_OTHER
      }
    }
    "return a bad request" when {
      "the form is submitted" in {
        val result = controller.submitAgeRating(isSessionUpdate = false).apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("rated" -> ""))
        status(result) shouldBe BAD_REQUEST
      }
    }
    "return InternalServerError" in {
      when(repo.readOne(any())).thenReturn(Future(Some(movieReg)))
      when(repo.addAgeRating(any(), any())).thenReturn(Future(false))
      val result = controller.submitAgeRating(isSessionUpdate = false).apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("rated" -> "testRating1"))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "getUpdateAgeRating" should {
    "load the page when called" in {
      when(connector.readOne(any()))
        .thenReturn(Future(Some(movie)))
      val result = controller.getUpdateAgeRating("MOVID").apply(FakeRequest("GET", "/")
        .withSession("adminId" -> "TESTID"))
      status(result) shouldBe OK
    }
  }

  "POST updateAgeRating" should {
    "the form is submitted with errors" in {
      val result = controller.updateAgeRating("MOVID").apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("rated" -> ""))
      status(result) shouldBe BAD_REQUEST
    }

    "the form is submitted" in {
      when(connector.readOne(any()))
        .thenReturn(Future(Some(movie)))
      when(connector.updateRating(any(), any())).thenReturn(Future(true))
      val result = controller.updateAgeRating("MOVID").apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("rated" -> "UpdatedRating"))
      status(result) shouldBe SEE_OTHER
    }

    "returns redirect" when {
      "when form value is the same" in {
        when(connector.readOne(any()))
          .thenReturn(Future(Some(movie)))
        when(connector.updateRating(any(), any())).thenReturn(Future(false))
        val result = controller.updateAgeRating("TESTMOV").apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("rated" -> "testRating"))
        status(result) shouldBe SEE_OTHER
      }
    }

    "returns internalServerError" in {
      when(connector.readOne(any()))
        .thenReturn(Future(Some(movie)))
      when(connector.updateRating(any(), any())).thenReturn(Future(false))
      val result = controller.updateAgeRating("MOVID").apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("rated" -> "UpdatedRating"))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

}