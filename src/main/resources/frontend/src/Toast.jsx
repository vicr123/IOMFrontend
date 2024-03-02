import React from "react";
import ReactDOM from "react-dom";
import Styles from "./Toast.module.css";

let toastTimeout = null;

class Toast extends React.Component {
    render() {
        return <div className={Styles.Toast}>
            <span className={Styles.title}>{this.props.title}</span>
            <span className={Styles.text}>{this.props.text}</span>
        </div>
    }

    static makeToast(toast) {
        let node = document.getElementById('toast');
        if (toastTimeout) {
            clearTimeout(toastTimeout);
            ReactDOM.unmountComponentAtNode(node);
        }
        ReactDOM.render(toast, node);

        toastTimeout = setTimeout(() => {
            ReactDOM.unmountComponentAtNode(node);
        }, 5000);
    }
}

export default Toast;