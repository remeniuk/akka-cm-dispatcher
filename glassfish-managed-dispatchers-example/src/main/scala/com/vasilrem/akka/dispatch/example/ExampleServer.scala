/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vasilrem.akka.dispatch.example

import org.scalatra._
import com.vasilrem.akka.example.GlassfishExample

class ExampleServer extends ScalatraServlet {

  get("/glassfish/:dispatcherType/:poolName") {
    <ul>{
        GlassfishExample.simulate(params("dispatcherType"), params("poolName"))
        .map(actorResponse => <li>{actorResponse}</li>)
      }</ul>
  }

  get("/dispatcherTypes") {
    <ul>     
      <li>event-based</li>
      <li>work-stealing</li>     
    </ul>
  }

}

