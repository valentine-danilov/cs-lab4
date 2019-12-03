import React from 'react'

const Form = (props) => (
    <div className="mr-25">

        <input
            onBlur={(event) => props.onBlur(event)}
            className="form-control"
            type="text"
            name="login"
            placeholder="Login" />
        <input
            className="form-control"
            onBlur={(event) => props.onBlur(event)}
            type="password"
            name="password"
            placeholder="Password" />
        

        <div className="container">
            <div className="row">
                <button
                    onClick={() => props.onSubmit()}
                    className="btn btn-info custom-button"
                    type="button"
                    name="submit">
                    Log In
                </button>

                <button
                    onClick={() => props.getKeys()}
                    className="btn btn-info custom-button"
                    type="button">
                    Generate Keys
                </button>
            </div>
        </div>

        <div className="container">
            <div className="row">
                <h3>RSA Public Key</h3>
                <textarea rows="5" className="mt-5 form-control keyarea" defaultValue={props.rsaKey ? props.rsaKey : ""}>
                </textarea>
            </div>
        </div>


    </div>
)

export default Form;