rootProject.name = "make-poc-be"

include(":app")

include(":app:common")
include(":app:server")

include(":app:auth")
include(":app:auth:core")
include(":app:auth:adapter")

include(":app:user")
include(":app:user:core")
include(":app:user:adapter")