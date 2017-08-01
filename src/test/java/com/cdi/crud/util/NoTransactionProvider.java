package com.cdi.crud.util;

import org.jboss.arquillian.transaction.spi.provider.TransactionProvider;
import org.jboss.arquillian.transaction.spi.test.TransactionalTest;

/**
 * workaround para: https://github.com/arquillian/arquillian-extension-transaction/issues/14
 */
public class NoTransactionProvider implements TransactionProvider {
   @Override public void beginTransaction(TransactionalTest test) {}
   @Override public void commitTransaction(TransactionalTest test) {}
   @Override public void rollbackTransaction(TransactionalTest test) {}
}