import React from "react";
import Styles from "./newMapItemSelector.module.css"

class NewMapItemSelector extends React.Component {
    constructor(props) {
        super(props);

        this.fileSelector = React.createRef();
    }


    render() {
        return <div className={Styles.SelectorRoot} onClick={this.onClick.bind(this)}>
            <div className={Styles.AddPicture} />
            <input className={Styles.FileSelector} type={"file"} accept={"image/png,image/svg+xml,image/gif,image/webp"} onChange={this.onChange.bind(this)} ref={this.fileSelector} multiple={true} />
        </div>
    }

    onClick() {
        this.fileSelector.current.click();
    }

    onChange(e) {
        this.props.manager.uploadMaps(
            [...e.target.files].filter(file => ["image/png", "image/svg+xml", "image/gif", "image/webp"].includes(file.type)), this.props.category);

        e.target.value = null;
    }
}

export default NewMapItemSelector;