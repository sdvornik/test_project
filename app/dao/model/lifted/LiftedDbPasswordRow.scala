package dao.model.lifted

import shared.{PasswordRowKey, UtcInstant}
import slick.lifted.Rep

case class LiftedDbPasswordRow(
    id: Rep[PasswordRowKey],
    name: Rep[String],
    password: Rep[String],
    comment: Rep[Option[String]],
    created: Rep[UtcInstant],
    deleted: Rep[Option[UtcInstant]]
)
