package ejektaflex.bountiful.api


/**
 * [BountifulAPIProvider] is a wrapper around an empty instance of the Bountiful API.
 * At the bountyTime of mod construction, it gets replaced by Bountiful's internal API
 * implementation.
 */
open class BountifulAPIProvider : IBountifulAPI by api {
    companion object {
        lateinit var api: IBountifulAPI
        /**
         * Used to change the BountifulAPI implementation.
         * @param newAPI The new API implementation to use.
         */
        fun changeAPI(newAPI: IBountifulAPI) {
            api = newAPI
        }
    }
}
