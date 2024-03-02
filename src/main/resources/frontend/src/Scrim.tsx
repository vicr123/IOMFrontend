import React from "react";
import ReactDOM from "react-dom";
import Styles from "./Scrim.module.css";

class Scrim extends React.Component {
    render() {
        return <div className={Styles.Scrim} />
    }

    static mount() {
        ReactDOM.render(
            <Scrim />,
            document.getElementById('scrim')
        );
    }

    static unmount() {
        ReactDOM.unmountComponentAtNode(document.getElementById('scrim')!);
    }
}

export default Scrim;