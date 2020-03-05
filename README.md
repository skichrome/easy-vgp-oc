# easy-vgp-oc
An app to complete my OpenClassrooms course, this is the last project (11).

[![Build Status](https://jenkins.campeoltoni.fr/buildStatus/icon?job=easy-vgp-oc-pipeline)](https://jenkins.campeoltoni.fr/view/Pipelines/job/easy-vgp-oc-pipeline/)

## Release build configuration
To be able to compile this project with release build variant, you must sign the generated apk with
a keystore file. To do this, you have to create a file named 'keystore.properties' in root level of this
project, and put this content into it :

```properties
# This file is dedicated to Keystore credentials that must be not versioned to Git.
# To get this file working you need to tweak app level build.gradle file.

# Keystore credentials
STORE_FILE=ABSOLUTE_PATH_TO_YOUR_KEYSTORE_FILE
STORE_PASS=KEYSTORE_FILE_PASSWORD
KEY_ALIAS=KEY_ALIAS
KEY_PASS=KEY_PASSWORD
```