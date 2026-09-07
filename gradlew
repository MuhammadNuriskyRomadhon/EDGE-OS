#!/usr/bin/env sh
set -eu
if command -v gradle >/dev/null 2>&1; then exec gradle "$@"; fi
printf '%s\n' 'Gradle wrapper bootstrap: install Gradle or generate the standard wrapper with Gradle before local execution.' >&2
exit 1
