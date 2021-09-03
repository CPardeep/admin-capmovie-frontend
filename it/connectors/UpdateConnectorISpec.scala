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
import uk.gov.hmrc.capmovie.connectors.UpdateConnector
import uk.gov.hmrc.capmovie.models.Movie

class UpdateConnectorISpec extends AnyWordSpec with Matchers with GuiceOneServerPerSuite
  with WireMockHelper with BeforeAndAfterEach {

  lazy val connector: UpdateConnector = app.injector.instanceOf[UpdateConnector]

  override val wireMockPort: Int = 9009

  override def beforeEach(): Unit = startWireMock()

  override def afterEach(): Unit = stopWireMock()

  val id = "TESTID"

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

  "readOne" should {
    "return a movie" in {
      stubGet(s"/movie/${movie.id}", 200, Json.toJson(movie).toString())
      val result = connector.readOne(movie.id)
      await(result) shouldBe Some(movie)
    }
    "return None" in {
      stubGet(s"/movie/${movie.id}", 200, Json.toJson("{}").toString())
      val result = connector.readOne(movie.id)
      await(result) shouldBe None
    }
    "throw an exception" in {
      stubGet(s"/movie/${movie.id}", 404, Json.toJson("{}").toString())
      val result = await(connector.readOne(movie.id))
      result shouldBe None
    }
  }

  "removeGenre" should {
    "return true" when {
      "genre is removed" in {
        stubPatch(s"/movie/$id/remove-genre", 200, "")
        val result = connector.removeGenre("TESTID", "TestGenre")
        await(result) shouldBe true
      }
    }
    "return false" when {
      "genre is not removed" in {
        stubPatch(s"/movie/$id/remove-genre", 500, "")
        val result = connector.removeGenre("TESTID", "TestGenre")
        await(result) shouldBe false
      }
    }
  }

  "removeCast" should {
    "return true" when {
      "cast is removed" in {
        stubPatch(s"/movie/$id/remove-cast", 200, "")
        val result = connector.removeCast("TESTID", "TestCast")
        await(result) shouldBe true
      }
    }
    "return false" when {
      "cast is not removed" in {
        stubPatch(s"/movie/$id/remove-cast", 500, "")
        val result = connector.removeCast("TESTID", "TestCast")
        await(result) shouldBe false
      }
    }
  }

  "updateTitle" should {
    "return true" when {
      "title is updated" in {
        stubPatch(s"/movie/$id/update-title", 200, "")
        val result = connector.updateTitle("TESTID", "TestTitle")
        await(result) shouldBe true
      }
    }
    "return false" when {
      "title is not updated" in {
        stubPatch(s"/movie/$id/update-title", 500, "")
        val result = connector.updateTitle("TESTID", "TestTitle")
        await(result) shouldBe false
      }
    }
  }

  "updatePlot" should {
    "return true" when {
      "plot is updated" in {
        stubPatch(s"/movie/$id/update-plot", 200, "")
        val result = connector.updatePlot("TESTID", "TestPlot")
        await(result) shouldBe true
      }
    }
    "return false" when {
      "plot is not updated" in {
        stubPatch(s"/movie/$id/update-plot", 500, "")
        val result = connector.updatePlot("TESTID", "TestPlot")
        await(result) shouldBe false
      }
    }
  }

  "updatePoster" should {
    "return true" when {
      "poster is updated" in {
        stubPatch(s"/movie/$id/update-poster", 200, "")
        val result = connector.updatePoster("TESTID", "TestPoster")
        await(result) shouldBe true
      }
    }
    "return false" when {
      "poster is not updated" in {
        stubPatch(s"/movie/$id/update-poster", 500, "")
        val result = connector.updatePoster("TESTID", "TestPoster")
        await(result) shouldBe false
      }
    }
  }

  "updateRating" should {
    "return true" when {
      "ageRating is updated" in {
        stubPatch(s"/movie/$id/update-age-rating", 200, "")
        val result = connector.updateRating("TESTID", "TestRating")
        await(result) shouldBe true
      }
    }
    "return false" when {
      "ageRating is not updated" in {
        stubPatch(s"/movie/$id/update-age-rating", 500, "")
        val result = connector.updateRating("TESTID", "TestRating")
        await(result) shouldBe false
      }
    }
  }

  "updateGenre" should {
    "return true" when {
      "genre is updated" in {
        stubPatch(s"/movie/$id/update-genre", 200, "")
        val result = connector.updateGenre("TESTID", "TestGenre")
        await(result) shouldBe true
      }
    }
    "return false" when {
      "genre is not updated" in {
        stubPatch(s"/movie/$id/update-genre", 500, "")
        val result = connector.updateGenre("TESTID", "TestGenre")
        await(result) shouldBe false
      }
    }
  }

  "updateCast" should {
    "return true" when {
      "cast is updated" in {
        stubPatch(s"/movie/$id/update-cast", 200, "")
        val result = connector.updateCast("TESTID", "TestCast")
        await(result) shouldBe true
      }
    }
    "return false" when {
      "cast is not updated" in {
        stubPatch(s"/movie/$id/update-cast", 500, "")
        val result = connector.updateCast("TESTID", "TestCast")
        await(result) shouldBe false
      }
    }
  }
}
