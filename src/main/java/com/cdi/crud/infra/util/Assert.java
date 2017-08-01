
package com.cdi.crud.infra.util;


import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * Created by rafael-pestano on 10/06/2014.
 * utility class for assertions
 */
public class Assert implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Assert() {

    }

    /**
     * @return TRUE assertion when given objects is not null, FALSE otherwise
     */
    public static boolean has(Object object) {
        return object != null;
    }

    /**
     * @return TRUE when given text has any character, FALSE otherwise
     */
    public static boolean has(String text) {
        if (text != null && text.trim().length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * @return TRUE when given text contains the given substring, FALSE otherwise
     */
    public static boolean contains(String textToSearch, String substring) {
        if (textToSearch != null && textToSearch.contains(substring)) {
            return true;
        }
        return false;
    }

    /**
     * @return TRUE when given array has elements; that is, it must not be {@code null} and must
     * have at least one element. FALSE otherwise
     */
    public static boolean has(Object[] array) {
        if (array == null || array.length == 0) {
            return false;
        }
        for (Object element : array) {
            if (element != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return TRUE when given collection has elements; that is, it must not be {@code null} and
     * must have at least one element. @return FALSE otherwise
     */
    public static boolean has(Collection<?> collection) {
        if (collection != null && !collection.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return TRUE if given Map has entries; that is, it must not be {@code null} and must have at
     * least one entry. Queue FALSE otherwise
     */
    public static boolean has(Map<?, ?> map) {
        if (map == null) {
            return false;
        }
        if (has(map.entrySet().toArray())) {
            return true;
        }
        return false;
    }

}