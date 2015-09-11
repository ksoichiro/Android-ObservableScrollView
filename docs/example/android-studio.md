# Build on Android Studio

This library and samples basically support Android Studio and Gradle.  
(Actually, I'm using them to develop this library.)

If you're an Eclipse user, you can skip and go to the next topic.

## Prerequisites

Please [check here](../../docs/reference/environment.md) to see if your enviroment satisfies the prerequisites for building the app.

## Instructions

### Get the source codes

Get the source code of the library and example app, by cloning git repository or downloading archives.

If you use git, execute the following command in your workspace directory.

```
$ git clone https://github.com/ksoichiro/Android-ObservableScrollView.git
```

If you are using Windows, try it on GitBash or Cygwin or something that supports git.

### Import the project to Android Studio

1. Select File &gt; New &gt; Import Project... from the menu.
1. Select the directory that is cloned. If you can't see your cloned directory, click "Refresh" icon and find it.
1. Android Studio will import the project and build it. This might take minutes to complete. Even when the project window is opened, wait until the Gradle tasks are finished and indexed.
1. Click "Run 'samples'" button to build and launch the app. Don't forget to connect your devices to your machine.

### Build and install using Gradle

If you just want to install the app to your device, you don't have to import project to Android Studio.

After cloning the project, connect your device to your machine, and execute the following command on the terminal.

Mac / Linux / Git Bash, Cygwin on Windows:

```sh
$ cd /path/to/Android-ObservableScrollView
$ ./gradlew installDevDebug
```

Windows (Command prompt):

```sh
> cd C:\path\to\Android-ObservableScrollView
> gradlew installDevDebug
```


[Next: Build on Eclipse &raquo;](../../docs/example/eclipse.md)
