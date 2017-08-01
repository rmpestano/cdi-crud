package com.cdi.crud.util;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.transaction.spi.provider.TransactionProvider;

/**
 * workaround para: https://github.com/arquillian/arquillian-extension-transaction/issues/14
 */
public class NoTxWorkAround implements LoadableExtension {
   @Override
   public void register(ExtensionBuilder builder) {
      builder.service(TransactionProvider.class, NoTransactionProvider.class);
   }
}