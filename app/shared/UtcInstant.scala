package shared

import play.api.libs.json._

import java.time.{Instant, LocalDateTime, OffsetDateTime, ZoneId, ZoneOffset}
import java.time.format.DateTimeFormatter
import UtcInstant._

case class UtcInstant(value: Long) extends AnyVal {
  override def toString(): String = OffsetDateTime
    .ofInstant(Instant.ofEpochMilli(value), ZoneId.of("UTC"))
    .format(formatter)
}

object UtcInstant {

  def now: UtcInstant = UtcInstant(Instant.now().toEpochMilli)

  val formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

  def fromString(s: String): UtcInstant = UtcInstant(
    LocalDateTime.parse(s, formatter).toInstant(ZoneOffset.UTC).toEpochMilli
  )

  implicit val format: Format[UtcInstant] =
    Format.of[String].bimap(fromString, _.toString())

}
