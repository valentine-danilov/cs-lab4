const CryptoJS = require("crypto-js");

export function encryptAES_CBC(key, iv, source) {

    const keyBytes = CryptoJS.enc.Base64.parse(key)
    const ivBytes = CryptoJS.enc.Base64.parse(iv)

    let encryptedSource = CryptoJS.AES.encrypt(source, keyBytes, {
        iv: ivBytes,
        padding: CryptoJS.pad.Pkcs7,
        mode: CryptoJS.mode.CBC
    })

    return encryptedSource.toString();
}

export function decryptAES_CBC(key, iv, enc) {

    const keyBytes = CryptoJS.enc.Base64.parse(key)
    const ivBytes = CryptoJS.enc.Base64.parse(iv)

    let decrypted = CryptoJS.AES.decrypt(
        {
            ciphertext: CryptoJS.enc.Base64.parse(enc),
            salt: ""
        },
        keyBytes,
        {
            iv: ivBytes,
        })

    const rawResult = new Buffer(decrypted.toString(), 'hex');

    return rawResult.toString();

}