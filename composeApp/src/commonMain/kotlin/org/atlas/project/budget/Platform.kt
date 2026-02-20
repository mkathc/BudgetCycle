package org.atlas.project.budget

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform