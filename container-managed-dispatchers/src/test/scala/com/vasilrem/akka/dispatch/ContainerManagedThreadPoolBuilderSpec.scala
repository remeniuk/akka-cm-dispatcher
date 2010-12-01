/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vasilrem.akka.dispatch

import org.specs.Specification
import se.scalablesolutions.akka.dispatch.ExecutorBasedEventDrivenDispatcher

class ContainerManagedThreadPoolBuilderSpec extends Specification{

  "Container managed thread pool builder" should {
    "not allow configuring pool properties" in {
      val dispatcher = new ExecutorBasedEventDrivenDispatcher("name")
                                               with ContainerManagedThreadPoolBuilder
      dispatcher.withGlassFishManagedThreadPool("test-pool")
      .setCorePoolSize(16)
      .buildThreadPool must throwA[IllegalStateException]
    }
    "allow accessing container thread pool by id" in {
      val dispatcher = new ExecutorBasedEventDrivenDispatcher("name")
                                               with ContainerManagedThreadPoolBuilder
      (try{
          dispatcher.withGlassFishManagedThreadPool("test-pool")
          .buildThreadPool
          dispatcher.isShutdown
        } catch {
          case exception => exception.getMessage must include("Could not initialize GlassFishWorkManagerTaskExecutor because GlassFish API is not available")
        }) must notBe(true)
    }
  }

  "Custom thread pool" should {
    "be configurable" in {
      val dispatcher = new ExecutorBasedEventDrivenDispatcher("name")
      dispatcher.withNewThreadPoolWithLinkedBlockingQueueWithCapacity(100)
      .setCorePoolSize(16)
      .setMaxPoolSize(128)
      .setKeepAliveTimeInMillis(60000)
      .buildThreadPool
      dispatcher.isShutdown must notBe(true)
    }
  }

}
