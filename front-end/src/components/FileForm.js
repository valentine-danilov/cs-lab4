import React from 'react'

export default class FileForm extends React.Component {

    render() {

        const file = this.props.file;

        return (
            <div>
                <div className="row"><input
                    onBlur={(event) => this.props.onBlur(event)}
                    className="form-control"
                    type="text"
                    name="fileName"
                    placeholder="Enter fileName" />
                    <button
                        onClick={() => this.props.onSubmit(file.fileName)}
                        className="btn btn-info custom-button"
                        type="submit">
                        Load text
                    </button>
                    <button
                        onClick={() => this.props.onUpload(file.fileName, file.content)}
                        className="btn btn-info custom-button"
                        type="submit">
                        Upload text
                    </button>
                </div>

                <div className="row">
                    <div className="container">
                        <div className="row">

                            <h2>Text</h2>
                            <textarea
                                rows="20"
                                name="content"
                                onBlur={(event) => this.props.onBlur(event)}
                                className="form-control custom-textarea"
                                defaultValue={file.content ? file.content : ""}>
                            </textarea>
                        </div>

                    </div>
                </div>
            </div>
        )
    }

}