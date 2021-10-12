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
import uk.gov.hmrc.capmovie.controllers.MovieGenresController
import uk.gov.hmrc.capmovie.controllers.predicates.Login
import uk.gov.hmrc.capmovie.models.{Movie, MovieReg, MovieWithAvgRating}
import uk.gov.hmrc.capmovie.repo.SessionRepo
import uk.gov.hmrc.capmovie.views.html.{MovieGenres, MovieGenresConfirmation}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class MovieGenresControllerISpec extends AnyWordSpec with Matchers with GuiceOneAppPerSuite {

  val repo: SessionRepo = mock[SessionRepo]
  val connector: UpdateConnector = mock[UpdateConnector]
  val genresPage: MovieGenres = app.injector.instanceOf[MovieGenres]
  val genresConfirmationPage: MovieGenresConfirmation = app.injector.instanceOf[MovieGenresConfirmation]
  val login: Login = app.injector.instanceOf[Login]
  val controller = new MovieGenresController(repo, Helpers.stubMessagesControllerComponents(), genresPage, genresConfirmationPage, login, connector)

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
      "genre1",
      "genre2"),
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

  "getMovieGenres" should {
    "load the genres page when called" in {
      val result = controller.getMovieGenres(isSessionUpdate = false)(FakeRequest("GET", "/")
        .withSession("adminId" -> "TESTID"))
      status(result) shouldBe OK
    }
  }
  "submitMovieGenres" should {
    "return a form value" when {
      "the form is submitted with valid details" in {
        when(repo.readOne(any()))
          .thenReturn(Future(Some(movieReg)))
        when(repo.addGenres(any(), any())).thenReturn(Future(true))
        val result = controller.submitMovieGenres(isSessionUpdate = false).apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("genres" -> "newTestGenre"))
        status(result) shouldBe SEE_OTHER
      }
    }
    "returns redirect" when {
      "the form value is the same" in {
        when(repo.readOne(any()))
          .thenReturn(Future(Some(movieReg)))
        when(repo.addGenres(any(), any())).thenReturn(Future(false))
        val result = controller.submitMovieGenres(isSessionUpdate = true).apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("genres" -> "testGenre1"))
        status(result) shouldBe SEE_OTHER
      }
    }
    "return a bad request" when {
      "no form value is submitted" in {
        val result = controller.submitMovieGenres(isSessionUpdate = false).apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("genres" -> ""))
        status(result) shouldBe BAD_REQUEST
      }
    }
    "return InternalServerError" in {
      when(repo.readOne(any())).thenReturn(Future(Some(movieReg)))
      when(repo.addGenres(any(), any())).thenReturn(Future(false))
      val result = controller.submitMovieGenres(isSessionUpdate = false).apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("genres" -> "newTestGenre"))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "getConfirmationPage" should {
    "load genres confirmation page" in {
      when(repo.readOne(any()))
        .thenReturn(Future.successful(Some(MovieReg("testId", genres = List("genre1", "genre2")))))
      val result = controller.getConfirmationPage(isSessionUpdate = false)(FakeRequest("GET", "/")
        .withSession("adminId" -> "testId"))
      status(result) shouldBe OK
    }
  }

  "deleteGenre" should {
    "redirect if genres are successfully removed" when {
      "genre list not empty" in {
        when(repo.removeGenre(any(), any()))
          .thenReturn(Future(true))
        when(repo.readOne(any()))
          .thenReturn(Future.successful(Some(MovieReg("testId", genres = List("genre1", "genre2")))))
        val result = controller.deleteGenre("genre1", isSessionUpdate = false).apply(FakeRequest("GET", "/")
          .withSession("adminId" -> "testId"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).get shouldBe "/capmovie/movie-genres/confirmation/false"
      }
      "genre list is empty" in {
        when(repo.removeGenre(any(), any()))
          .thenReturn(Future(true))
        when(repo.readOne(any()))
          .thenReturn(Future.successful(Some(MovieReg("testId", genres = List()))))
        val result = controller.deleteGenre("genre1", isSessionUpdate = false).apply(FakeRequest("GET", "/")
          .withSession("adminId" -> "testId"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).get shouldBe "/capmovie/movie-genres/false"
      }
    }
    "return bad request when admin attempts to delete invalid genre" in {
      when(repo.removeGenre(any(), any()))
        .thenReturn(Future(false))
      when(repo.readOne(any()))
        .thenReturn(Future.successful(None))
      val result = controller.deleteGenre("", isSessionUpdate = false).apply(FakeRequest("GET", "/")
        .withSession("adminId" -> "testId"))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "getUpdateGenre" should {
    "load the page when called" in {
      val result = controller.getUpdateGenre("TESTMOV").apply(FakeRequest("GET", "/")
        .withSession("adminId" -> "testId"))
      status(result) shouldBe OK
    }
  }

  "POST updateMovieGenre" should {
    "the form is submitted with errors" in {
      val result = controller.updateMovieGenre("TESTMOV").apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("genres" -> ""))
      status(result) shouldBe BAD_REQUEST
    }

    "the form is submitted" in {
      when(connector.readOne(any()))
        .thenReturn(Future(Some(movieWithAvgRating)))
      when(connector.updateGenre(any(), any())).thenReturn(Future(true))
      val result = controller.updateMovieGenre("TESTMOV").apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("genres" -> "newGenre"))
      status(result) shouldBe SEE_OTHER
    }

    "returns redirect" when {
      "the same form value is submitted" in {
        when(connector.readOne(any()))
          .thenReturn(Future(Some(movieWithAvgRating)))
        when(connector.updateGenre(any(), any())).thenReturn(Future(false))
        val result = controller.updateMovieGenre("TESTMOV").apply(FakeRequest("POST", "/")
          .withSession("adminId" -> "TESTID")
          .withFormUrlEncodedBody("genres" -> "genre1"))
        status(result) shouldBe SEE_OTHER
      }
    }

    "returns internalServerError" in {
      when(connector.readOne(any()))
        .thenReturn(Future(Some(movieWithAvgRating)))
      when(connector.updateGenre(any(), any())).thenReturn(Future(false))
      val result = controller.updateMovieGenre("TESTMOV").apply(FakeRequest("POST", "/")
        .withSession("adminId" -> "TESTID")
        .withFormUrlEncodedBody("genres" -> "newGenre"))
      status(result) shouldBe INTERNAL_SERVER_ERROR
    }
  }

  "getUpdateConfirmationPage" should {
    "load genres confirmation page" in {
      when(connector.readOne(any())).thenReturn(Future.successful(Some(movieWithAvgRating)))
      val result = controller.getUpdateConfirmationPage("TESTMOV")
        .apply(FakeRequest("GET", "/")
          .withSession("adminId" -> "testId"))
      status(result) shouldBe OK
    }
  }

  "updateDeleteGenre" should {
    "redirect if genres are successfully removed" when {
      "genre list is not empty" in {
        when(connector.removeGenre(any(), any()))
          .thenReturn(Future(true))
        when(connector.readOne(any()))
          .thenReturn(Future.successful(Some(movieWithAvgRating)))
        val result = controller.updateDeleteGenre("TESTMOV", "genre1")
          .apply(FakeRequest("GET", "/")
            .withSession("adminId" -> "testId"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).get shouldBe "/capmovie/update/TESTMOV/genre/confirmation"
      }
    }
    "redirect if genres are not successfully removed" when {
      "genre list is empty" in {
        when(connector.removeGenre(any(), any()))
          .thenReturn(Future(true))
        when(connector.readOne(any()))
          .thenReturn(Future.successful(Some(MovieWithAvgRating(movie.copy(genres = List()), 0.0))))
        val result = controller.updateDeleteGenre("TESTMOV", "genre1")
          .apply(FakeRequest("GET", "/")
            .withSession("adminId" -> "testId"))
        status(result) shouldBe SEE_OTHER
        redirectLocation(result).get shouldBe "/capmovie/update/TESTMOV/genre"
      }
    }

    "returns Internal Server Error" when {
      "admin attempts to delete invalid genre" in {
        when(connector.removeGenre(any(), any()))
          .thenReturn(Future(false))
        when(connector.readOne(any()))
          .thenReturn(Future.successful(None))
        val result = controller.updateDeleteGenre("TESTMOV", "")
          .apply(FakeRequest("GET", "/")
            .withSession("adminId" -> "testId"))
        status(result) shouldBe INTERNAL_SERVER_ERROR
      }
    }
  }

}

