# Instruction for releasing a new version of cordova-plugin-nearby-connections

1. Make sure your local master branch is up to date and clean. `git fetch origin && git checkout master && git merge origin/master && git status`.
2. Complete an entry in `CHANGELOG.md` for the release.
3. Change the version number in package.json and plugin.xml.
4. Git commit with a git commit message of the same release number.
5. Git tag with the same name as the release number.
6. Git push the master branch, git push the tag.
7. Draft a new release on Github of the same tag name using that tag. Use the CHANGELOG notes.
8. Release to npm.
