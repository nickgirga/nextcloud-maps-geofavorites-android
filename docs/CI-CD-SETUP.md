# CI/CD Setup Guide

This guide explains how to set up automated signed releases for the Nextcloud Geofavorites Android app using GitHub Actions.

## Overview

The CI/CD pipeline automatically:
- Builds a signed release APK on every push to the `main` branch
- Creates a GitHub release with the APK attached
- Uses a secure keystore stored as GitHub secrets

## Prerequisites

- Access to the GitHub repository
- Java JDK installed locally (for keystore generation)
- Repository write permissions (to set secrets)

## Setup Instructions

### Step 1: Generate a Keystore

1. Navigate to the project root directory
2. Make the keystore generation script executable:
   ```bash
   chmod +x scripts/generate-keystore.sh
   ```

3. Run the script:
   ```bash
   cd scripts
   ./generate-keystore.sh
   ```

4. Follow the prompts to provide:
   - Key password (remember this!)
   - Store password (can be the same as key password)
   - Your name
   - Organization details
   - City, state, country

5. The script will create `release.keystore` and display instructions

### Step 2: Encode Keystore for GitHub

After the keystore is generated, encode it to base64:

```bash
base64 -w 0 release.keystore > keystore.txt
```

Or on macOS:
```bash
base64 -i release.keystore -o keystore.txt
```

This creates a text file with the base64-encoded keystore.

### Step 3: Configure GitHub Secrets

1. Go to your GitHub repository
2. Navigate to **Settings → Secrets and variables → Actions**
3. Click **New repository secret** and add the following secrets:

| Secret Name | Value | Description |
|------------|-------|-------------|
| `KEYSTORE_BASE64` | Contents of `keystore.txt` | Base64-encoded keystore file |
| `KEYSTORE_PASSWORD` | Your store password | Password for the keystore |
| `KEY_ALIAS` | `release-key` | Alias of the key (default from script) |
| `KEY_PASSWORD` | Your key password | Password for the key |

**IMPORTANT:** After adding the secrets, delete the `release.keystore` and `keystore.txt` files from your local machine for security!

```bash
rm release.keystore keystore.txt
```

### Step 4: Verify Workflow

1. Push a commit to the `main` branch
2. Go to **Actions** tab in your GitHub repository
3. Watch the "Build and Release APK" workflow run
4. Once complete, check the **Releases** section for the new release

## Workflow Details

### Triggers

The workflow runs on:
- Every push to the `main` branch
- Manual trigger via "Run workflow" button

### Build Process

1. **Checkout code**: Gets the latest code from the repository
2. **Setup Java**: Installs JDK 17
3. **Decode keystore**: Converts base64 secret back to keystore file
4. **Build APK**: Compiles and signs the release APK
5. **Extract version**: Gets version name from `app/build.gradle`
6. **Rename APK**: Names it `nextcloud-geofavorites-{version}.apk`
7. **Upload artifact**: Stores APK for 30 days
8. **Create release**: Creates a GitHub release with the APK

### Release Naming

Releases are named: `v{version}-{build_number}`

Example: `v0.4.0-42` (version 0.4.0, build number 42)

## Security Best Practices

### ✅ DO:
- Keep keystore passwords secure and never commit them
- Use GitHub secrets for all sensitive information
- Generate a unique keystore for production releases
- Back up your keystore securely (offline, encrypted)
- Delete local keystore files after encoding for GitHub

### ❌ DON'T:
- Commit keystore files to version control
- Share keystore passwords in plain text
- Use the same keystore for debug and release builds
- Store passwords in code or configuration files

## Troubleshooting

### Build Fails: "Keystore not found"

**Solution**: Verify that `KEYSTORE_BASE64` secret is set correctly. Re-encode and upload if needed.

### Build Fails: "Invalid keystore format"

**Solution**: Ensure base64 encoding was done correctly. Try encoding again:
```bash
base64 -w 0 release.keystore
```

### Build Fails: "Wrong password"

**Solution**: Verify `KEYSTORE_PASSWORD` and `KEY_PASSWORD` secrets match what you used when generating the keystore.

### Release Not Created

**Solution**: Check that:
1. Push was to the `main` branch
2. Build completed successfully
3. Version in `app/build.gradle` is correct

### APK Won't Install: "App not installed"

**Problem**: This can happen if you try to install over an existing app signed with a different keystore.

**Solution**: Uninstall the old version first, then install the new one.

## Manual Build (Alternative)

To build a signed release APK locally:

1. Generate keystore (if not done already)
2. Build with Gradle:
   ```bash
   ./gradlew assembleRelease \
     -Pandroid.injected.signing.store.file=./release.keystore \
     -Pandroid.injected.signing.store.password=YOUR_STORE_PASSWORD \
     -Pandroid.injected.signing.key.alias=release-key \
     -Pandroid.injected.signing.key.password=YOUR_KEY_PASSWORD
   ```

3. Find APK at: `app/build/outputs/apk/release/app-release.apk`

## Updating the Keystore

If you need to regenerate the keystore:

1. Run the generation script again
2. Re-encode to base64
3. Update the `KEYSTORE_BASE64` secret on GitHub
4. Update password secrets if changed

**WARNING**: Changing the keystore means users must uninstall the old app before installing the new version!

## Additional Resources

- [Android App Signing Documentation](https://developer.android.com/studio/publish/app-signing)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Gradle Signing Configuration](https://developer.android.com/studio/build/building-cmdline#sign_cmdline)

## Support

For issues with the CI/CD setup, please open an issue on the GitHub repository.
