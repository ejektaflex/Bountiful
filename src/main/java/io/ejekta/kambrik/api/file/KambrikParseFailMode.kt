package io.ejekta.kambrik.api.file

/**
 * Determines what to do when a file cannot be parsed correctly
 */
enum class KambrikParseFailMode {
    /**
     * If a file cannot be read, it will use the default data instead
     */
    LEAVE,

    /**
     * If a file cannot be read, it will overwrite the file with default data and then use it
     */
    OVERWRITE
}