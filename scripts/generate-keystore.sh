#!/bin/bash

# Script to generate a new Android keystore for signing releases
# This keystore will be used for CI/CD automated builds

set -e

echo "========================================="
echo "Android Keystore Generation Script"
echo "========================================="
echo ""

# Configuration
KEYSTORE_FILE="release.keystore"
KEY_ALIAS="release-key"
VALIDITY_DAYS=10000

# Check if keystore already exists
if [ -f "$KEYSTORE_FILE" ]; then
    echo "WARNING: Keystore file '$KEYSTORE_FILE' already exists!"
    read -p "Do you want to overwrite it? (yes/no): " OVERWRITE
    if [ "$OVERWRITE" != "yes" ]; then
        echo "Aborted. Existing keystore preserved."
        exit 0
    fi
    rm -f "$KEYSTORE_FILE"
fi

# Prompt for keystore details
echo "Please provide the following information for the keystore:"
echo ""

read -p "Key password (will be hidden): " -s KEY_PASSWORD
echo ""
read -p "Confirm key password: " -s KEY_PASSWORD_CONFIRM
echo ""

if [ "$KEY_PASSWORD" != "$KEY_PASSWORD_CONFIRM" ]; then
    echo "ERROR: Passwords do not match!"
    exit 1
fi

read -p "Store password (will be hidden, can be same as key password): " -s STORE_PASSWORD
echo ""
read -p "Confirm store password: " -s STORE_PASSWORD_CONFIRM
echo ""

if [ "$STORE_PASSWORD" != "$STORE_PASSWORD_CONFIRM" ]; then
    echo "ERROR: Passwords do not match!"
    exit 1
fi

read -p "Your name: " DNAME_CN
read -p "Organizational unit (e.g., Development): " DNAME_OU
read -p "Organization name: " DNAME_O
read -p "City: " DNAME_L
read -p "State/Province: " DNAME_ST
read -p "Country code (2 letters, e.g., US): " DNAME_C

echo ""
echo "Generating keystore..."
echo ""

# Generate the keystore
keytool -genkey -v \
    -keystore "$KEYSTORE_FILE" \
    -alias "$KEY_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity $VALIDITY_DAYS \
    -storepass "$STORE_PASSWORD" \
    -keypass "$KEY_PASSWORD" \
    -dname "CN=$DNAME_CN, OU=$DNAME_OU, O=$DNAME_O, L=$DNAME_L, ST=$DNAME_ST, C=$DNAME_C"

echo ""
echo "========================================="
echo "Keystore generated successfully!"
echo "========================================="
echo ""
echo "Keystore file: $KEYSTORE_FILE"
echo "Key alias: $KEY_ALIAS"
echo ""
echo "IMPORTANT: Store these credentials securely!"
echo ""
echo "For GitHub Actions, you need to set these secrets:"
echo "1. KEYSTORE_BASE64: Base64-encoded keystore file"
echo "2. KEYSTORE_PASSWORD: $STORE_PASSWORD"
echo "3. KEY_ALIAS: $KEY_ALIAS"
echo "4. KEY_PASSWORD: $KEY_PASSWORD"
echo ""
echo "To generate base64 keystore for GitHub secrets:"
echo "  base64 -w 0 $KEYSTORE_FILE"
echo ""
echo "========================================="
