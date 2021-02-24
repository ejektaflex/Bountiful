package io.ejekta.kambrik.api.file

enum class KambrikReadFailMode {
    /**
     * If a file cannot be read, it will use the default data instead
     */
    LEAVE,

    /**
     * If a file cannot be read, it will overwrite the file with default data and then use it
     */
    OVERWRITE
}