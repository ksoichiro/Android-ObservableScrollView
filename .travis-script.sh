#!/bin/bash

echo "TEST_TARGET=${TEST_TARGET}"
echo "TRAVIS_PULL_REQUEST=${TRAVIS_PULL_REQUEST}"
echo "TRAVIS_BRANCH=${TRAVIS_BRANCH}"

if [ "$TEST_TARGET" = "android" ]; then
  # Release build type is only for Google Play store currently,
  # which resolve dependency from Maven Central.
  # This causes build errors while developing a new feature, so disable release build.
  ./gradlew --full-stacktrace assembleDevDebug :library:connectedCheck
elif [ "$TEST_TARGET" = "website" ]; then
  if [ "$TRAVIS_PULL_REQUEST" = "false" ] && [ "$TRAVIS_BRANCH" = "master" ] && [ ! -z "$GH_TOKEN" ]; then
    echo "Update website..."
    pushd website > /dev/null 2>&1
    npm run deploy
    popd > /dev/null 2>&1
  fi
fi
