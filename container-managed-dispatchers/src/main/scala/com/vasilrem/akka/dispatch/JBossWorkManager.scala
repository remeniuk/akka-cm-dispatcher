/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vasilrem.akka.dispatch

import org.springframework.jca.work.jboss.JBossWorkManagerTaskExecutor

class JBossWorkManager extends JBossWorkManagerTaskExecutor {
  override def getDefaultWorkManager = super.getDefaultWorkManager

  def useWorkManagerMBean(mbeanName: String) = {
    super.setWorkManagerMBeanName(mbeanName)
    this
  }
}
