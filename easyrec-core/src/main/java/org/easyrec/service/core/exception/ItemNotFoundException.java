package org.easyrec.service.core.exception;

/**
 * @author: Fabian Salcher
 * @version: 2013-11-15
 */
public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(String message) {
        super(message);
    }
}
