package com.humanharvest.organz;

/**
 * Any object that can be validated using an ETag
 */
@FunctionalInterface
public interface ConcurrencyControlledEntity {

    /**
     * Retrieve the ETag
     * @return The hashed ETag value
     */
    String getETag();
}
