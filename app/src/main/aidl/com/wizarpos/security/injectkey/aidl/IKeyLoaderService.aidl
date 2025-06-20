// IKeyLoaderService.aidl
package com.wizarpos.security.injectkey.aidl;

// Declare any non-default types here with import statements

interface IKeyLoaderService {
    int importKeyInfo(in byte[] keyInfo);
    byte[] getAuthInfo();
}
