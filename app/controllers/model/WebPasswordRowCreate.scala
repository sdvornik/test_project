package controllers.model

import cats.implicits.catsSyntaxTuple3Semigroupal
import dao.model.{DbPasswordRow, DbPasswordRowUpdate}
import play.api.libs.json.{Format, Json}
import shared.{PasswordRowKey, UtcInstant}

case class WebPasswordRow(
    id: PasswordRowKey,
    name: String,
    password: String,
    comment: Option[String],
    created: UtcInstant,
    deleted: Option[UtcInstant]
)

object WebPasswordRow {
  def apply(row: DbPasswordRow): WebPasswordRow = {
    import row._
    WebPasswordRow(
      id,
      name,
      password,
      comment,
      created,
      deleted
    )
  }

  implicit val format: Format[WebPasswordRow] = Json.format[WebPasswordRow]
}

case class WebPasswordRowCreate(
    id: PasswordRowKey,
    name: String,
    password: String,
    comment: Option[String]
) {
  def toDb: DbPasswordRow = DbPasswordRow(
    id = id,
    name = name,
    password = password,
    comment = comment,
    created = UtcInstant.now,
    deleted = None
  )
}

object WebPasswordRowCreate {
  def apply(map: Map[String, String]): Option[WebPasswordRowCreate] = {
    (map.get("id"), map.get("name"), map.get("password")) mapN {
      case (id, name, password) =>
        WebPasswordRowCreate(
          id = PasswordRowKey(id),
          name = name,
          password = password,
          comment = map.get("comment")
        )
    }
  }

  implicit val format: Format[WebPasswordRowCreate] =
    Json.format[WebPasswordRowCreate]
}

case class WebPasswordRowUpdate(
    id: PasswordRowKey,
    name: Option[String],
    password: Option[String],
    comment: Option[String]
) {
  def toDb: DbPasswordRowUpdate = DbPasswordRowUpdate(
    id = id,
    name = name,
    password = password,
    comment = comment
  )
}

object WebPasswordRowUpdate {
  implicit val format: Format[WebPasswordRowUpdate] =
    Json.format[WebPasswordRowUpdate]
}
