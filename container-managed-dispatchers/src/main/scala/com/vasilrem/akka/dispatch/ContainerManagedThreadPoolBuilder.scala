/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vasilrem.akka.dispatch

import javax.resource.spi.work.WorkManager
import org.springframework.jca.work.WorkManagerTaskExecutor
import scala.actors.threadpool.RejectedExecutionHandler
import se.scalablesolutions.akka.dispatch.ThreadPoolBuilder

trait ContainerManagedThreadPoolBuilder{
  self: ThreadPoolBuilder =>

  protected var managedThreadPoolBuilder: WorkManagerExecutorService = _

  /**
   * Use server managed thread pool (JCA WorkManager). WorkManager is identified
   * either be a fully qualified JNDI name, or the JNDI name relative to the
   * current environment naming context if "resourceRef" is set to "true".
   * @param workManagerName WorkManager JNDI name
   * @param resourceRef defines whether JNDI name should be relative to the
   * current environment naming context or not
   * @see org.springframework.jca.work.WorkManagerTaskExecutor#setWorkManagerName()
   */
  def withAppServerWorkManager(workManagerName: String, resourceRef: Boolean = false) = synchronized {
    val workManagerTaskExecutor = new WorkManagerTaskExecutor()
    workManagerTaskExecutor.setWorkManagerName(workManagerName)
    workManagerTaskExecutor.setResourceRef(resourceRef)
    withWorkManagerTaskExecutor(workManagerTaskExecutor)
  }

  /**
   * Use specific GlassFish thread pool to talk to.
   * <p>The thread pool name matches the resource adapter name
   * in default RAR deployment scenarios.<br/><br/>
   * @param poolName thread pool name
   * @see org.springframework.jca.work.glassfish.GlassFishWorkManagerTaskExecutor#setThreadPoolName()
   */
  def withGlassFishManagedThreadPool(poolName: String) = synchronized {
    withWorkManagerTaskExecutor(
      new GlassFishWorkManager()
      .useThreadPool(poolName)
    )
  }

  /**
   * Use GlassFish's default thread pool.<br/><br/>
   * @see org.springframework.jca.work.glassfish.GlassFishWorkManagerTaskExecutor#getDefaultWorkManager()
   */
  def withGlassFishManagedDefaultThreadPool = synchronized {
    withAppServerWorkManager(
      new GlassFishWorkManager()
      .getDefaultWorkManager()
    )
  }

  /**
   * Use specific JBossWorkManagerMBean to talk to,
   * through its JMX object name.
   * <p>The default MBean name is "jboss.jca:service=WorkManager".
   * @param mbeanName WorkManager MBean name
   * @see org.springframework.jca.work.glassfish.JBossWorkManagerTaskExecutor#setWorkManagerMBeanName()
   */
  def withJBossWorkManagerMBean(mbeanName: String) = synchronized {
    withWorkManagerTaskExecutor(
      new JBossWorkManager()
      .useWorkManagerMBean(mbeanName)
    )
  }
  
  /**
   * Use default JBoss JCA WorkManager obtained through a JMX lookup
   * @see org.springframework.jca.work.glassfish.JBossWorkManagerTaskExecutor#getDefaultWorkManager()
   */
  def withJBossManagedDefaultThreadPool = synchronized {
    withAppServerWorkManager(
      new JBossWorkManager()
      .getDefaultWorkManager()
    )
  }

  protected def withWorkManagerTaskExecutor(workManager: WorkManagerTaskExecutor) = synchronized {
    ensureNotActive
    managedThreadPoolBuilder = new WorkManagerExecutorService(workManager)
    this
  }

  protected def withAppServerWorkManager(workManager: WorkManager) = synchronized {
    ensureNotActive
    val workManagerTaskExecutor = new WorkManagerTaskExecutor()
    workManagerTaskExecutor.setWorkManager(workManager)
    managedThreadPoolBuilder = new WorkManagerExecutorService(workManagerTaskExecutor)
    this
  }

  override def setCorePoolSize(size: Int): ThreadPoolBuilder = throw new IllegalStateException(
    "Can't set 'core pool size' on container managed pool")

  override def setMaxPoolSize(size: Int): ThreadPoolBuilder = throw new IllegalStateException(
    "Can't set 'max pool size' on container managed pool")

  override def setKeepAliveTimeInMillis(time: Long): ThreadPoolBuilder = throw new IllegalStateException(
    "Can't set 'keep alive time' on container managed pool")

  def setRejectionPolicy(policy: RejectedExecutionHandler): ThreadPoolBuilder = throw new IllegalStateException(
    "Can't set 'rejection  policy' on container managed pool")

  override def buildThreadPool = synchronized {
    ensureNotActive
    executor = managedThreadPoolBuilder
  }

}
