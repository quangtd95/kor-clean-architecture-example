rootProject.name = "fun-gpt-ktor-clean-architecture"

include(":app:common:adapter")
include(":app:common:core")

include(":app:auth:core")
include(":app:auth:adapter")

include(":app:profile:core")
include(":app:profile:adapter")

include(":app:conversation:core")
include(":app:conversation:adapter")

include(":app:server")