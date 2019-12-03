const NodeRSA = require('node-rsa');

const key = new NodeRSA().generateKeyPair(512)

export function generateRSAKeyPair() {
    const publicKey = key.exportKey("pkcs1-public-pem");
    const privateKey = key.exportKey("pkcs8-private-pem");

    /* const publicKey = new Buffer(publicKeyDer , 'binary').toString('base64');
    const privateKey = new Buffer(privateKeyDer , 'binary').toString('base64'); */

    const keyPair = {
        publicKey: publicKey,
        privateKey: privateKey
    }

    return keyPair;
}

export function decryptTokenRSA(encryptedToken, privateKey) {

    const decryptedToken = key.decrypt(encryptedToken, 'utf8');


    return decryptedToken;

}