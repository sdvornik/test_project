package shared

import play.api.libs.json._

case class PasswordRowKey(value: String) extends AnyVal {
  override def toString(): String = value
}

object PasswordRowKey {
  implicit val format: Format[PasswordRowKey] = Json.valueFormat[PasswordRowKey]
}
