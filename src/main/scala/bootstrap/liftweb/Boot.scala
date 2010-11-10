

package bootstrap.liftweb

import _root_.net.liftweb.http.{LiftRules, NotFoundAsTemplate, ParsePath}
import _root_.net.liftweb.sitemap.{SiteMap, Menu, Loc}
import _root_.net.liftweb.util.{ NamedPF }
import _root_.net.liftweb.mapper.{Schemifier, DB, StandardDBVendor, DefaultConnectionIdentifier}
import _root_.net.liftweb.util.{Props}
import _root_.net.liftweb.common.{Full}
import _root_.net.liftweb.http.{S}
import com.weiglewilczek.model._


class Boot {
  def boot {
  
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
        new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
        			               Props.get("db.url") openOr 
        			               "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
        			               Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // Use Lift's Mapper ORM to populate the database
    // you don't need to use Mapper to use Lift... use
    // any ORM you want
    Schemifier.schemify(true, Schemifier.infoF _, User)

  
    // where to search snippet
    LiftRules.addToPackages("com.weiglewilczek")

    // build sitemap
    val entries = List(Menu("Home") / "index") :::
                  // the User management menu items
                  User.sitemap :::
                  Nil
    
    LiftRules.uriNotFound.prepend(NamedPF("404handler"){
      case (req,failure) => NotFoundAsTemplate(
        ParsePath(List("exceptions","404"),"html",false,false))
    })
    
    LiftRules.setSiteMap(SiteMap(entries:_*))
    
    // set character encoding
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))
    
    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Make a transaction span the whole HTTP request
    S.addAround(DB.buildLoanWrapper)
    
  }
}