import React from 'react'

const EmailInput = (props) => {

    if (!props.emailSent) {
        return (
            <div className="row">

                <input
                    className="form-control"
                    onBlur={(event) => props.onBlur(event)}
                    type="email"
                    name="email"
                    pattern=".+@.+"
                    placeholder="Email" />

                <button
                    onClick={() => props.onEmailSubmit()}
                    className="btn btn-info custom-button"
                    type="button">
                    Send me verification code
                </button>
            </div>
        );

    } else {
        return (
            <div className="row">
                <input
                    className="form-control"
                    onBlur={(event) => props.onBlur(event)}
                    type="text"
                    name="verificationCode"
                    defaultValue=""
                    placeholder="Enter code" />

                <button
                    onClick={() => props.onCodeSubmit()}
                    className="btn btn-info custom-button"
                    type="button">
                    Verify code
                </button>
            </div>
        );
    }
}


export default EmailInput;
