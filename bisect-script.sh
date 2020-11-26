#!/bin/sh

# adopted from https://git-scm.com/docs/git-bisect

# tweak the working tree by cherry picking the test case
# and then attempt a build
if git cherrypick --no-commit bisect-testcase
then
  # run project specific test and report its status
  ./gradlew :spock-specs:test --tests BisectSpec
  status=$?
else
  # tell the caller this is untestable
  status=125
fi

# undo the tweak to allow clean flipping to the next commit
git reset --hard

# return control
exit $status
