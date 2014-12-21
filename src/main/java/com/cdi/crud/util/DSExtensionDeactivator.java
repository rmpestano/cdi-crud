package com.cdi.crud.util;

import org.apache.deltaspike.core.impl.config.ConfigurationExtension;
import org.apache.deltaspike.core.impl.jmx.MBeanExtension;
import org.apache.deltaspike.core.impl.message.MessageBundleExtension;
import org.apache.deltaspike.core.spi.activation.ClassDeactivator;
import org.apache.deltaspike.core.spi.activation.Deactivatable;

/**
 * Created by rmpestano on 11/1/14.
 */
public class DSExtensionDeactivator implements ClassDeactivator {

    private static final long serialVersionUID = 1L;


    @Override
    public Boolean isActivated(Class<? extends Deactivatable> targetClass)
    {
        if (
                targetClass.equals(MessageBundleExtension.class) ||
                targetClass.equals(ConfigurationExtension.class) ||
                targetClass.equals(MBeanExtension.class)) {

            return Boolean.FALSE;
        }
        return null;
    }
}
