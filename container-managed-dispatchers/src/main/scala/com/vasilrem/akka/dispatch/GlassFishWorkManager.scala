/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vasilrem.akka.dispatch

import org.springframework.jca.work.glassfish.GlassFishWorkManagerTaskExecutor

class GlassFishWorkManager extends GlassFishWorkManagerTaskExecutor {
  override def getDefaultWorkManager = super.getDefaultWorkManager

  def useThreadPool(poolName: String) = {
    super.setThreadPoolName(poolName)
    this
  }
}