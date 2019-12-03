import React from 'react'
import Form from './Form';
import { Switch, Route, Redirect } from 'react-router-dom';
import { encryptAES_CBC, decryptAES_CBC } from './AESUtils'
import FileForm from './FileForm';
import EmailInput from './EmailInput';
const NodeRSA = require('node-rsa');

export default class Main extends React.Component {
    constructor(props) {
        super(props);

        const key = new NodeRSA().generateKeyPair(512)
        key.setOptions({ encryptionScheme: 'pkcs1' });

        this.state = {
            loggedUser: {
                login: "",
                password: "",
                email: "",
                verificationCode: null,
            },
            file: {
                fileName: null,
                content: null,
            },
            publicKey: null,
            privateKey: null,
            token: null,
            iv: null,
            rsa: key,
            loggedIn: false,
            requestId: null,
            credentialsVerified: false,
            emailVerified: false,
            emailSent: false
        }
    }

    /* --- Communication with API --- */

    async loadFile(fileName, fileContent) {

        const body = {
            fileName: this.encryptAES(fileName),
            content: this.encryptAES(fileContent)
        }

        console.log("Successfully encrypted fileName and fileContent");

        const response = await fetch("http://localhost:9003/api/file/upload", {
            headers: {
                'Content-Type': 'application/json',
                "REQUEST_ID": this.state.requestId
            },
            method: "POST",
            credentials: "include",
            body: JSON.stringify(body)
        })

        if (response.status === 200) {
            const json = await response.json();
            const requestId = json.attributes.REQUEST_ID;

            console.log("Successfully uploaded file");
            alert("Successfully uploaded file")
            this.setState({
                requestId: requestId
            })
        }
    }

    async fetchFile(fileName) {

        const body = {
            fileName: this.encryptAES(fileName)
        }

        const response = await fetch("http://localhost:9003/api/file/get", {
            headers: {
                'Content-Type': 'application/json',
                "REQUEST_ID": this.state.requestId
            },
            method: "POST",
            credentials: "include",
            body: JSON.stringify(body)
        })

        if (response.status === 401) {
            alert("401 Unauthorized");
            return;
        }

        const json = await response.json();

        if (response.status === 200) {

            const requestId = json.attributes.REQUEST_ID;

            this.setState({
                requestId: requestId
            })

            const text = json.attributes.text;

            console.log("Successfully requested encrypted text")

            const decr = this.decryptAES(text);

            console.log("Succesfully decrypted reqeusted cipher text")

            let file = this.state.file;
            file.content = decr;

            this.setState({
                file: file
            });
        } else {
            alert(json.message)
        }

    }

    async onSubmit() {

        const publicKey = this.state.publicKey;
        if (publicKey) {

            const body = {
                key: publicKey
            };

            const response = await fetch("http://localhost:9003/auth/token/request", {
                method: "POST",
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: "include",
                body: JSON.stringify(body)
            });


            const json = await response.json();

            console.log("Successfully requested encrypted token");

            const token = json.attributes.token;
            const iv = json.attributes.iv;
            let requestId = json.attributes.REQUEST_ID;

            const decryptedToken = this.decryptToken(token, iv);

            console.log("Successfully decrypted token");

            this.setState({
                token: decryptedToken.token,
                iv: decryptedToken.iv,
                requestId: requestId
            });

            const authData = this.encryptLoginAndPassword();

            console.log("Successfully encrypted creds");

            const authResponse = await fetch("http://localhost:9003/api/auth", {
                headers: {
                    'Content-Type': 'application/json',
                    'REQUEST_ID': this.state.requestId
                },
                method: "POST",
                credentials: "include",
                body: JSON.stringify(authData)
            })

            const authJson = await authResponse.json();
            requestId = authJson.attributes.REQUEST_ID;

            this.setState({
                requestId: requestId
            })

            const responseCode = authResponse.status

            if (responseCode === 200) {
                alert(authJson.attributes.message)
                this.setState({
                    credentialsVerified: true
                });
            } else {
                alert(authJson.attributes.message)
            }

            console.log("Login finished with " + responseCode + " code")

        } else {
            alert('No public key generated');

        }

    }
    /* --- */

    /* --- Handling input's value changes --- */
    handleValueChange(event) {
        event.preventDefault();
        const inputName = event.target.name;
        const inputValue = event.target.value;

        const user = this.state.loggedUser;

        user[inputName] = inputValue;

        console.log(inputName + ": " + user[inputName])

        this.setState({
            loggedUser: user
        });
    }

    handleFileChange(event) {
        event.preventDefault();
        const inputName = event.target.name;
        const inputValue = event.target.value;

        const file = this.state.file;

        file[inputName] = inputValue;

        console.log(inputName + ":\n " + file[inputName])

        this.setState({
            file: file
        });
    }

    handleFileNameChange(event) {
        event.preventDefault();
        const inputName = event.target.name;
        const inputValue = event.target.value;

        let file = this.state.file;

        file[inputName] = inputValue;

        this.setState({
            file: file
        });
    }
    /* --- */

    /* --- RSA keys generation and RSA decryption --- */
    generateKeys() {

        const publicKey = this.state.rsa.exportKey("pkcs1-public-pem");
        const privateKey = this.state.rsa.exportKey("pkcs8-private-pem");

        this.setState({
            publicKey: publicKey,
            privateKey: privateKey
        })
    }

    decryptToken(token, iv) {
        const decryptedToken = {
            token: this.state.rsa.decrypt(token, 'utf8'),
            iv: this.state.rsa.decrypt(iv, 'utf8')
        }

        return decryptedToken;
    }
    /* --- */

    /* --- AES CBC encryption and decryption --- */
    encryptLoginAndPassword() {
        let login = this.state.loggedUser.login;
        let password = this.state.loggedUser.password;
        let key = this.state.token;
        let iv = this.state.iv;

        login = encryptAES_CBC(key, iv, login);
        password = encryptAES_CBC(key, iv, password);

        const encryptedCreds = {
            login: login,
            password: password
        };

        return encryptedCreds;
    }

    decryptAES(text) {

        let key = this.state.token;
        let iv = this.state.iv;

        const dect = decryptAES_CBC(key, iv, text)

        return dect;
    }

    encryptAES(text) {

        let key = this.state.token;
        let iv = this.state.iv;

        const encr = encryptAES_CBC(key, iv, text);

        return encr;
    }
    /* --- */

    /* --- Email verification --- */

    async sendEmail() {

        const encryptedEmail = this.encryptAES(this.state.loggedUser.email);

        const body = {
            email: encryptedEmail
        }

        const response = await fetch("http://localhost:9003/api/two_factor/email-verification", {
            headers: {
                'Content-Type': 'application/json',
                'REQUEST_ID': this.state.requestId
            },
            method: "POST",
            credentials: "include",
            body: JSON.stringify(body)
        })

        if (response.status === 400) {
            alert("Unable to send message. You probably have entered invalid E-Mail address")
            return;
        }

        if (response.status === 200) {
            const json = await response.json();

            const requestId = json.attributes.requestId;

            this.setState({
                requestId: requestId,
                emailSent: true
            })

            alert("Verification code has been sent to your E-Mail address")
            return;
        }

    }

    async sendVerificationCode() {

        const encryptedVerificationCode = this.encryptAES(this.state.loggedUser.verificationCode);

        const body = {
            verificationCode: encryptedVerificationCode
        }

        const response = await fetch("http://localhost:9003/api/two_factor/code-validation", {
            headers: {
                'Content-Type': 'application/json',
                'REQUEST_ID': this.state.requestId
            },
            method: "POST",
            credentials: "include",
            body: JSON.stringify(body)
        })

        if (response.status === 400) {
            alert("You have entered wrong verification code")
            this.setState({
                verificationCode: null
            })
            return;
        }

        if (response.status === 200) {
            const json = await response.json();

            const requestId = json.attributes.requestId;

            this.setState({
                requestId: requestId,
                emailVerified: true,
                loggedIn: true
            })

            alert("Successfully verified");
            return;

        }
    }

    /* --- */

    render() {

        const rsaPublicKey = this.state.publicKey;

        return (
            <div className="container">

                <div className="row">

                    <Switch>
                        <Route exact path="/">
                            {
                                this.state.emailVerified ? <Redirect to="/form/file" /> :
                                    this.state.credentialsVerified ? <Redirect to="/email-verification"/> :
                                    <div className="col-md-12">
                                        <div className="py-5 text-center">
                                            <h2>Secure database for text files</h2>
                                            <p className="lead">Authorization</p>
                                        </div>
                                        <Form
                                            onBlur={(event) => this.handleValueChange(event)}
                                            onSubmit={() => this.onSubmit()}
                                            getKeys={() => this.generateKeys()}
                                            rsaKey={rsaPublicKey} />
                                    </div>
                            }
                        </Route>
                        {<Route path="/email-verification">
                            {
                                !this.state.credentialsVerified ? <Redirect to="/" /> :
                                    this.state.emailVerified ? <Redirect to="/form/file" /> :
                                        <div className="col-md-12">
                                            <div className="py-5 text-center">
                                                <h2>Secure database for text files</h2>
                                                <p className="lead">Email verification</p>
                                            </div>
                                            <EmailInput
                                                onBlur={(event) => this.handleValueChange(event)}
                                                onEmailSubmit={() => this.sendEmail()}
                                                onCodeSubmit={() => this.sendVerificationCode()}
                                                emailSent={this.state.emailSent} />
                                        </div>
                            }
                        </Route>}
                        <Route path="/form/file">
                            {
                                <div className="col-md-12">
                                    <div className="py-5 text-center">
                                        <h2>Secure database for text files</h2>
                                        <p className="lead">Requesting file</p>
                                    </div>
                                    <FileForm
                                        file={this.state.file}
                                        onBlur={(event) => this.handleFileChange(event)}
                                        onSubmit={(filename) => this.fetchFile(filename)}
                                        onUpload={(fileName, fileContent) => this.loadFile(fileName, fileContent)}
                                    />
                                </div>
                            }
                        </Route>
                        <Route path="/email-verification">

                        </Route>
                    </Switch>
                </div>
            </div >
        )
    }
}