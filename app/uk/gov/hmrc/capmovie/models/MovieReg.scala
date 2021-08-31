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

import play.api.data.Form
import play.api.data.Forms.{mapping, text}
import play.api.libs.json.{Json, OFormat}

case class MovieReg(adminId: String,
                    plot: Option[String] = None,
                    genres: List[String] = List(),
                    rated: Option[String] = None,
                    cast: Option[List[String]] = None,
                    poster: Option[String] = None,
                    title: Option[String] = None)

object MovieReg {
  implicit val format: OFormat[MovieReg] = Json.format[MovieReg]
}

case class MovieRegTitle(title: String)

object MovieRegTitle {
  val form: Form[MovieRegTitle] = Form(
    mapping("title" -> text.verifying("cannot leave empty", _.nonEmpty)
    )(MovieRegTitle.apply)(MovieRegTitle.unapply)
  )
}

case class MovieRegPoster(poster: String)

object MovieRegPoster {
  val form: Form[MovieRegPoster] = Form(
    mapping("poster" -> text.verifying("cannot leave empty", _.nonEmpty)
    )(MovieRegPoster.apply)(MovieRegPoster.unapply)
  )
}

case class MovieRegPlot(plot: String)

object MovieRegPlot {
  val form: Form[MovieRegPlot] = Form(
    mapping("plot" -> text.verifying("cannot leave empty", _.nonEmpty)
    )(MovieRegPlot.apply)(MovieRegPlot.unapply)
  )
}

case class MovieRegRating(rating: String)

object MovieRegRating {
  val form: Form[MovieRegRating] = Form(
    mapping("rated" -> text.verifying("cannot leave empty", _.nonEmpty)
    )(MovieRegRating.apply)(MovieRegRating.unapply)
  )

}

case class MovieRegGenres(genres: String)

object MovieRegGenres {
  val form: Form[MovieRegGenres] = Form(
    mapping("genres" -> text.verifying("Enter a genre", _.nonEmpty)
    )(MovieRegGenres.apply)(MovieRegGenres.unapply)
  )
}


