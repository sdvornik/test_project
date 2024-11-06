package dao.model

import shared.{PasswordRowKey, UtcInstant}

case class DbPasswordRow(
    id: PasswordRowKey,
    name: String,
    password: String,
    comment: Option[String],
    created: UtcInstant,
    deleted: Option[UtcInstant]
)

case class DbPasswordRowUpdate(
    id: PasswordRowKey,
    name: Option[String],
    password: Option[String],
    comment: Option[String]
)
