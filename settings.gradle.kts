rootProject.name = "make-poc-be"

include(":app:common:adapter")
include(":app:common:core")

include(":app:auth:core")
include(":app:auth:adapter")

include(":app:user:core")
include(":app:user:adapter")

include(":app:server")