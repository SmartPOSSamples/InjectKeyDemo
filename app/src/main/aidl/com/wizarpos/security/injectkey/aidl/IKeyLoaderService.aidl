// IKeyLoaderService.aidl
package com.wizarpos.security.injectkey.aidl;

// Declare any non-default types here with import statements

interface IKeyLoaderService {
//     static final int SUCCESS = 0;
//    static final int ERROR_CAN_NOT_OPEN_PINPAD = -1;
//    static final int ERROR_INJECT_PINPAD_FAILED = -2;
//    static final int ERROR_SYSTEM_NOT_SUPPORT = -3;
    int importKeyInfo(in byte[] keyInfo);
    byte[] getAuthInfo();
}
