# App secret generation

In order to allow the application to perform successful Dynamic Client Registration a fingerprint of the app needs to be provided to the client configuration on the Token Server.

> Please note that each platform uses its own dedicated BinaryHashCalculator instance.

## Android

Since Android SDK version 3.02.01 it uses a calculated binary checksum as a application secret when responding to OCRA challenge in DCR flow. Such approach provides additional security protecting clients app against tampering/modification.

#### Calculating value
Navigate to the 'binaryhashcalculator' folder and from the command line execute:
```bash
java com.onegini.mobile.hashCalc.HashCalculator PATH_TO_BINARY_FILE
```

If the provided path is valid the program will print the calculated hash value. 
```bash
Calculated hash - a491d0374840ac684d6bcb4bf9fc93ee4d9731dbe2996b5a1db2313efb42b7e
```

## iOS

Since version 3.02.00 iOS-SDK calculates binary fingerprint and uses it as an application secret when responding to OCRA challenge in DCR flow. Such approach provides additional security protecting client's application against tampering/modification.

#### Calculating value
Navigate to 'binaryhashcalculator' folder and from the command line execute:
```bash
java -cp . com/onegini/mobile/hashCalc/iOS/HashCalculator {PATH_TO_APPLICATION_BINARY}

```

PATH_TO_APPLICATION_BINARY - path pointing to application binary equal to one ${BUILT_PRODUCTS_DIR}/${PROJECT_NAME}.app/${PROJECT_NAME} accessed from the xCode.


If provided path is valid the program will print calculated hash value. 
```bash
Calculated hash - c96de7fe7fe80a5e73b9a5af90afcc8a639506688914f1ffeb00d62f47315ea2