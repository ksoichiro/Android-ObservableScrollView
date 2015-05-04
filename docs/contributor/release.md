# Release

This is just a memo for me.

## Bump up the version

Edit `gradle.properties` and commit.

```
VERSION_NAME=x.x.x
```

If you set the version suffix `-SNAPSHOT`, it will be handled as a snapshot.

## Commit changes

Before the final confirmation and release, make sure that
there are no uncommitted changes in your repository.

## Confirm test

Check if all tests pass at your machine.  

```sh
./gradlew clean :library:assemble :library:connectedCheck
```

## Upload archives

### Set the credentials

If this is the first time for uploading archives,
you must write credentials to `~/.gradle/gradle.properties`.

```
NEXUS_USERNAME=xxxx
NEXUS_PASSWORD=xxxx
```

### Upload

```sh
./gradlew :library:uploadArchives
```

Or you can clean, test and upload at once.

```sh
./gradlew clean :library:assemble :library:connectedCheck :library:uploadArchives
```

## Close the repo on Sonatype

Open [Sonatype Nexus Professional](https://oss.sonatype.org/) on your browser,
find your repo and close it.
If there are no problems, repository will be staged to the URL like this.

```
https://oss.sonatype.org/content/repositories/TEMPORARY_REPO_NAME/GROUP/ARTIFACT_ID/VERSION/ARTIFACT_ID-VERSION.aar
```

You will receive an email with title "Nexus: Staging Completed",
you can know the appropriate URL from the email.

Set the URL to the `repositories` in your `build.gradle`, and sync.

```gradle
repositories {
  maven {
    url uri('https://oss.sonatype.org/content/repositories/TEMPORARY_REPO_NAME/')
  }
}
```

## Release

After that, click "Release" to promote.
If it's processed successfully, you will receive an email with title "Nexus: Promotion Completed".

It takes 3 or 4 hours to be synced to the Maven Central Repository.

## Create tag, update synced version and push

If it's successfully published to the Maven Central Repository,

* create a tag like `v1.5.0`
* update the `SYNCED_VERSION_NAME` in `gradle.properties`
* push the master branch and the tag to GitHub
