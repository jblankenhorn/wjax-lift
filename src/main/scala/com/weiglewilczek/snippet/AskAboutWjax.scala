package com.weiglewilczek.snippet

import net.liftweb.http.{S, LiftScreen}

/**
 * Created by IntelliJ IDEA.
 * User: janblankenhorn
 * Date: 10.11.2010
 * Time: 14:13:54
 * To change this template use File | Settings | File Templates.
 */

object AskAboutWjax extends LiftScreen
{
  val name = field(S ? "Name", "", trim, valMinLen(2, "Bitte mehr als 2 Zeichen eingeben"))
  val like = field(S ? "You liked the wjax so far?", false)

  override def validations = youHaveToLike _ :: super.validations
    def youHaveToLike(): Errors = {
      if(!like)
        "You have to like it"
      else Nil
  }

  def finish() {
      S.notice("Danke " + name)
  }
}

