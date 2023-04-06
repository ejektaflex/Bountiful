package io.ejekta.bountiful.bridge

import java.util.ServiceLoader

class Bountybridge {
    companion object : BountifulSharedApi by ServiceLoader
        .load(BountifulSharedApi::class.java)
        .findFirst()
        .orElseThrow()

}
