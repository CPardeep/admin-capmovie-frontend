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
import play.api.test.Helpers.{defaultAwaitTimeout, redirectLocation, status}
import play.api.test.{FakeRequest, Helpers}
import uk.gov.hmrc.capmovie.connectors.UpdateConnector
import uk.gov.hmrc.capmovie.controllers.MovieCastController
import uk.gov.hmrc.capmovie.controllers.predicates.Login
import uk.gov.hmrc.capmovie.models.{Movie, MovieReg}
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.{MovieCast, MovieCastConfirmation}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MovieCastControllerISpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val repo: SessionRepo = mock[SessionRepo]
  val connector: UpdateConnector = mock[UpdateConnector]
  val castPage: MovieCast = app.injector.instanceOf[MovieCast]
  val confirmPage: MovieCastConfirmation = app.injector.instanceOf[MovieCastConfirmation]
  val login: Login = app.injector.instanceOf[Login]
  val controller = new MovieCastController(repo, Helpers.stubMessagesControllerComponents(), castPage, confirmPage, login, connector)

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
      "TestPerson2"),
    poster = "testURL",
    title = "testTitle")

  "getMovieCast" should {
    "load the page when called" in {
      val result = controller.getMovieCast(FakeRequest("GET", "/")
        .withSession("adminId" -> "TESTID"))
      status(result) shouldBe OK
    }
  }

  "submitMovieCast" should {
    "return a form value" when {
      "the form is submitted" in {
        when(repo.readOne(any()))
          .thenReturn(Future(Some(movieReg)))
        when(repo.addCast(any(), any())).thenReturn(Future(true))
        val result = controller.submitMovieCast().apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("cast" -> "newTestPerson"))
        status(result) shouldBe SEE_OTHER
      }
    }

    "returns redirect" when {
      "the form value is the same" in {
        when(repo.readOne(any()))
          .thenReturn(Future(Some(movieReg)))
        when(repo.addCast(any(), any())).thenReturn(Future(false))
        val result = controller.submitMovieCast().apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("cast" -> "TestPerson"))
        status(result) shouldBe SEE_OTHER
      }
    }

    "return a bad request" when {
      "the form is submitted" in {
        val result = controller.submitMovieCast().apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("cast" -> ""))
        status(result) shouldBe BAD_REQUEST
      }
    }


    "return InternalServerError" in {
      when(repo.readOne(any())).thenReturn(Future(Some(movieReg)))
      when(repo.addCast(any(), any())).thenReturn(Future(false))
      val result = controller.submitMovieCast().apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("cast" -> "testCast"))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "getConfirmationPage" should {
    "load MovieCastConfirmation Page" in {
      when(repo.readOne(any()))
        .thenReturn(Future.successful(Some(MovieReg("testId", cast = List("actor1", "actor2")))))
      val result = controller.getConfirmationPage(FakeRequest("GET", "/")
        .withSession("adminId" -> "testId"))
      status(result) shouldBe OK
    }
  }

  "deleteCast" should {
    "redirect if cast members are successfully removed" when {
      "cast list not empty" in {
        when(repo.removeCast(any(), any()))
          .thenReturn(Future(true))
        when(repo.readOne(any()))
          .thenReturn(Future.successful(Some(MovieReg("testId", cast = List("actor1", "actor2")))))
        val result = controller.deleteCast("actor1").apply(FakeRequest("GET", "/")
          .withSession("adminId" -> "testId"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).get shouldBe "/capmovie/movie-cast/confirmation"
      }
      "cast list is empty" in {
        when(repo.removeCast(any(), any()))
          .thenReturn(Future(true))
        when(repo.readOne(any()))
          .thenReturn(Future.successful(Some(MovieReg("testId", cast = List()))))
        val result = controller.deleteCast("actor1").apply(FakeRequest("GET", "/")
          .withSession("adminId" -> "testId"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).get shouldBe "/capmovie/movie-cast"
      }
    }
    "return bad request when admin attempts to delete invalid cast member" in {
      when(repo.removeCast(any(), any()))
        .thenReturn(Future(false))
      when(repo.readOne(any()))
        .thenReturn(Future.successful(None))
      val result = controller.deleteCast("").apply(FakeRequest("GET", "/")
        .withSession("adminId" -> "testId"))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "getUpdateCast" should {
    "load the page when called" in {
      val result = controller.getUpdateCast("MOVID").apply(FakeRequest("GET", "/")
        .withSession("adminId" -> "testId"))
      status(result) shouldBe OK
    }
  }

  "POST updateMovieCast" should {
    "the form is submitted with errors" in {
      val result = controller.updateMovieCast("TESTMOV").apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("cast" -> ""))
      status(result) shouldBe BAD_REQUEST
    }

    "the form is submitted" in {
      when(connector.readOne(any()))
        .thenReturn(Future(Some(movie)))
      when(connector.updateCast(any(), any())).thenReturn(Future(true))
      val result = controller.updateMovieCast("TESTMOV").apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("cast" -> "newTestPerson"))
      status(result) shouldBe SEE_OTHER
    }

    "returns redirect" when {
      "the form value is the same" in {
        when(connector.readOne(any()))
          .thenReturn(Future(Some(movie)))
        when(connector.updateCast(any(), any())).thenReturn(Future(false))
        val result = controller.updateMovieCast("TESTMOV").apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("cast" -> "testPerson"))
        status(result) shouldBe SEE_OTHER
      }
    }

    "returns internalServerError" in {
      when(connector.readOne(any()))
        .thenReturn(Future(Some(movie)))
      when(connector.updateCast(any(), any())).thenReturn(Future(false))
      val result = controller.updateMovieCast("TESTMOV").apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("cast" -> "newTestPerson"))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "getUpdateConfirmationPage" should {
    "load genres confirmation page" in {
      when(connector.readOne(any())).thenReturn(Future.successful(Some(movie)))
      val result = controller.getUpdateConfirmationPage("TESTMOV")
        .apply(FakeRequest("GET", "/")
          .withSession("adminId" -> "testId"))
      status(result) shouldBe OK
    }
  }

  "updateDeleteCast" should {
    "redirect if cast are successfully removed" when {
      "cast list is not empty" in {
        when(connector.removeCast(any(), any()))
          .thenReturn(Future(true))
        when(connector.readOne(any()))
          .thenReturn(Future.successful(Some(movie)))
        val result = controller.updateDeleteCast("TESTMOV", "testPerson")
          .apply(FakeRequest("GET", "/")
            .withSession("adminId" -> "testId"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).get shouldBe "/capmovie/update/TESTMOV/cast/confirmation"
      }
    }
    "redirect if cast are not successfully removed" when {
      "cast list is empty" in {
        when(connector.removeCast(any(), any()))
          .thenReturn(Future(true))
        when(connector.readOne(any()))
          .thenReturn(Future.successful(Some(movie.copy(cast = List()))))
        val result = controller.updateDeleteCast("TESTMOV", "testPerson")
          .apply(FakeRequest("GET", "/")
            .withSession("adminId" -> "testId"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).get shouldBe "/capmovie/update/TESTMOV/cast"
      }
    }

    "returns Internal Server Error" when {
      "admin attempts to delete invalid cast" in {
        when(connector.removeCast(any(), any()))
          .thenReturn(Future(false))
        when(connector.readOne(any()))
          .thenReturn(Future.successful(None))
        val result = controller.updateDeleteCast("TESTMOV", "")
          .apply(FakeRequest("GET", "/")
            .withSession("adminId" -> "testId"))
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }


}
