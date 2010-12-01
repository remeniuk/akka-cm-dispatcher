/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vasilrem.akka.dispatch

import java.util.concurrent._
import org.springframework.jca.work.WorkManagerTaskExecutor
import se.scalablesolutions.akka.util.Logging
import scala.collection.JavaConversions._


class WorkManagerExecutorService(workManagerTaskExecutor: WorkManagerTaskExecutor) extends AbstractExecutorService with Logging{

  def execute(command: Runnable) = workManagerTaskExecutor.execute(command)

  def shutdown = log.debug("Container-managed thread pool can't be shut down")

  def shutdownNow = {log.debug("Container-managed thread pool can't be shut down"); List[Runnable]()}

  def isShutdown = false

  def isTerminated = false

  def awaitTermination(l: Long, timeUnit: TimeUnit) = {log.debug("Container-managed thread pool can't be terminated"); true}

}
