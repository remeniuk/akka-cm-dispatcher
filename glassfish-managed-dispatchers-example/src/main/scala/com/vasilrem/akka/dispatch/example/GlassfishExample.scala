/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vasilrem.akka.example

import com.vasilrem.akka.dispatch.ContainerManagedThreadPoolBuilder
import se.scalablesolutions.akka.actor.Actor
import se.scalablesolutions.akka.actor.Actor._
import se.scalablesolutions.akka.actor.ActorRegistry
import se.scalablesolutions.akka.dispatch.ExecutorBasedEventDrivenDispatcher
import se.scalablesolutions.akka.dispatch.ExecutorBasedEventDrivenWorkStealingDispatcher
import se.scalablesolutions.akka.dispatch.Future
import se.scalablesolutions.akka.dispatch.Futures
import se.scalablesolutions.akka.dispatch.MessageDispatcher
import se.scalablesolutions.akka.util.Logging

class MyActor extends Actor {
  def receive = {
    case message =>
      Thread.sleep(100)
      val response = "Actor [%s] received message [%s] on thread [%s]" format(self.id, message, Thread.currentThread.getId)
      log.info(response)
      self.reply_?(response)
  }
}

object GlassfishExample extends Logging{
    
  private def sendToActors(message: Any) =
    ActorRegistry.actorsFor[MyActor].map(actor => actor !!! message)


  def dispatcherInstance(dispatcherType: String, poolName: String) = {
    log.info("Starting dispatcher with Glassfish managed thread pool...")

    val dispatcher =
      dispatcherType match{
        case "event-based" => new ExecutorBasedEventDrivenDispatcher("name") with ContainerManagedThreadPoolBuilder
        case "work-stealing" => new ExecutorBasedEventDrivenWorkStealingDispatcher("name") with ContainerManagedThreadPoolBuilder
        case msg => throw new IllegalStateException("%s is not a valid dispatcher type!" format(msg))
      } 
    (if(poolName == "default")
      dispatcher.withGlassFishManagedDefaultThreadPool
    else
      dispatcher.withGlassFishManagedThreadPool(poolName))
    .buildThreadPool
    log.info("Successfully started dispatcher.")
    dispatcher
  }

  def createActorsDispatchedBy(dispatcher: MessageDispatcher) = {
    log.info("Starting actors...")
    (1 to 10).foreach(i => actorOf(new MyActor{
          self.id = "myactor-" + i
          self.dispatcher = dispatcher
        }).start)
    log.info("Successfully started actors.")
  }

  def stopActors = ActorRegistry.actorsFor[MyActor].foreach{actor =>
     log.info("Stopping actor " + actor.id)
     actor.stop
   }

  def simulate(dispatcherType: String, poolName: String) = {

    def getActorResponse(future: Future[_]) = future.result.getOrElse("")

    createActorsDispatchedBy(dispatcherInstance(dispatcherType, poolName))
    try{
      Futures.awaitMap[Nothing, Any]((1 to 10).map(sendToActors).flatten)(getActorResponse)
    } finally{
      stopActors
    }
    
  }

}