# Advanced Pipez Utilities

[![Build](https://github.com/yuuki1293/Advanced-Pipez-Utilities/actions/workflows/build.yml/badge.svg)](https://github.com/yuuki1293/Advanced-Pipez-Utilities/actions/workflows/build.yml)

Advanced Pipez Utilities is a Pipez add-on for Minecraft 1.21.1 on NeoForge. It adds GregTech-style manual pipe connections and an advanced pipe wrench.

## Features

- Pipes no longer connect indiscriminately to every adjacent compatible block.
- A newly placed pipe automatically connects only to the pipe or compatible block that was targeted during placement.
- Other sides that merely happen to be adjacent remain disconnected until configured with the wrench.
- Holding the Advanced Pipe Wrench gives pipes a full-block selection area.
- The wrench uses the shape and shading of Pipez's standard wrench with a blue tint.
- The targeted face displays the same blue pulsing grid and connection-state icons used by GTCEu Modern.
- Right-click toggles the connection in the selected direction.
- Shift-right-click toggles automatic extraction in the selected direction, connecting the side first when necessary.
- The center selects the near side, the four edges select their corresponding directions, and the corners select the far side.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.209 or later
- Pipez 1.21.1-1.2.31 or later
- Java 21

## Building

```shell
./gradlew build
```

The built mod is written to `build/libs/advanced_pipez_utilities-1.0.0.jar`.

Every push to `1.21.1` and every pull request targeting `1.21.1` is built automatically by GitHub Actions. Successful workflow runs provide the generated JAR as a downloadable artifact.

## Releasing

Releases are published automatically to Modrinth, CurseForge, and GitHub Releases when a version tag is pushed.

Configure these GitHub repository secrets before publishing:

- `MODRINTH_TOKEN`: a Modrinth personal access token with permission to create versions for project `dVgBH47t`
- `CURSEFORGE_TOKEN`: a CurseForge API token with permission to upload files to project `1655012`

`GITHUB_TOKEN` is provided automatically by GitHub Actions and does not need to be configured manually.

The tag must match `mod_version` and `minecraft_version` from `gradle.properties`. For example:

```shell
git tag v1.0.0+1.21.1
git push origin v1.0.0+1.21.1
```

The release workflow validates the tag, extracts the matching version section from `CHANGELOG.md`, builds the production JAR, and publishes the same artifact and changelog to all three services. Alpha, beta, and release-candidate versions are marked as prereleases automatically when `mod_version` contains `-alpha`, `-beta`, or `-rc`.

## License

The source code is available under the MIT License. The two GTCEu Modern overlay assets retain their original LGPL-3.0 license; see [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) for details.
