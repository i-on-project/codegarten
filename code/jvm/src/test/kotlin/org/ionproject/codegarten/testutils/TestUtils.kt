package org.ionproject.codegarten.testutils

import java.io.File

object TestUtils {
    private val classLoader = javaClass.classLoader

    fun getResourceFile(resourceName: String) =
        File(classLoader.getResource(resourceName)!!.toURI())

}